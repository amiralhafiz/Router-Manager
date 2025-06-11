package com.example.routermanager

import android.webkit.WebView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
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
        prefs.edit().putString("routerUrl", "https://10.80.80.1/").commit()
    }

    @Test
    fun webViewLoadsExpectedUrl() {
        onView(withId(R.id.routerWebView)).check { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            val webView = view as WebView
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            assertEquals("https://10.80.80.1/", webView.url)
        }
    }
}
