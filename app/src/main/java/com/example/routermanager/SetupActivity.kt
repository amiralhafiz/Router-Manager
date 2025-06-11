package com.example.routermanager

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

const val EXTRA_FORCE_SETUP = "forceSetup"
private const val KEY_ROUTER_URL = "routerUrl"

class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val forceSetup = intent.getBooleanExtra(EXTRA_FORCE_SETUP, false)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        if (!forceSetup && prefs.contains(KEY_ROUTER_URL)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_setup)
        val urlField: EditText = findViewById(R.id.urlEditText)
        urlField.setText(prefs.getString(KEY_ROUTER_URL, ""))
        val accessButton: Button = findViewById(R.id.accessButton)

        urlField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                accessButton.performClick()
                true
            } else {
                false
            }
        }

        accessButton.setOnClickListener {
            val url = urlField.text.toString().trim()
            val editor = prefs.edit()
            if (url.isBlank()) {
                editor.remove(KEY_ROUTER_URL)
            } else {
                editor.putString(KEY_ROUTER_URL, url)
            }
            editor.apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
