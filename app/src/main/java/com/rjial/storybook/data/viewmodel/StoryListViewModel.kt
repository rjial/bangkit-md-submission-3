package com.rjial.storybook.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rjial.storybook.network.response.ListStoryItem
import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.repository.StoryListRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryListViewModel(private val storyListRepository: StoryListRepository): ViewModel() {

    fun getAllStories(withLoc: Boolean = false): LiveData<PagingData<ListStoryItem>> = storyListRepository.getAllStoriesPagerLV(withLoc).cachedIn(viewModelScope)
    suspend fun uploadStorySus(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null): Result<StoryAddResponse> {
        return storyListRepository.uploadStorySus(photo, description, lat, lon)
    }

    fun getAllStoriesLoc(callback: (story: StoryListResponse) -> Unit) {
        viewModelScope.launch {
            val result = storyListRepository.getAllStoriesSus(true, size = -1)
            result.onSuccess {
                callback(it)
            }
        }
    }
}