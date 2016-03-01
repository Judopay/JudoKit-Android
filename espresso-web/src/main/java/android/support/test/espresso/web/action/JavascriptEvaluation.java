/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.test.espresso.web.action;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.test.espresso.core.deps.guava.base.Function;
import android.support.test.espresso.core.deps.guava.util.concurrent.AbstractFuture;
import android.support.test.espresso.core.deps.guava.util.concurrent.AsyncFunction;
import android.support.test.espresso.core.deps.guava.util.concurrent.Futures;
import android.support.test.espresso.core.deps.guava.util.concurrent.ListenableFuture;
import android.support.test.espresso.core.deps.guava.util.concurrent.MoreExecutors;
import android.support.test.espresso.web.bridge.Conduit;
import android.support.test.espresso.web.bridge.JavaScriptBridge;
import android.support.test.espresso.web.model.Evaluation;
import android.support.test.espresso.web.model.ModelCodec;
import android.support.test.espresso.web.model.WindowReference;
import android.webkit.ValueCallback;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkState;

/**
 * Wraps scripts into WebDriver atoms, which are used to ensure consistent behaviour cross-browser.
 */
final class JavascriptEvaluation {
  private JavascriptEvaluation() {}
  private static final ScriptPreparer SCRIPT_PREPARER;
  private static final AsyncFunction<PreparedScript, String> RAW_EVALUATOR;
  private static final Function<String, Evaluation> DECODE_EVALUATION =
      new Function<String, Evaluation>() {
        @Override
        public Evaluation apply(String in) {
          return ModelCodec.decodeEvaluation(in);
        }
      };

