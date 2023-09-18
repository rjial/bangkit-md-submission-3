package com.rjial.storybook.network.service

import com.rjial.storybook.network.endpoint.StoryAuthEndpoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StoryAuthService {
    companion object {

        @Volatile
        private var instance: StoryAuthService? = null

        @JvmStatic
        fun getInstance(): StoryAuthService {
            return instance ?: synchronized(this) {
                instance = StoryAuthService()
                return instance ?: StoryAuthService()
            }

        }
    }

    fun getService(): StoryAuthEndpoint {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(StoryAuthEndpoint::class.java)
    }
}