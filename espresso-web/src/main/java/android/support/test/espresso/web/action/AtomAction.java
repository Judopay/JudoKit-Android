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
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.core.deps.guava.base.Function;
import android.support.test.espresso.core.deps.guava.util.concurrent.Futures;
import android.support.test.espresso.core.deps.guava.util.concurrent.ListenableFuture;
import android.support.test.espresso.core.deps.guava.util.concurrent.MoreExecutors;
import android.support.test.espresso.core.deps.guava.util.concurrent.SettableFuture;
import android.support.test.espresso.web.model.Atom;
import android.support.test.espresso.web.model.ElementReference;
import android.support.test.espresso.web.model.Evaluation;
import android.support.test.espresso.web.model.WindowReference;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import org.hamcrest.Matcher;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkState;
import static android.support.test.espresso.matcher.ViewMatchers.isJavascriptEnabled;

/**
 * A ViewAction which causes the provided Atom to be evaluated within a webview.
 *
 * <p>It is not recommended to use AtomAction directly.
 * <p>Instead {@see android.support.test.espresso.web.sugar.Web} for
 * examples of how to interact with a WebView's content through Atoms.
 * <p>If you must use AtomAction directly, take care to remember that they are Stateful
 * (unlike most ViewActions) and the caller must call {@ #get()} to ensure that the action
 * has completed.
 *
 * @param <E> The type the specific Atom returns.
 */
public final class AtomAction<E> implements ViewAction {
  private static final String TAG = "AtomAction";
  private final SettableFuture<Evaluation> futureEval = SettableFuture.create();
  private final Atom<E> atom;
  @Nullable
  private final WindowReference window;
  @Nullable
  private final ElementReference element;

  /**
   * Creates an AtomAction.
   *
   * @param atom the atom to execute
   * @param window (optional/nullable) the window context to execute on.
   * @param element (optional/nullable) the element to execute on.
   */
  public AtomAction(Atom<E> atom, @Nullable WindowReference window,
      @Nullable ElementReference element) {
    this.atom = checkNotNull(atom);
    this.window = window;
    this.element = element;
  }

  @Override
  public Matcher<View> getConstraints() {
    return isJavascriptEnabled();
  }

  @Override
  public String getDescription() {
    return String.format("Evaluate Atom: %s in window: %s with element: %s",
        atom, window, element);
  }

  @Override
  public void perform(UiController controller, View view) {
    WebView webView = (WebView) view;
    List<? extends Object> arguments = checkNotNull(atom.getArguments(element));
    String script = checkNotNull(atom.getScript());
    final ListenableFuture<Evaluation> localEval = JavascriptEvaluation.evaluate(
        webView, script, arguments, window);
    if (null != window && Build.VERSION.SDK_INT == 19) {
      Log.w(TAG, "WARNING: KitKat does not report when an iframe is loading new content. "
          + "If you are interacting with content within an iframe and that content is changing ("
          + "eg: you have just pressed a submit button). Espresso will not be able to block you "
          + "until the new content has loaded (which it can do on all other API levels). You will "
          + "need to have some custom polling / synchronization with the iframe in that case.");
    }

    localEval.addListener(
        new Runnable() {
          @Override
          public void run() {
            try {
              futureEval.set(localEval.get());
            } catch (ExecutionException ee) {
              futureEval.setException(ee.getCause());
            } catch (InterruptedException ie) {
              futureEval.setException(ie);
            }
          }
        }, MoreExecutors.sameThreadExecutor());
  }

  /**
   * Return a Future, which will be set and transformed from futureEval.
   * Espresso's public API cannot have guava types in its method signatures, so return Future
   * instead of ListenableFuture or SettableFuture.
   */
  public Future<E> getFuture() {
    return Futures.transform(futureEval, new Function<Evaluation, E>() {
      @Override
      public E apply(Evaluation e) {
        return atom.transform(e);
      }
    });
  }

  /**
   * Blocks until the atom has completed execution.
   */
  public E get() throws ExecutionException, InterruptedException {
    checkState(Looper.myLooper() != Looper.getMainLooper(), "On main thread!");
    return getFuture().get();
  }

  /**
   * Blocks until the atom has completed execution with a configurable timeout.
   */
  public E get(long val, TimeUnit unit) 
    throws ExecutionException, InterruptedException, TimeoutException {
    checkState(Looper.myLooper() != Looper.getMainLooper(), "On main thread!");
    return getFuture().get(val, unit);
  }

}