  private static final int SANITIZER_SYNC = 1;
  private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper()) {

    @Override
    public void handleMessage(Message m) {
      switch(m.what) {
        case SANITIZER_SYNC:
          ((SanitizerTask) m.obj).sanitizerSync();
          break;
      }
    }
  };

  static {
    if (Build.VERSION.SDK_INT < 19) {
      SCRIPT_PREPARER = new ScriptPreparer(true);
      RAW_EVALUATOR = new AsyncConduitEvaluation();
    } else {
      SCRIPT_PREPARER = new ScriptPreparer(false);
      RAW_EVALUATOR = new AsyncJavascriptEvaluation();
    }
  }

  /**
   * Evaluates a script on a given WebView.
   *
   * Scripts are only evaluated when a WebView is deemed sane. That is:
   * <ul>
   * <li>The WebView's back/forward list's last item agrees with the WebView</li>
   * <li>The WebView's reported content height is non-zero</li>
   * <li>The WebView's reported progress is 100</li>
   * <li>The document.documentElement object for the DOM of the selected window is non-null</li>
   * <ul>
   *
   * Scripts are evaluated on the WebKit/Chromium thread (that is - not the Main thread).
   * A Future is returned which contains the result of the evaluation.
   */
  static ListenableFuture<Evaluation> evaluate(final WebView view, final String script,
                                               final List<? extends Object> arguments, @Nullable final WindowReference window) {
    UnpreparedScript unprepared = new UnpreparedScript(view, script, arguments, window);
    SanitizerTask sanitizer = new SanitizerTask(unprepared);
    view.post(sanitizer);
    ListenableFuture<PreparedScript> preparedScript = Futures.transform(sanitizer, SCRIPT_PREPARER);
    ListenableFuture<String> rawEvaluation = Futures.transform(preparedScript, RAW_EVALUATOR);
    ListenableFuture<Evaluation> parsedEvaluation = Futures.transform(rawEvaluation,
        DECODE_EVALUATION);
    return parsedEvaluation;
  }

  /**
   * Ensures the WebView meetings minimum sanity guidelines.
   */
  private static class SanitizerTask extends AbstractFuture<UnpreparedScript> implements Runnable {
    private static final String DOC_ELEMENT_PRESENT =
        "return document.documentElement != null && document.readyState === 'complete'";
    private static final int DELAY = 100;
    private final UnpreparedScript unprepared;
    private String sanityMessage = "";
    private int count;

    public SanitizerTask(UnpreparedScript unprepared) {
      this.unprepared = checkNotNull(unprepared);
      count = 0;
    }

    @Override
    public void run() {
      if (Looper.myLooper() != Looper.getMainLooper()) {
        unprepared.view.post(this);
      } else {
        try {
          innerSanity();
        } catch (RuntimeException re) {
          setException(re);
        }
      }
    }

    void sanitizerSync() {
      if (isWebViewSane()) {
        set(unprepared);
      } else {
        // try again!
        unprepared.view.post(this);
      }
    }


    private void innerSanity() {
      count++;
      checkState(count < 250,
          "Waited over: %s millis but webview never went sane: %s",
          250 * DELAY,
          sanityMessage);

      if (isWebViewSane()) {
        PreparedScript docCheckScript = SCRIPT_PREPARER.apply(new UnpreparedScript(
            unprepared.view, DOC_ELEMENT_PRESENT, Collections.EMPTY_LIST, unprepared.window));
        ListenableFuture<String> futureRaw = null;
        try {
          futureRaw = RAW_EVALUATOR.apply(docCheckScript);
        } catch (Exception e) {
          setException(e);
          return;
        }
        final ListenableFuture<Evaluation> futureParsed = Futures.transform(futureRaw,
            DECODE_EVALUATION);

        futureParsed.addListener(new Runnable() {
          @Override
          public void run() {
            try {
              Evaluation eval = futureParsed.get();
              if (eval.getStatus() == 0) {
                if ((Boolean) eval.getValue()) {
                  if (Build.VERSION.SDK_INT == 10) {
                    set(unprepared);
                  } else {
                    // webview seems ready, but force it to respond to a requestFocusNodeHref call
                    // and check if it is still sane after the response.
                    // This works around flakes in API 15 where progress updates may not be sent
                    // without a requestFocusNodeHref call.
                    unprepared.view.post(new Runnable() {
                      @Override
                      public void run() {
                        unprepared.view.requestFocusNodeHref(
                            MAIN_HANDLER.obtainMessage(SANITIZER_SYNC, SanitizerTask.this));
                      }
                    });
                  }
                } else {
                  unprepared.view.postDelayed(SanitizerTask.this, DELAY);
                }
              } else {
                setException(
                    new RuntimeException("Fatal exception checking document state: " + eval));
              }
            } catch (ExecutionException ee) {
              setException(ee.getCause());
            } catch (InterruptedException ie) {
              setException(ie.getCause());
            }
          }
        }, MoreExecutors.sameThreadExecutor());

      } else {
        unprepared.view.postDelayed(this, DELAY);
      }
    }

    private boolean isWebViewSane() {
      String url = unprepared.view.getUrl();
      WebHistoryItem current = unprepared.view.copyBackForwardList().getCurrentItem();
      boolean getUrlReady = url != null;
      boolean webHistoryReady = current != null;


      if (getUrlReady && webHistoryReady) {
        String historyUrl = current.getUrl();
        boolean viewAndHistoryMatch = url.equals(historyUrl);
        boolean nonZeroContentHeight = unprepared.view.getContentHeight() != 0;
        boolean progressComplete = unprepared.view.getProgress() == 100;
        sanityMessage = String.format(
          "viewAndHistoryUrlsMatch: %s, nonZeroContentHeight: %s, progressComplete: %s",
          viewAndHistoryMatch, nonZeroContentHeight, progressComplete);

        return viewAndHistoryMatch && progressComplete && nonZeroContentHeight;
      } else {
        sanityMessage = String.format(
          "view.getUrl() != null: %s view.copyBackForwardList().getCurrentItem() != null: %s",
          getUrlReady, webHistoryReady);

      }
      return false;
    }
  }

  /**
   * Contains the raw script, it's arguments, and the webview to run it against.
   */
  private static class UnpreparedScript {
    private final WebView view;
    private final String script;
    private final List<? extends Object> args;
    @Nullable
    private final WindowReference window;

    UnpreparedScript(WebView view, String script, List<? extends Object> args,
        @Nullable WindowReference window) {
      this.view = checkNotNull(view);
      this.script = checkNotNull(script);
      this.args = checkNotNull(args);
      this.window = window;
    }
  }

  /**
   * Contains a script which has been wrapped with the EXECUTE_SCRIPT atom, has been properly
   * escaped, and potentially conduitized.
   */
  private static class PreparedScript {
    private final WebView view;
    private final String script;

    @Nullable
    private final Conduit conduit;
    PreparedScript(WebView view, String script, @Nullable Conduit conduit) {
      this.view = checkNotNull(view);
      this.script = checkNotNull(script);
      this.conduit = conduit;
    }
  }

  private static final class ScriptPreparer implements Function<UnpreparedScript, PreparedScript> {
    private final boolean conduitize;
    public ScriptPreparer(boolean conduitize) {
      this.conduitize = conduitize;
    }

    @Override
    public PreparedScript apply(UnpreparedScript unprepared) {
      StringBuilder atomized = atomize(unprepared.script, unprepared.args, unprepared.window);
      Conduit conduit = null;
      if (conduitize) {
        conduit = JavaScriptBridge.makeConduit();
        atomized = conduit.wrapScriptInConduit(atomized)
            .insert(0, "javascript:");
      }
      return new PreparedScript(unprepared.view, atomized.toString(), conduit);
    }

    private StringBuilder atomize(String script, List<? extends Object> args,
        WindowReference windowReference) {
      int guessedSize = EvaluationAtom.EXECUTE_SCRIPT_ANDROID.length() + script.length() + 1024;
      if (windowReference != null) {
        guessedSize += EvaluationAtom.GET_ELEMENT_ANDROID.length();
      }
      StringBuilder toExecute = new StringBuilder(guessedSize)
        .append("var my_wind = ");
      if (windowReference != null) {
        toExecute.append("(")
            .append(EvaluationAtom.GET_ELEMENT_ANDROID)
            .append(")(")
            .append(ModelCodec.encode(windowReference))
            .append("[\"WINDOW\"]);");
      } else {
        toExecute.append("null;");
      }
      toExecute
          .append("return (").append(EvaluationAtom.EXECUTE_SCRIPT_ANDROID).append(")(");
      escapeAndQuote(toExecute, script).append(",")
          .append(ModelCodec.encode(args))
          .append(",")
          .append(conduitize) // JSON.stringify at webdriver level. Necessary for conduits.
          .append(",")
          .append("my_wind)");
      return wrapInFunction(toExecute);
    }

    private StringBuilder wrapInFunction(StringBuilder script) {
      script.insert(0, "(function(){")
          .append("})()");
      return script;
    }

    private static final Pattern FUNCTION_PATTERN = Pattern.compile(
        "^\\s*function\\s*\\w*\\s*\\(.*\\}\\s*$" , Pattern.DOTALL | Pattern.MULTILINE);

    static boolean isFunctionDefinition(String script) {
      return FUNCTION_PATTERN.matcher(script).matches();
    }

    private StringBuilder escapeAndQuote(StringBuilder scriptBuffer, String toWrap) {
      scriptBuffer.append("\"");
      boolean isFunction = isFunctionDefinition(toWrap);
      if (isFunction) {
        scriptBuffer.append("return (");
      }

      for (int i = 0; i < toWrap.length(); i++) {
        char c = toWrap.charAt(i);
        switch (c) {
          case '\"':  // literally: "
          case '\'':  // literally: '
          case '\\':  // literally: \
            scriptBuffer.append('\\')
                .append(c);
            break;
          case '\n':  // literally a unix-newline.
            scriptBuffer.append("\\n");
            break;
          case '\r':
            scriptBuffer.append("\\r");
            break;
          case '\u2028':
            scriptBuffer.append("\\u2028");
            break;
          case '\u2029':
            scriptBuffer.append("\\u2029");
            break;
          default:
            scriptBuffer.append(c);
        }
      }
      if (isFunction) {
        scriptBuffer.append(").apply(null,arguments);");
      }

      scriptBuffer.append("\"");
      return scriptBuffer;
    }
  }

  private static final class AsyncConduitEvaluation implements
      AsyncFunction<PreparedScript, String> {
        @Override
        public ListenableFuture<String> apply(final PreparedScript in) {

          if (null == in.conduit) {
            return Futures.<String>immediateFailedFuture(
                new RuntimeException("Not a conduit script!"));
          } else {
            if (Looper.myLooper() == Looper.getMainLooper()) {
              in.view.loadUrl(in.script);
            } else {
              in.view.post(new Runnable() {
                @Override
                public void run() {
                  in.view.loadUrl(in.script);
                }
              });
            }
            return in.conduit.getResult();
          }
        }
      };

  private static final class AsyncJavascriptEvaluation implements
      AsyncFunction<PreparedScript, String> {
        @Override
        public ListenableFuture<String> apply(final PreparedScript in) {
          if (null != in.conduit) {
            return Futures.<String>immediateFailedFuture(
                new RuntimeException("Conduit script cannot be used"));
          } else {
            final ValueCallbackFuture<String> result = new ValueCallbackFuture<String>();
            if (Looper.myLooper() == Looper.getMainLooper()) {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                in.view.evaluateJavascript(in.script, result);
              }
            } else {
              in.view.post(new Runnable() {
                @Override
                public void run() {
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    in.view.evaluateJavascript(in.script, result);
                  }
                }
              });
            }
            return result;
          }
        }
      }

  private static class ValueCallbackFuture<V> extends AbstractFuture<V>
      implements ValueCallback<V> {
    @Override
    public void onReceiveValue(V value) {
      set(value);
    }
  }
}
