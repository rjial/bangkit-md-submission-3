package com.rjial.storybook.network.endpoint

import com.rjial.storybook.network.response.StoryAuthLoginBody
import com.rjial.storybook.network.response.StoryAuthLoginResponse
import com.rjial.storybook.network.response.StoryAuthRegisterBody
import com.rjial.storybook.network.response.StoryAuthRegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface StoryAuthEndpoint {
    @POST("login")
    fun loginFunc(@Body body: StoryAuthLoginBody): Call<StoryAuthLoginResponse>

    @POST("register")
    fun registerFunc(@Body body: StoryAuthRegisterBody): Call<StoryAuthRegisterResponse>
}