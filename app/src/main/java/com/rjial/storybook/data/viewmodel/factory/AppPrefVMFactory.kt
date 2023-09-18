package com.rjial.storybook.data.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rjial.storybook.data.preference.AppPreferences
import com.rjial.storybook.data.viewmodel.AppPreferencesViewModel

class AppPrefVMFactory(private val preferences: AppPreferences): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AppPreferencesViewModel::class.java)) {
            return AppPreferencesViewModel(preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class : ${modelClass.name}")
    }
}