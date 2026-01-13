package com.example.routermanager

import android.content.Context
import android.content.SharedPreferences

val Context.settingsPrefs: SharedPreferences
    get() = getSharedPreferences("settings", Context.MODE_PRIVATE)
