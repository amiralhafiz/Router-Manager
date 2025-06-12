package com.example.routermanager

import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.annotation.IdRes
import android.view.View
import androidx.test.core.app.ActivityScenario
import org.hamcrest.Matchers.not
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(SetupActivity::class.java)

    @org.junit.Before
    fun clearPrefs() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun setupScreenShowsBigIcon() {
        onView(withId(R.id.routerImageView)).check(matches(isDisplayed()))
    }

    @Test
    fun urlFieldRemainsDisabledAfterScan() {
        val resource = ProgressGoneIdlingResource(activityRule.scenario, R.id.setupProgress)
        IdlingRegistry.getInstance().register(resource)
        onView(withId(R.id.urlEditText)).check(matches(not(isEnabled())))
        IdlingRegistry.getInstance().unregister(resource)
    }

    @Test
    fun progressBarShowsAndHidesDuringScan() {
        onView(withId(R.id.setupProgress))
            .check(matches(isDisplayed()))

        val resource = ProgressGoneIdlingResource(activityRule.scenario, R.id.setupProgress)
        IdlingRegistry.getInstance().register(resource)

        onView(withId(R.id.setupProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        IdlingRegistry.getInstance().unregister(resource)
    }
}

private class ProgressGoneIdlingResource(
    private val scenario: ActivityScenario<*>,
    @IdRes private val viewId: Int
) : IdlingResource {
    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "ProgressGoneIdlingResource:$viewId"

    override fun isIdleNow(): Boolean {
        var idle = false
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(viewId)
            idle = view.visibility == View.GONE
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
