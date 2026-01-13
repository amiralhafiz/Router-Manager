package com.example.routermanager

import android.app.Application
import com.google.android.material.color.DynamicColors

class RouterManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
