package com.rjial.storybook.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.rjial.storybook.data.database.database.StoryListDatabase
import com.rjial.storybook.data.paging.mediator.StoryListRemoteMediator
import com.rjial.storybook.network.endpoint.StoryEndpoint
import com.rjial.storybook.network.response.ListStoryItem
import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.util.ResponseResult
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryListRepository(
    private val apiService: StoryEndpoint,
    private val database: StoryListDatabase
) {

    companion object {
        @Volatile
        private var instance: StoryListRepository? = null

        fun getInstance(apiService: StoryEndpoint, database: StoryListDatabase): StoryListRepository {
            return instance ?: synchronized(this) {
                instance = StoryListRepository(apiService, database)
                return instance!!
            }
        }
    }

    suspend fun getAllStoriesSus(withLoc: Boolean = false, page: Int? = 1, size: Int? = 10): ResponseResult<StoryListResponse> {
        return try {
            val response = apiService.getAllStoriesSus(if (withLoc) 1 else 0, page, size)
            ResponseResult.Success(response)
        } catch (e: Exception) {
            ResponseResult.Error(e.message!!.toString())
        }
    }

//    fun getAllStories(withLoc: Boolean = false, page: Int? = 1, size: Int? = 10) {
//        storyList.value = ResponseResult.Loading
//        apiService.getAllStories(location = if(withLoc) 1 else 0, page = page, size = size).enqueue(object: Callback<StoryListResponse> {
//            override fun onResponse(
//                call: Call<StoryListResponse>,
//                response: Response<StoryListResponse>
//            ) {
//                val body = response.body()
//                if(response.isSuccessful && body != null) {
//                    storyList.value = ResponseResult.Success(body)
//
//                } else {
//                    storyList.value = ResponseResult.Error("Failed to fetch stories")
//                }
//            }
//
//            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
//                storyList.value = ResponseResult.Error("Failed to fetch stories: ${t.message}")
//            }
//        })
//    }

//    private fun storyPager(withLoc: Boolean): Pager {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 10
//            ),
//            pagingSourceFactory = {
//                StoryListPagingSource(apiService, withLoc)
//            }
//        )
//    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStoriesPagerLV(withLoc: Boolean): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryListRemoteMediator(database, apiService, withLoc),
            pagingSourceFactory = {
                database.storyListDao().getAllStories()
//                StoryListPagingSource(apiService, withLoc)
            }
        ).liveData
    }

//    fun uploadStory(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null): LiveData<ResponseResult<StoryAddResponse>>  {
//        resultUpload.value = ResponseResult.Loading
//        try {
//            val upload = apiService.uploadImage(photo, description, lat, lon)
//            upload.enqueue(object : Callback<StoryAddResponse> {
//                override fun onResponse(
//                    call: Call<StoryAddResponse>,
//                    response: Response<StoryAddResponse>
//                ) {
//                    val body = response.body()
//                    if (response.isSuccessful && body != null) {
//                        resultUpload.value = ResponseResult.Success(body)
//                    } else {
//                        val errorRes = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
//                        resultUpload.value = ResponseResult.Error("Failed to upload story : ${errorRes.getString("message")}")
//                    }
//                }
//
//                override fun onFailure(call: Call<StoryAddResponse>, t: Throwable) {
//                    resultUpload.value = ResponseResult.Error("Failed to upload story : ${t.message}")
//                }
//
//            })
//        } catch(exc: Exception) {
//            resultUpload.value = ResponseResult.Error("Failed to upload story : ${exc.message}")
//        }
//        return resultUpload
//    }
    suspend fun uploadStorySus(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null): Result<StoryAddResponse>  {
        val uploadStoryRes = kotlin.runCatching { apiService.uploadStorySus(photo, description, lat, lon) }
        uploadStoryRes.onFailure {
            return Result.failure(it)
        }
        return uploadStoryRes
    }
}