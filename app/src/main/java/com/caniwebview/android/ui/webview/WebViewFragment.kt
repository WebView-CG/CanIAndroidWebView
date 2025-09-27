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

package com.caniwebview.android.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.caniwebview.android.R
import com.caniwebview.android.ui.fullscreen.WebViewActivity
import com.caniwebview.android.databinding.FragmentWebviewBinding

class WebViewFragment : Fragment() {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var webView: WebView
    
    companion object {
        private const val DEFAULT_URL = "https://caniwebview.local/assets/html/index.html"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences =
            requireActivity().getSharedPreferences("WebViewSettings", Context.MODE_PRIVATE)

        // Add the MenuProvider
        setupMenu()

        webView = binding.webView
        val urlEditText: EditText = binding.urlEditText
        val loadButton: Button = binding.loadButton
        val urlBar = binding.urlBar


        // Create the asset loader
        // Create the asset loader with a custom domain
        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain("caniwebview.local") // Set your custom domain here
            .setHttpAllowed(false) // Enforce HTTPS (recommended)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .build()


        // Set the WebViewClient
        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Show the current URL in the URL bar
                urlBar.text = url
            }
        }

        // Load url in field if saved
        val savedUrl = sharedPreferences.getString("url", DEFAULT_URL)
        if (savedUrl != null) {
            urlEditText.setText(savedUrl)
            webView.loadUrl(savedUrl)
        }

        loadButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotEmpty()) {
                // Save to preferences
                with(sharedPreferences.edit()) {
                    putString("url", url)
                    apply()
                }

                if (sharedPreferences.getBoolean("fullscreenWebView", false)) {
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("url", url)
                    startActivity(intent)
                } else {
                    webView.loadUrl(url)
                    urlBar.text = url
                }
            }
        }

        return root
    }

    private fun setupMenu() {
        // The usage of an interface lets you inject your own implementation.
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State.
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_webview, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_home -> {
                        loadDefaultUrl()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loadDefaultUrl() {
        val urlEditText: EditText = binding.urlEditText
        urlEditText.setText(DEFAULT_URL)
        
        // Save to preferences
        with(sharedPreferences.edit()) {
            putString("url", DEFAULT_URL)
            apply()
        }

        if (sharedPreferences.getBoolean("fullscreenWebView", false)) {
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            intent.putExtra("url", DEFAULT_URL)
            startActivity(intent)
        } else {
            webView.loadUrl(DEFAULT_URL)
            binding.urlBar.text = DEFAULT_URL
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyWebViewSettings(webView.settings)
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

        // Change label of load button if fullscreen is enabled
        if (sharedPreferences.getBoolean("fullscreenWebView", false)) {
            binding.loadButton.text = getString(R.string.fullscreen)
        } else {
            binding.loadButton.text = getString(R.string.load)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}