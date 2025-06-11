package com.example.routermanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.app.AlertDialog
import android.webkit.SslErrorHandler
import android.net.http.SslError
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.JsResult
import android.webkit.WebSettings
import android.webkit.CookieManager
import android.os.Build
import android.graphics.Bitmap
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity

private const val ROUTER_URL = "https://10.80.80.1/"
private const val KEY_SSL_TRUSTED = "sslTrusted"

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
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
                    .setTitle("SSL Certificate Error")
                    .setMessage(
                        "The router presented an untrusted certificate. Continue anyway?"
                    )
                    .setPositiveButton("Continue") { _, _ ->
                        sslTrusted = true
                        pendingSslHandlers.forEach { it.proceed() }
                        pendingSslHandlers.clear()
                    }
                    .setNegativeButton("Cancel") { _, _ ->
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
    }
    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sslTrusted = savedInstanceState?.getBoolean(KEY_SSL_TRUSTED) ?: false
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.routerWebView)
        val refreshButton: FloatingActionButton = findViewById(R.id.refreshButton)
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

        webView.loadUrl(ROUTER_URL)

        refreshButton.setOnClickListener {
            webView.url?.let { currentUrl -> webView.loadUrl(currentUrl) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_SSL_TRUSTED, sslTrusted)
    }
}
