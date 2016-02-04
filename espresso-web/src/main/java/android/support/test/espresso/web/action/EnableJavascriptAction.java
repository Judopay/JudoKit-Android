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
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import android.webkit.WebView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Forcibly enables Javascript on a WebView.
 *
 * <p>This has side-effects callers should be aware of:
 *
 * <ul>
 * <li>On Jellybean and below - the WebView will be reloaded.</li>
 * <li>Javascript will be enabled for the rest of the view's lifetime </li>
 * </ul>
 */
public class EnableJavascriptAction implements ViewAction {

  @Override
  public Matcher<View> getConstraints() {
    return isAssignableFrom(WebView.class);
  }

  @Override
  public String getDescription() {
    return "Forcibly enable javascript.";
  }

  @Override
  public void perform(UiController controller, View view) {
    WebView webView = (WebView) view;
    if (!webView.getSettings().getJavaScriptEnabled()) {
      webView.getSettings().setJavaScriptEnabled(true);
      if (Build.VERSION.SDK_INT < 19) {
        webView.reload();
      }
    }
  }
}
