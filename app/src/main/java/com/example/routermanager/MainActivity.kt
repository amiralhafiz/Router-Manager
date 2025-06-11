package com.example.routermanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.JsResult
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.isVisible

private const val DEFAULT_ROUTER_URL = "https://10.80.80.1/"

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var prefs: SharedPreferences
    private var sslTrusted: Boolean = false
    private val pendingSslHandlers = mutableListOf<SslErrorHandler>()

    private inner class RouterWebViewClient : WebViewClient() {
        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            if (sslTrusted) {
                handler?.proceed()
                return
            }

            handler?.let { pendingSslHandlers.add(it) }

            if (pendingSslHandlers.size == 1) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.ssl_certificate_error_title))
                    .setMessage(
                        getString(R.string.ssl_certificate_error_message)
                    )
                    .setPositiveButton(getString(R.string.action_continue)) { _, _ ->
                        sslTrusted = true
                        prefs.edit { putBoolean(PrefsKeys.KEY_SSL_TRUSTED, true) }
                        pendingSslHandlers.forEach { it.proceed() }
                        pendingSslHandlers.clear()
                    }
                    .setNegativeButton(getString(R.string.action_cancel)) { _, _ ->
                        prefs.edit { remove(PrefsKeys.KEY_SSL_TRUSTED) }
                        pendingSslHandlers.forEach { it.cancel() }
                        pendingSslHandlers.clear()
                    }
                    .setCancelable(false)
                    .show()
            }
        }

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
                showLoadError()
            }
        }

        @Deprecated("Deprecated in Java")
        @Suppress("DEPRECATION")
        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            val currentUrl = view?.url
            if (currentUrl != null && currentUrl == failingUrl) {
                progressBar.visibility = View.GONE
                showLoadError()
            }
        }
    }

    private fun showLoadError() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.page_load_error_title))
            .setMessage(getString(R.string.page_load_error_message))
            .setPositiveButton(getString(R.string.action_back_to_setup)) { _, _ ->
                val intent = Intent(this, SetupActivity::class.java)
                intent.putExtra(EXTRA_FORCE_SETUP, true)
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }
    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        sslTrusted = savedInstanceState?.getBoolean(PrefsKeys.KEY_SSL_TRUSTED)
            ?: prefs.getBoolean(PrefsKeys.KEY_SSL_TRUSTED, false)
        val routerUrl = prefs.getString(PrefsKeys.KEY_ROUTER_URL, DEFAULT_ROUTER_URL)
            ?: DEFAULT_ROUTER_URL
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.routerWebView)
        val refreshButton: FloatingActionButton = findViewById(R.id.refreshButton)
        val speedTestButton: FloatingActionButton = findViewById(R.id.speedTestButton)
        val offButton: ExtendedFloatingActionButton = findViewById(R.id.offButton)
        val buttonContainer: View = findViewById(R.id.buttonContainer)
        val toggleFab: FloatingActionButton = findViewById(R.id.toggleFab)
        progressBar = findViewById(R.id.loadingProgress)
        webView.webViewClient = RouterWebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
                    .setCancelable(false)
                    .show()
                return true
            }
            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?,
            ): Boolean {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> result?.cancel() }
                    .setCancelable(false)
                    .show()
                return true
            }
        }
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        CookieManager.getInstance().setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }

        webView.loadUrl(routerUrl)

        refreshButton.setOnClickListener {
            webView.url?.let { currentUrl -> webView.loadUrl(currentUrl) }
        }

        speedTestButton.setOnClickListener {
            startActivity(Intent(this, SpeedTestActivity::class.java))
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
            prefs.edit { clear() }
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(PrefsKeys.KEY_SSL_TRUSTED, sslTrusted)
    }
}
