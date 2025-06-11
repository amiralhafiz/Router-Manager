package com.example.routermanager

import android.os.Bundle
import android.app.AlertDialog
import android.webkit.SslErrorHandler
import android.net.http.SslError
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.JsResult
import android.webkit.WebSettings
import android.graphics.Bitmap
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity

private const val ROUTER_URL = "https://10.80.80.1/"

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar

    private inner class RouterWebViewClient : WebViewClient() {
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            AlertDialog.Builder(this@MainActivity)
                .setTitle("SSL Certificate Error")
                .setMessage("The router presented an untrusted certificate. Continue anyway?")
                .setPositiveButton("Continue") { _, _ -> handler?.proceed() }
                .setNegativeButton("Cancel") { _, _ -> handler?.cancel() }
                .setCancelable(false)
                .show()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        webView.loadUrl(ROUTER_URL)
        refreshButton.setOnClickListener { webView.reload() }
    }
}
