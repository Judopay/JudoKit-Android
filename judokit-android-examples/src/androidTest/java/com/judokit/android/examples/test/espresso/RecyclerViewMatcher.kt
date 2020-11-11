package com.judokit.android.examples.test.espresso

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(private val recyclerViewId: Int) {

    fun atPosition(position: Int): Matcher<View?>? {
        return atPositionOnView(position, -1)
    }

    private fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View?>? {
        return object : TypeSafeMatcher<View?>() {
            var resources: Resources? = null
            var childView: View? = null
            override fun describeTo(description: Description?) {
                val idDescription: String = try {
                    resources?.getResourceName(recyclerViewId)
                        ?: throw Resources.NotFoundException()
                } catch (e: Resources.NotFoundException) {
                    String.format(
                        "%s (resource name not found)",
                        Integer.valueOf(recyclerViewId)
                    )
                }
                description?.appendText("with id: $idDescription")
            }

            override fun matchesSafely(view: View?): Boolean {
                resources = view?.resources
                val recyclerView =
                    view?.rootView?.findViewById(recyclerViewId) as RecyclerView
                childView = if (recyclerView.id == recyclerViewId) {
                    recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                } else {
                    return false
                }
                return if (targetViewId == -1) {
                    view == childView
                } else {
                    val targetView: View =
                        childView?.findViewById(targetViewId) ?: throw Resources.NotFoundException()
                    view == targetView
                }
            }
        }
    }
}
