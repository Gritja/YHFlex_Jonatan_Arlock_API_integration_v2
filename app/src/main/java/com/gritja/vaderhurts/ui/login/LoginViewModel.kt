package com.gritja.vaderhurts.ui.login

import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is login Fragment"
    }
    val text: LiveData<String> = _text

}