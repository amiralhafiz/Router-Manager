package com.example.routermanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

const val EXTRA_FORCE_SETUP = "forceSetup"

class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val forceSetup = intent.getBooleanExtra(EXTRA_FORCE_SETUP, false)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        if (!forceSetup && prefs.contains(PrefsKeys.KEY_ROUTER_URL)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_setup)
        val urlField: EditText = findViewById(R.id.urlEditText)
        val progress: ProgressBar = findViewById(R.id.setupProgress)
        urlField.setText(prefs.getString(PrefsKeys.KEY_ROUTER_URL, ""))
        val accessButton: FloatingActionButton = findViewById(R.id.accessButton)

        urlField.isEnabled = false
        accessButton.isEnabled = false
        progress.visibility = View.VISIBLE
        Thread {
            val wifi = applicationContext.getSystemService(WIFI_SERVICE) as? WifiManager
            val address = wifi?.dhcpInfo?.gateway?.takeIf { it != 0 }?.let {
                @Suppress("DEPRECATION")
                Formatter.formatIpAddress(it)
            }
            runOnUiThread {
                address?.let { urlField.setText("https://$it/") }
                progress.visibility = View.GONE
                if (address != null || urlField.text.isNotBlank()) {
                    accessButton.isEnabled = true
                }
            }
        }.start()

        accessButton.setOnClickListener {
            val url = urlField.text.toString().trim()
            prefs.edit {
                if (url.isBlank()) {
                    remove(PrefsKeys.KEY_ROUTER_URL)
                } else {
                    putString(PrefsKeys.KEY_ROUTER_URL, url)
                }
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
