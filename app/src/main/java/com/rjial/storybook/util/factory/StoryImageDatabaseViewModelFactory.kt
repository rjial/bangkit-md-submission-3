package com.rjial.storybook.util.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rjial.storybook.data.viewmodel.StoryImageDatabaseViewModel

class StoryImageDatabaseViewModelFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StoryImageDatabaseViewModel::class.java)) {
            return StoryImageDatabaseViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown class : ${modelClass.name}")
    }
}