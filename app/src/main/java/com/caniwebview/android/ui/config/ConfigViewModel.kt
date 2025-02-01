package com.caniwebview.android.ui.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfigViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "You will be able to configure your WebView here soon"
    }
    val text: LiveData<String> = _text
}