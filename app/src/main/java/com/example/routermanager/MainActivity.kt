package com.example.routermanager

import android.os.Bundle
import android.app.AlertDialog
import android.webkit.SslErrorHandler
import android.net.http.SslError
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
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
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.routerWebView)
        webView.webViewClient = RouterWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("http://10.80.80.1/")
    }
}
