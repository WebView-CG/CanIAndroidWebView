package com.caniwebview.android.ui.config

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.caniwebview.android.R
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

        // Add the MenuProvider
        setupMenu()

        // Initialize UI elements and load saved settings
        setupUI()
        loadSettings()

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
                menuInflater.inflate(R.menu.menu_config, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_github -> {
                        openGitHub()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupUI() {
        // Presets
        binding.selectAllButton.setOnClickListener {
            setPreset("all")
        }

        binding.selectNoneButton.setOnClickListener {
            setPreset("none")
        }

        binding.selectCordovaButton.setOnClickListener {
            setPreset("cordova")
        }

        // Settings
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

    private fun setPreset(preset: String) {
        when (preset) {
            "all" -> {
                binding.javaScriptEnabledCheckBox.isChecked = true
                binding.javaScriptCanOpenWindowsCheckBox.isChecked = true
                binding.domStorageEnabledCheckBox.isChecked = true
                binding.geolocationEnabledCheckBox.isChecked = true
            }
            "none" -> {
                binding.javaScriptEnabledCheckBox.isChecked = false
                binding.javaScriptCanOpenWindowsCheckBox.isChecked = false
                binding.domStorageEnabledCheckBox.isChecked = false
                binding.geolocationEnabledCheckBox.isChecked = false
            }
            "cordova" -> {
                binding.javaScriptEnabledCheckBox.isChecked = true
                binding.javaScriptCanOpenWindowsCheckBox.isChecked = true
                binding.domStorageEnabledCheckBox.isChecked = true
                binding.geolocationEnabledCheckBox.isChecked = true
            }
        }
    }

    private fun openGitHub() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WebView-CG/CanIAndroidWebView"))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}