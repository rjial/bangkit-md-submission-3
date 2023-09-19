package com.rjial.storybook.data.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rjial.storybook.data.viewmodel.AppPreferencesViewModel
import com.rjial.storybook.repository.StoryAuthAppPrefRepository

class AppPrefVMFactory(private val repository: StoryAuthAppPrefRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AppPreferencesViewModel::class.java)) {
            return AppPreferencesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class : ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: AppPrefVMFactory? = null

        fun getInstance(repository: StoryAuthAppPrefRepository): AppPrefVMFactory {
            return instance ?: synchronized(this) {
                instance = AppPrefVMFactory(repository)
                return instance!!
            }
        }
    }
}