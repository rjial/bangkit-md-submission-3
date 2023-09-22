package com.rjial.storybook.network.service

import com.rjial.storybook.network.endpoint.StoryEndpoint
import com.rjial.storybook.network.response.StoryListResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class StoryListService {

    companion object {
        @Volatile
        private var instance: StoryListService? = null

        @JvmStatic
        fun getInstance(): StoryListService {
            return instance ?: synchronized(this) {
                instance = StoryListService()
                return StoryListService()
            }
        }

    }

    fun getService(token: String): StoryEndpoint {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = Interceptor {
            val req = it.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            it.proceed(requestHeaders)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(StoryEndpoint::class.java)
    }
}