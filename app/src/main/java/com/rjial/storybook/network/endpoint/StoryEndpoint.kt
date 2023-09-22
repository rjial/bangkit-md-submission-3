package com.rjial.storybook.network.endpoint

import com.rjial.storybook.network.response.StoryListResponse
import retrofit2.Call
import retrofit2.http.GET

interface StoryEndpoint {
    @GET("stories")
    fun getAllStories(): Call<StoryListResponse>
}