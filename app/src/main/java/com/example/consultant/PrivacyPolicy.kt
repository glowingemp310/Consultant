package com.example.consultant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView

class PrivacyPolicy : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var btnBack: ImageView
    var filename:String="privacypolicy.html"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        findView()
        onClickListener()

        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadUrl("file:///android_asset/" + filename)
    }
    private fun findView()
    {
        webView=findViewById(R.id.wvPrivacyPolicy)
        btnBack=findViewById(R.id.btnBack)

    }

    private fun onClickListener() {
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}