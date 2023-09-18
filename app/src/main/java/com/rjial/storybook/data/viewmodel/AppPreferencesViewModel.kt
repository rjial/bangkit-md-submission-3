package com.rjial.storybook.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rjial.storybook.data.preference.AppPreferences
import kotlinx.coroutines.launch

class AppPreferencesViewModel(private val pref: AppPreferences): ViewModel() {
    fun getTokenAuth(): LiveData<String> {
        return pref.getTokenAuth().asLiveData()
    }

    fun setTokenAuth(token: String) {
        viewModelScope.launch {
            pref.setTokenAuth(token)
        }
    }

    fun isAuthorized(): LiveData<Boolean> {
        return pref.isAuthorized().asLiveData()
    }
}