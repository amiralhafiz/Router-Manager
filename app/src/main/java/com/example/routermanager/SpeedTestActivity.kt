package com.example.routermanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.util.Log
import com.example.routermanager.BuildConfig
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SpeedTestActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var downloadText: TextView
    private val socket = SpeedTestSocket()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_test)

        progressBar = findViewById(R.id.loadingProgress)
        downloadText = findViewById(R.id.downloadText)
        val runButton: FloatingActionButton = findViewById(R.id.runTestButton)
        val backButton: FloatingActionButton = findViewById(R.id.backButton)
        val offButton: ExtendedFloatingActionButton = findViewById(R.id.offButton)
        val buttonContainer: View = findViewById(R.id.buttonContainer)
        val toggleFab: FloatingActionButton = findViewById(R.id.toggleFab)

        runButton.setOnClickListener { startTest() }
        backButton.setOnClickListener { finish() }
        toggleFab.setOnClickListener { toggleButtonContainer(buttonContainer, toggleFab) }
        offButton.setOnClickListener {
            getSharedPreferences("settings", MODE_PRIVATE).edit { clear() }
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }
    }

    private fun startTest() {
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        downloadText.text = getString(R.string.loading)

        val listener = object : ISpeedTestListener {
            override fun onProgress(percent: Float, report: SpeedTestReport) {
                runOnUiThread { progressBar.progress = percent.toInt() }
            }

            override fun onCompletion(report: SpeedTestReport) {
                socket.removeSpeedTestListener(this)
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    val mbps = report.transferRateBit.toDouble() / 1_000_000
                    downloadText.text = getString(R.string.download_speed_format, mbps)
                }
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                socket.removeSpeedTestListener(this)
                runOnUiThread {
                    Log.e("SpeedTestActivity", "Speed test failed: $errorMessage")
                    progressBar.visibility = View.GONE
                    downloadText.text = getString(R.string.speed_test_failed)
                    Toast.makeText(this@SpeedTestActivity, R.string.speed_test_failed, Toast.LENGTH_LONG).show()
                }
            }
        }

        socket.addSpeedTestListener(listener)
        socket.startDownload(TEST_FILE_URL)
    }

    companion object {
        private const val TEST_FILE_URL = BuildConfig.TEST_FILE_URL
    }
}
