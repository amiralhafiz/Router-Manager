package com.example.routermanager

import android.view.View
import androidx.annotation.IdRes
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.routermanager.BuildConfig
import com.example.routermanager.SpeedTester
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpeedTestActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(SpeedTestActivity::class.java)

    private lateinit var fakeTester: FakeSpeedTester

    @org.junit.Before
    fun setUp() {
        fakeTester = FakeSpeedTester()
        activityRule.scenario.onActivity { activity ->
            activity.tester = fakeTester
        }
    }

    @Test
    fun runButtonShowsProgress() {
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.runTestButton)).perform(click())

        val showResource = ProgressVisibilityIdlingResource(
            activityRule.scenario,
            R.id.loadingProgress,
            View.VISIBLE
        )
        IdlingRegistry.getInstance().register(showResource)
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        IdlingRegistry.getInstance().unregister(showResource)

        val hideResource = ProgressVisibilityIdlingResource(
            activityRule.scenario,
            R.id.loadingProgress,
            View.GONE
        )
        IdlingRegistry.getInstance().register(hideResource)
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        IdlingRegistry.getInstance().unregister(hideResource)
    }

    @Test
    fun toggleFabTogglesButtonsVisibility() {
        onView(withId(R.id.buttonContainer))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.toggleFab)).perform(click())

        onView(withId(R.id.buttonContainer))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.toggleFab)).perform(click())

        onView(withId(R.id.buttonContainer))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun progressBarHidesAfterTestCompletes() {
        onView(withId(R.id.runTestButton)).perform(click())

        val showResource = ProgressVisibilityIdlingResource(
            activityRule.scenario,
            R.id.loadingProgress,
            View.VISIBLE
        )
        IdlingRegistry.getInstance().register(showResource)
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        IdlingRegistry.getInstance().unregister(showResource)

        val hideResource = ProgressVisibilityIdlingResource(
            activityRule.scenario,
            R.id.loadingProgress,
            View.GONE
        )
        IdlingRegistry.getInstance().register(hideResource)
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        IdlingRegistry.getInstance().unregister(hideResource)
    }

    @Test
    fun progressBarHidesAndErrorShownOnFailure() {
        activityRule.scenario.onActivity { activity ->
            activity.tester = FakeSpeedTester(shouldFail = true)
        }

        onView(withId(R.id.runTestButton)).perform(click())

        val hideResource = ProgressVisibilityIdlingResource(
            activityRule.scenario,
            R.id.loadingProgress,
            View.GONE
        )
        IdlingRegistry.getInstance().register(hideResource)
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        IdlingRegistry.getInstance().unregister(hideResource)

        onView(withId(R.id.downloadText))
            .check(matches(withText(R.string.speed_test_failed)))
    }

    @Test
    fun usesConfiguredTestUrl() {
        onView(withId(R.id.runTestButton)).perform(click())

        val hideResource = ProgressVisibilityIdlingResource(
            activityRule.scenario,
            R.id.loadingProgress,
            View.GONE
        )
        IdlingRegistry.getInstance().register(hideResource)
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        IdlingRegistry.getInstance().unregister(hideResource)

        assertEquals(BuildConfig.TEST_FILE_URL, fakeTester.lastUrl)
    }
}

private class FakeSpeedTester(
    private val shouldFail: Boolean = false
) : SpeedTester {
    var lastUrl: String? = null
    override suspend fun downloadSpeed(
        url: String,
        onProgress: (Int) -> Unit,
    ): Double {
        lastUrl = url
        delay(100)
        onProgress(50)
        delay(100)
        if (shouldFail) {
            throw RuntimeException("boom")
        }
        onProgress(100)
        return 42.0
    }
}

private class ProgressVisibilityIdlingResource(
    private val scenario: ActivityScenario<*>,
    @IdRes private val viewId: Int,
    private val expectedVisibility: Int,
) : IdlingResource {
    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String =
        "ProgressVisibilityIdlingResource:$viewId:$expectedVisibility"

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

