package com.example.routermanager

import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
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
}
