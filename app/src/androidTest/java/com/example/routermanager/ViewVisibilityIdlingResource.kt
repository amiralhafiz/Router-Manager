package com.example.routermanager

import android.view.View
import androidx.annotation.IdRes
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingResource

class ViewVisibilityIdlingResource(
    private val scenario: ActivityScenario<*>,
    @IdRes private val viewId: Int,
    private val expectedVisibility: Int
) : IdlingResource {
    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "ViewVisibilityIdlingResource:$viewId:$expectedVisibility"

    override fun isIdleNow(): Boolean {
        var idle = false
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(viewId)
            idle = view.visibility == expectedVisibility
        }
        if (idle) {
            callback?.onTransitionToIdle()
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}
