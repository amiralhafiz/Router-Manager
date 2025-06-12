package com.example.routermanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import android.os.Build
import java.net.InetAddress
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.routermanager.BuildConfig


class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val forceSetup = intent.getBooleanExtra(Constants.EXTRA_FORCE_SETUP, false)
        val prefs = settingsPrefs
        if (!forceSetup && prefs.contains(PrefsKeys.KEY_ROUTER_URL)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_setup)
        val urlField: EditText = findViewById(R.id.urlEditText)
        val progress: ProgressBar = findViewById(R.id.setupProgress)
        val versionView: TextView = findViewById(R.id.versionTextView)
        urlField.setText(prefs.getString(PrefsKeys.KEY_ROUTER_URL, ""))
        val accessButton: FloatingActionButton = findViewById(R.id.accessButton)
        versionView.text = getString(R.string.version_format, BuildConfig.VERSION_NAME)

        urlField.isEnabled = false
        accessButton.isEnabled = false
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            val address = withContext(Dispatchers.IO) {
                val connectivity = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val network = connectivity?.activeNetwork
                    connectivity?.getLinkProperties(network)
                        ?.routes
                        ?.firstOrNull { it.isDefaultRoute && it.gateway is Inet4Address }
                        ?.gateway
                        ?.hostAddress
                } else {
                    val wifi = applicationContext.getSystemService(WIFI_SERVICE) as? WifiManager
                    @Suppress("DEPRECATION")
                    wifi?.dhcpInfo?.gateway?.takeIf { it != 0 }?.let { gateway ->
                        val bytes = ByteBuffer.allocate(4)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .putInt(gateway)
                            .array()
                        InetAddress.getByAddress(bytes).hostAddress
                    }
                }
            }
            val scheme = withContext(Dispatchers.IO) {
                address?.let {
                    when {
                        NetworkUtils.isPortOpen(it, 80, 200) -> "http://"
                        NetworkUtils.isPortOpen(it, 443, 200) -> "https://"
                        else -> "http://"
                    }
                }
            }
            withContext(Dispatchers.Main) {
                // Keep the URL field read‑only after network detection completes
                address?.let { urlField.setText("${scheme ?: "http://"}$it/") }
                progress.visibility = View.GONE
                if (address != null || urlField.text.isNotBlank()) {
                    accessButton.isEnabled = true
                }
            }
        }

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

