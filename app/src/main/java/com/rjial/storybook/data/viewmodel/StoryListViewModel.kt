package com.rjial.storybook.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rjial.storybook.network.response.ListStoryItem
import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.repository.StoryListRepository
import com.rjial.storybook.util.ResponseResult
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryListViewModel(val storyListRepository: StoryListRepository): ViewModel() {
    private val _storyList: MutableLiveData<ResponseResult<StoryListResponse>> = MutableLiveData<ResponseResult<StoryListResponse>>()
    var storyList: LiveData<ResponseResult<StoryListResponse>> = _storyList

    fun getAllStories(withLoc: Boolean = false): LiveData<PagingData<ListStoryItem>> = storyListRepository.getAllStoriesNew(withLoc).cachedIn(viewModelScope)

    fun getAllStoriesNonPaging(withLoc: Boolean = false, page: Int = 1, size: Int = 10) {
        storyListRepository.getAllStories(withLoc, page, size, _storyList)
    }

    fun uploadStory(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null): LiveData<ResponseResult<StoryAddResponse>> = storyListRepository.uploadStory(photo, description, lat, lon)
}