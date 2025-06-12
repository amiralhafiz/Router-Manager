package com.example.routermanager

interface SpeedTester {
    suspend fun downloadSpeed(url: String, onProgress: (Int) -> Unit): Double
}
