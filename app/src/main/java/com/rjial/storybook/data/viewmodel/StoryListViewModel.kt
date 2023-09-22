package com.rjial.storybook.data.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.repository.StoryListRepository
import com.rjial.storybook.util.ResponseResult
import com.rjial.storybook.util.injection.StoryListInjection
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryListViewModel(context: Context): ViewModel() {
    private val storyListRepository: StoryListRepository = StoryListInjection.provideInjection(context)
    private var _stories: MutableLiveData<ResponseResult<StoryListResponse>> = MutableLiveData<ResponseResult<StoryListResponse>>()
    val stories: LiveData<ResponseResult<StoryListResponse>> = _stories

    fun getAllStories(): LiveData<ResponseResult<StoryListResponse>> = storyListRepository.getAllStories()

    fun uploadStory(photo: MultipartBody.Part, description: RequestBody): LiveData<ResponseResult<StoryAddResponse>> = storyListRepository.uploadStory(photo, description)
}