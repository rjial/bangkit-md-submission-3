package com.rjial.storybook.data.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.repository.StoryListRepository
import com.rjial.storybook.util.ResponseResult
import com.rjial.storybook.util.injection.StoryListInjection

class StoryListViewModel(context: Context): ViewModel() {
    private val storyListRepository: StoryListRepository = StoryListInjection.provideInjection(context)

    fun getAllStories(): LiveData<ResponseResult<StoryListResponse>> = storyListRepository.getAllStories()
}