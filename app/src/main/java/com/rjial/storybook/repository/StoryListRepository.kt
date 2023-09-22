package com.rjial.storybook.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.rjial.storybook.network.endpoint.StoryEndpoint
import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.network.service.StoryListService
import com.rjial.storybook.util.ResponseResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart

class StoryListRepository(
    private val apiService: StoryEndpoint
) {
    private val result = MutableLiveData<ResponseResult<StoryListResponse>>()
    private val resultUpload = MutableLiveData<ResponseResult<StoryAddResponse>>()

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

    fun uploadStory(photo: MultipartBody.Part, description: RequestBody): LiveData<ResponseResult<StoryAddResponse>>  {
        resultUpload.value = ResponseResult.Loading
        try {
            val upload = apiService.uploadImage(photo, description)
            upload.enqueue(object : Callback<StoryAddResponse> {
                override fun onResponse(
                    call: Call<StoryAddResponse>,
                    response: Response<StoryAddResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        resultUpload.value = ResponseResult.Success(body)
                    } else {
                        resultUpload.value = ResponseResult.Error("Failed to upload story : ${body!!.message}")
                    }
                }

                override fun onFailure(call: Call<StoryAddResponse>, t: Throwable) {
                    resultUpload.value = ResponseResult.Error("Failed to upload story : ${t.message}")
                }

            })
        } catch(exc: Exception) {
            resultUpload.value = ResponseResult.Error("Failed to upload story : ${exc.message}")
        }
        return resultUpload
    }
}