package com.rjial.storybook.data.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rjial.storybook.data.viewmodel.StoryListViewModel
import com.rjial.storybook.repository.StoryListRepository

class StoryListVMFactory(private val storyListRepository: StoryListRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryListViewModel::class.java)) {
            return StoryListViewModel(storyListRepository) as T
        }
        throw IllegalArgumentException("Failed make view model class : ${modelClass.name}")
    }
}