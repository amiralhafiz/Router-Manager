package com.example.routermanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.webkit.WebSettings
import android.webkit.CookieManager
import androidx.core.content.edit
import android.view.View
import android.widget.ProgressBar
import android.graphics.Bitmap

class SpeedTestActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar

    private inner class SpeedTestWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            if (request.isForMainFrame) {
                progressBar.visibility = View.GONE
            }
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_test)

        val webView: WebView = findViewById(R.id.speedTestWebView)
        progressBar = findViewById(R.id.loadingProgress)
        val backButton: FloatingActionButton = findViewById(R.id.backButton)
        val refreshButton: FloatingActionButton = findViewById(R.id.refreshButton)
        val offButton: ExtendedFloatingActionButton = findViewById(R.id.offButton)
        val buttonContainer: View = findViewById(R.id.buttonContainer)
        val toggleFab: FloatingActionButton = findViewById(R.id.toggleFab)

        webView.webViewClient = SpeedTestWebViewClient()

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
            toggleButtonContainer(buttonContainer, toggleFab)
        }
        offButton.setOnClickListener {
            settingsPrefs.edit { clear() }
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }
    }
}
