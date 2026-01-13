package com.example.routermanager

import android.content.Context
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebViewSavedUrlTest {

    @Test
    fun webViewLoadsSavedRouterUrl() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString(PrefsKeys.KEY_ROUTER_URL, "http://192.168.1.1/").commit()

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.routerWebView)).check { view, noViewFoundException ->
                if (noViewFoundException != null) throw noViewFoundException
                val webView = view as WebView
                InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                assertEquals("http://192.168.1.1/", webView.url)
            }
        }
    }
}
