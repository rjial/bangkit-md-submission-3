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
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryListRepository(
    private val apiService: StoryEndpoint,
    private val database: StoryListDatabase
) {


    suspend fun getAllStoriesSus(withLoc: Boolean = false, page: Int? = 1, size: Int? = 10): Result<StoryListResponse>  = kotlin.runCatching { apiService.getAllStoriesSus(if (withLoc) 1 else 0, page, size) }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStoriesPagerLV(withLoc: Boolean): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryListRemoteMediator(database, apiService, withLoc),
            pagingSourceFactory = {
                database.storyListDao().getAllStories()
            }
        ).liveData
    }

    suspend fun uploadStorySus(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null): Result<StoryAddResponse>  {
        val uploadStoryRes = kotlin.runCatching { apiService.uploadStorySus(photo, description, lat, lon) }
        uploadStoryRes.onFailure {
            return Result.failure(it)
        }
        return uploadStoryRes
    }

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
}