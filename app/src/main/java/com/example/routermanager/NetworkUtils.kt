package com.example.routermanager

import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtils {
    fun isPortOpen(host: String, port: Int, timeoutMs: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeoutMs)
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
