package com.rjial.storybook.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.rjial.storybook.network.response.ListStoryItem
import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.repository.StoryListRepository
import com.rjial.storybook.util.ResponseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryListViewModel(private val storyListRepository: StoryListRepository): ViewModel() {
    private val _storyList: MutableLiveData<ResponseResult<StoryListResponse>> =
        MutableLiveData<ResponseResult<StoryListResponse>>()
    var storyList: LiveData<ResponseResult<StoryListResponse>> = _storyList
    fun getAllStoriesSus(withLoc: Boolean, page: Int? = 1, size: Int? = 10) {
        viewModelScope.launch {
            val storiesSus = kotlin.runCatching { storyListRepository.getAllStoriesSus(withLoc, page, size) }
            storiesSus.onSuccess {
                _storyList.value = it
            }
        }
    }

    fun getAllStories(withLoc: Boolean = false): LiveData<PagingData<ListStoryItem>> = storyListRepository.getAllStoriesPager(withLoc).liveData.cachedIn(viewModelScope)

    fun uploadStory(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null): LiveData<ResponseResult<StoryAddResponse>> = storyListRepository.uploadStory(photo, description, lat, lon)
}