package com.example.routermanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import java.net.InetSocketAddress
import java.net.Socket
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.net.wifi.WifiManager
import java.net.InetAddress
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
        val versionView: TextView = findViewById(R.id.versionTextView)
        urlField.setText(prefs.getString(PrefsKeys.KEY_ROUTER_URL, ""))
        val accessButton: FloatingActionButton = findViewById(R.id.accessButton)
        versionView.text = getString(R.string.version_format, BuildConfig.VERSION_NAME)

        urlField.isEnabled = false
        accessButton.isEnabled = false
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            val address = withContext(Dispatchers.IO) {
                val wifi = applicationContext.getSystemService(WIFI_SERVICE) as? WifiManager
                wifi?.dhcpInfo?.gateway?.takeIf { it != 0 }?.let { gateway ->
                    val bytes = ByteBuffer.allocate(4)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(gateway)
                        .array()
                    InetAddress.getByAddress(bytes).hostAddress
                }
            }
            val scheme = withContext(Dispatchers.IO) {
                address?.let {
                    when {
                        isPortOpen(it, 443, 200) -> "https://"
                        isPortOpen(it, 80, 200) -> "http://"
                        else -> "http://"
                    }
                }
            }
            withContext(Dispatchers.Main) {
                urlField.isEnabled = true
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

private fun isPortOpen(host: String, port: Int, timeoutMs: Int): Boolean {
    return try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(host, port), timeoutMs)
        }
        true
    } catch (_: Exception) {
        false
    }
}
