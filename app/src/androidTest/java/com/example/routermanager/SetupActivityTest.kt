package com.example.routermanager

import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.Visibility
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
        // Wait for the asynchronous network detection to complete
        Thread.sleep(3000)
        onView(withId(R.id.urlEditText)).check(matches(not(isEnabled())))
    }

    @Test
    fun progressBarShowsAndHidesDuringScan() {
        onView(withId(R.id.setupProgress))
            .check(matches(isDisplayed()))

        Thread.sleep(3000)

        onView(withId(R.id.setupProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}
