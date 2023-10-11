package com.rjial.storybook.data.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rjial.storybook.network.response.ListStoryItem
import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.repository.StoryListRepository
import com.rjial.storybook.util.ResponseResult
import com.rjial.storybook.util.injection.StoryListInjection
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryListViewModel(context: Context): ViewModel() {
    private val storyListRepository: StoryListRepository = StoryListInjection.provideInjection(context)

    fun getAllStories(withLoc: Boolean = false): LiveData<PagingData<ListStoryItem>> = storyListRepository.getAllStoriesNew(withLoc).cachedIn(viewModelScope)

    fun getAllStoriesNonPaging(withLoc: Boolean = false): LiveData<ResponseResult<StoryListResponse>> = storyListRepository.getAllStories(withLoc)

    fun uploadStory(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null): LiveData<ResponseResult<StoryAddResponse>> = storyListRepository.uploadStory(photo, description, lat, lon)
}