package com.judopay.judokit.android.ui.cardentry.components

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.common.heightWithInsetsAndMargins

internal fun updateAppBarsOnScrollChange(
    topAppBar: AppBarLayout,
    bottomAppBar: BottomAppBar,
    scrollView: NestedScrollView,
) {
    bottomAppBar.elevation =
        if (ViewCompat.canScrollVertically(scrollView, 1)) {
            scrollView.resources.getDimension(R.dimen.elevation_4)
        } else {
            scrollView.resources.getDimension(R.dimen.elevation_0)
        }

    topAppBar.outlineProvider =
        if (ViewCompat.canScrollVertically(scrollView, -1)) {
            ViewOutlineProvider.PADDED_BOUNDS
        } else {
            null
        }
}

internal fun waitForAllLaidOut(
    vararg views: View,
    onAllLaidOut: () -> Unit,
) {
    var remaining = views.size
    views.forEach { view ->
        view.doOnLayout {
            remaining--
            if (remaining == 0) onAllLaidOut()
        }
    }
}

internal fun adjustContainerLayoutMargins(
    container: ConstraintLayout,
    topAppBar: AppBarLayout,
    bottomAppBar: BottomAppBar,
) {
    waitForAllLaidOut(container, topAppBar, bottomAppBar) {
        container.post {
            val params = container.layoutParams as MarginLayoutParams
            params.bottomMargin = bottomAppBar.heightWithInsetsAndMargins
            params.topMargin = topAppBar.heightWithInsetsAndMargins
            container.layoutParams = params
        }
    }
}
