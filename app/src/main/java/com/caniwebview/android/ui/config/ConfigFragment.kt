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
                        openUrlInBrowser("https://github.com/WebView-CG/CanIAndroidWebView")
                        true
                    }
                    R.id.action_testing_url -> {
                        openUrlInBrowser("https://caniwebview.com/testing")
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
        binding.fullscreenWebViewCheckBox.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("fullscreenWebView", isChecked)
        }
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
        binding.fullscreenWebViewCheckBox.isChecked = sharedPreferences.getBoolean("fullscreenWebView", false)
        binding.javaScriptEnabledCheckBox.isChecked = sharedPreferences.getBoolean("javaScriptEnabled", true)
        binding.javaScriptCanOpenWindowsCheckBox.isChecked = sharedPreferences.getBoolean("javaScriptCanOpenWindows", true)
        binding.domStorageEnabledCheckBox.isChecked = sharedPreferences.getBoolean("domStorageEnabled", true)
        binding.geolocationEnabledCheckBox.isChecked = sharedPreferences.getBoolean("geolocationEnabled", true)
        // Load other settings here..
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
                binding.fullscreenWebViewCheckBox.isChecked = false
                binding.javaScriptEnabledCheckBox.isChecked = false
                binding.javaScriptCanOpenWindowsCheckBox.isChecked = false
                binding.domStorageEnabledCheckBox.isChecked = false
                binding.geolocationEnabledCheckBox.isChecked = false
            }
            "cordova" -> {
                binding.fullscreenWebViewCheckBox.isChecked = true
                binding.javaScriptEnabledCheckBox.isChecked = true
                binding.javaScriptCanOpenWindowsCheckBox.isChecked = true
                binding.domStorageEnabledCheckBox.isChecked = true
                binding.geolocationEnabledCheckBox.isChecked = true
            }
        }
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}