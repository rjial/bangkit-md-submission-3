package com.rjial.storybook.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.rjial.storybook.data.database.entity.StoryImageEntity
import com.rjial.storybook.repository.StoryImageDatabaseRepository

class StoryImageDatabaseViewModel(context: Context): ViewModel() {
    private val repo: StoryImageDatabaseRepository = StoryImageDatabaseRepository(context.applicationContext)

    fun insert(url: String) {
        val storyImage = StoryImageEntity()
        storyImage.imageUrl = url
        repo.insert(storyImage)
    }

    fun deleteAllStoryImages() {
        repo.deleteAllStoryImages()
    }

}