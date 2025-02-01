package com.caniwebview.android.ui.config

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.caniwebview.android.databinding.FragmentConfigBinding

class ConfigFragment : Fragment() {

    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireActivity().getSharedPreferences("WebViewSettings", Context.MODE_PRIVATE)

        // Initialize UI elements and load saved settings
        setupUI()
        loadSettings()

        return root
    }

    private fun setupUI() {
        binding.javaScriptEnabledCheckBox.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("javaScriptEnabled", isChecked)
        }
        binding.javaScriptCanOpenWindowsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("javaScriptCanOpenWindows", isChecked)
        }
        binding.domStorageEnabledCheckBox.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("domStorageEnabled", isChecked)
        }
        binding.geolocationEnabledCheckBox.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("geolocationEnabled", isChecked)
        }
        // Add listeners for other settings here...
    }

    private fun loadSettings() {
        binding.javaScriptEnabledCheckBox.isChecked = sharedPreferences.getBoolean("javaScriptEnabled", true)
        binding.javaScriptCanOpenWindowsCheckBox.isChecked = sharedPreferences.getBoolean("javaScriptCanOpenWindows", true)
        binding.domStorageEnabledCheckBox.isChecked = sharedPreferences.getBoolean("domStorageEnabled", true)
        binding.geolocationEnabledCheckBox.isChecked = sharedPreferences.getBoolean("geolocationEnabled", true)
        // Load other settings here...
    }

    private fun saveSetting(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}