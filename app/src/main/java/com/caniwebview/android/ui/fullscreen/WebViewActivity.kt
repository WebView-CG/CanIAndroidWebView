/*
 * Copyright (C) 2025 Webview Community Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caniwebview.android.ui.fullscreen

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.caniwebview.android.databinding.FullscreenWebviewBinding

class WebViewActivity : AppCompatActivity() {

    private var _binding: FullscreenWebviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FullscreenWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("WebViewSettings", MODE_PRIVATE)

        webView = binding.webView
        applyWebViewSettings(webView.settings)

        // Create the asset loader
        // Create the asset loader with a custom domain
        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain("caniwebview.local") // Set your custom domain here
            .setHttpAllowed(false) // Enforce HTTPS (recommended)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()


        // Set the WebViewClient
        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        // Load the URL passed from the intent
        val url = intent.getStringExtra("url")
        if (url != null) {
            webView.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        applyWebViewSettings(webView.settings)
    }

    private fun applyWebViewSettings(settings: WebSettings) {
        settings.javaScriptEnabled = sharedPreferences.getBoolean("javaScriptEnabled", true)
        settings.javaScriptCanOpenWindowsAutomatically =
            sharedPreferences.getBoolean("javaScriptCanOpenWindows", true)
        settings.domStorageEnabled = sharedPreferences.getBoolean("domStorageEnabled", true)
        settings.setGeolocationEnabled(sharedPreferences.getBoolean("geolocationEnabled", true))
        // Apply other settings here...
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        settings.saveFormData = false
        settings.mediaPlaybackRequiresUserGesture = false
    }
}