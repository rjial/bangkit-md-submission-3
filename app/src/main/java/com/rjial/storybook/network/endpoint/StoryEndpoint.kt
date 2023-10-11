package com.rjial.storybook.network.endpoint

import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.network.response.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface StoryEndpoint {
    @GET("stories")
    fun getAllStories(
        @Query("location") location: Int = 0,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10
    ): Call<StoryListResponse>

    @GET("stories")
    suspend fun getAllStoriesSus(
        @Query("location") location: Int = 0,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10
    ): StoryListResponse

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Call<StoryAddResponse>
}