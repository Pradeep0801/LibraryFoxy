package com.sdk.foxpanda.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.MotionEvent
import com.sdk.foxpanda.R


class RichMediaNotification: AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.layout_rich_media)
        val webview = findViewById<WebView>(R.id.rich_media_webview)
        val url = intent.getStringExtra("url")
        webview.settings.javaScriptEnabled = true
        webview.setWebViewClient(WebViewClient())
        webview.loadUrl(url)
        //webview.setOnTouchListener { v, event -> event.action == MotionEvent.ACTION_MOVE }
    }

}
