package com.example.routermanager

import android.webkit.WebView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.IdlingRegistry
import android.view.View
import com.example.routermanager.ViewVisibilityIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @org.junit.Before
    fun setupPrefs() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        prefs.edit().putString("routerUrl", "https://10.80.80.1/").commit()
    }

    @Test
    fun webViewLoadsExpectedUrl() {
        onView(withId(R.id.routerWebView)).check { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            val webView = view as WebView
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val expectedUrl = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getString("routerUrl", "https://10.80.80.1/")
            assertEquals(expectedUrl, webView.url)
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

        val visibleResource = ViewVisibilityIdlingResource(
            activityRule.scenario,
            R.id.loadingProgress,
            View.VISIBLE
        )
        IdlingRegistry.getInstance().register(visibleResource)
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        IdlingRegistry.getInstance().unregister(visibleResource)

        val goneResource = ViewVisibilityIdlingResource(
            activityRule.scenario,
            R.id.loadingProgress,
            View.GONE
        )
        IdlingRegistry.getInstance().register(goneResource)
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        IdlingRegistry.getInstance().unregister(goneResource)
    }
}
