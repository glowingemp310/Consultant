package com.example.consultant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView

class TermsAndCondition : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var btnBack: ImageView
    var filename:String="termsCondition.html"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)
        findView()
        onClickListener()

        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadUrl("file:///android_asset/" + filename)
    }

    private fun onClickListener() {
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun findView()
    {
        webView=findViewById(R.id.wvTermCondition)
        btnBack=findViewById(R.id.btnBack)

    }
}