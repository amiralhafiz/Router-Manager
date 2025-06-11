package com.example.routermanager

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView = findViewById<WebView>(R.id.webview)
        val urlField = findViewById<EditText>(R.id.urlField)
        val loadButton = findViewById<Button>(R.id.loadButton)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        loadButton.setOnClickListener {
            val url = urlField.text.toString()
            webView.loadUrl(url)
        }
    }
}
