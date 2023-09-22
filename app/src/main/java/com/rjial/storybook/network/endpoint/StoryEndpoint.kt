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

interface StoryEndpoint {
    @GET("stories")
    fun getAllStories(): Call<StoryListResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<StoryAddResponse>
}