package com.example.routermanager

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpeedTestActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(SpeedTestActivity::class.java)

    @Test
    fun runButtonShowsProgress() {
        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.runTestButton)).perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
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

        Thread.sleep(1000)

        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        Thread.sleep(5000)

        onView(withId(R.id.loadingProgress))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}

