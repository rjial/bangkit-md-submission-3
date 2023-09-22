package com.rjial.storybook.data.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rjial.storybook.data.viewmodel.StoryListViewModel
import com.rjial.storybook.repository.StoryAuthAppPrefRepository

class StoryListVMFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryListViewModel::class.java)) {
            return StoryListViewModel(context) as T
        }
        throw IllegalArgumentException("Failed make view model class : ${modelClass.name}")
    }
}