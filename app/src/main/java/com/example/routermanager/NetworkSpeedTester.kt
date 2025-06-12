package com.example.routermanager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class NetworkSpeedTester(private val client: OkHttpClient = OkHttpClient()) : SpeedTester {
    override suspend fun downloadSpeed(url: String, onProgress: (Int) -> Unit): Double =
        withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                val body = response.body ?: return@withContext 0.0
                val length = body.contentLength()
                val input = body.byteStream()
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytesRead = 0L
                val start = System.nanoTime()
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    bytesRead += read
                    if (length > 0) {
                        val percent = (bytesRead * 100 / length).toInt()
                        withContext(Dispatchers.Main) {
                            onProgress(percent)
                        }
                    }
                }
                val time = (System.nanoTime() - start) / 1_000_000_000.0
                if (time == 0.0) 0.0 else (bytesRead * 8 / 1_000_000.0) / time
            }
        }

    suspend fun uploadSpeed(url: String, file: File): Double =
        withContext(Dispatchers.IO) {
            val requestBody: RequestBody = file.asRequestBody("application/octet-stream".toMediaType())
            val request = Request.Builder().url(url).post(requestBody).build()
            val bytes = file.length()
            val start = System.nanoTime()
            client.newCall(request).execute().use { }
            val time = (System.nanoTime() - start) / 1_000_000_000.0
            if (time == 0.0) 0.0 else (bytes * 8 / 1_000_000.0) / time
        }
}
