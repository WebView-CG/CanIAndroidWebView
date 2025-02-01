package com.caniwebview.android.ui.webview

import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModelProvider
import com.caniwebview.android.databinding.FragmentWebviewBinding

class WebViewFragment : Fragment() {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val webViewViewModel =
            ViewModelProvider(this).get(WebViewViewModel::class.java)

        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val webView: WebView = binding.webView
        val urlEditText: EditText = binding.urlEditText
        val loadButton: Button = binding.loadButton

        webView.webViewClient = WebViewClient()

        // That's how Apache Cordova is configured by default
        // https://github.com/apache/cordova-android/blob/a78fad17835f37e55b232427348d02c0c81bf491/framework/src/org/apache/cordova/engine/SystemWebViewEngine.java
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webView.settings.saveFormData = false
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.domStorageEnabled = true
        webView.settings.setGeolocationEnabled(true)

        // TODO make settings configurable

        loadButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotEmpty()) {
                webView.loadUrl(url)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}