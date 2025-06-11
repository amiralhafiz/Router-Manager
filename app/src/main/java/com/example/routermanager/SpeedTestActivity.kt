package com.example.routermanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.webkit.WebSettings
import android.webkit.CookieManager
import androidx.core.content.edit
import android.view.View
import androidx.core.view.isVisible

class SpeedTestActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_test)

        val webView: WebView = findViewById(R.id.speedTestWebView)
        val backButton: FloatingActionButton = findViewById(R.id.backButton)
        val refreshButton: FloatingActionButton = findViewById(R.id.refreshButton)
        val offButton: ExtendedFloatingActionButton = findViewById(R.id.offButton)
        val buttonContainer: View = findViewById(R.id.buttonContainer)
        val toggleFab: FloatingActionButton = findViewById(R.id.toggleFab)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        CookieManager.getInstance().setAcceptCookie(true)
        webView.loadUrl(/* url = */ "https://www.speedtest.net/")

        backButton.setOnClickListener {
            finish()
        }
        refreshButton.setOnClickListener {
            webView.url?.let { currentUrl ->
                webView.loadUrl(
                    currentUrl
                )
            }
        }
        toggleFab.setOnClickListener {
            val visible = buttonContainer.isVisible
            if (visible) {
                buttonContainer.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction { buttonContainer.visibility = View.GONE }
                    .start()
                toggleFab.setImageResource(android.R.drawable.ic_menu_more)
                toggleFab.contentDescription = getString(R.string.action_expand)
            } else {
                buttonContainer.alpha = 0f
                buttonContainer.visibility = View.VISIBLE
                buttonContainer.animate().alpha(1f).setDuration(200).start()
                toggleFab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                toggleFab.contentDescription = getString(R.string.action_collapse)
            }
        }
        offButton.setOnClickListener {
            getSharedPreferences("settings", MODE_PRIVATE).edit { clear() }
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }
    }
}
