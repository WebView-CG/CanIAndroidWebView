package com.caniwebview.android.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.caniwebview.android.databinding.FragmentWebviewBinding

class WebViewFragment : Fragment() {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var webView: WebView

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

        webView = binding.webView
        val urlEditText: EditText = binding.urlEditText
        val loadButton: Button = binding.loadButton

        webView.webViewClient = WebViewClient()

        // Load url in field if saved
        val savedUrl = sharedPreferences.getString("url", "https://caniwebview.com/app")
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
                webView.loadUrl(url)
            }
        }

        return root
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}