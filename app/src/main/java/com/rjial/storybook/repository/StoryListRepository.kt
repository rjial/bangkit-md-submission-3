package com.rjial.storybook.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.rjial.storybook.network.endpoint.StoryEndpoint
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.network.service.StoryListService
import com.rjial.storybook.util.ResponseResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryListRepository(
    private val apiService: StoryEndpoint
) {
    private val result = MutableLiveData<ResponseResult<StoryListResponse>>()

    companion object {
        @Volatile
        private var instance: StoryListRepository? = null

        fun getInstance(apiService: StoryEndpoint): StoryListRepository {
            return instance ?: synchronized(this) {
                instance = StoryListRepository(apiService)
                return instance!!
            }
        }
    }

    fun getAllStories(): LiveData<ResponseResult<StoryListResponse>> {
        result.value = ResponseResult.Loading
        apiService.getAllStories().enqueue(object: Callback<StoryListResponse> {
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
                val body = response.body()
                if(response.isSuccessful && body != null) {
                    result.value = ResponseResult.Success(body)
                } else {
                    result.value = ResponseResult.Error("Failed to fetch stories")
                }
            }

            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                result.value = ResponseResult.Error("Failed to fetch stories: ${t.message}")
            }
        })

        return result
    }
}