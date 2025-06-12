package com.example.routermanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class SpeedTestActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var downloadText: TextView
    private val tester = NetworkSpeedTester()

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
        lifecycleScope.launch {
            val mbps = tester.downloadSpeed(TEST_FILE_URL) { percent ->
                progressBar.progress = percent
            }
            progressBar.visibility = View.GONE
            downloadText.text = getString(R.string.download_speed_format, mbps)
        }
    }

    companion object {
        private const val TEST_FILE_URL = "https://speed.hetzner.de/100MB.bin"
    }
}
