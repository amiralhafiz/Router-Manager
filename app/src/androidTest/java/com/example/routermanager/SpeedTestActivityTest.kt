package com.example.routermanager

import android.webkit.WebView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpeedTestActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(SpeedTestActivity::class.java)

    @Test
    fun webViewLoadsExpectedUrl() {
        onView(withId(R.id.speedTestWebView)).check { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            val webView = view as WebView
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            assertEquals("https://www.speedtest.net/", webView.url)
        }
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
    fun progressBarTogglesOnPageLoad() {
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        Thread.sleep(3000)

        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}

