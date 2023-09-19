package com.rjial.storybook.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.rjial.storybook.data.preference.AppPreferences
import kotlinx.coroutines.flow.Flow

class StoryAuthAppPrefRepository(
    private val pref: AppPreferences,
) {
    companion object {
        @Volatile
        private var instance: StoryAuthAppPrefRepository? = null

        @JvmStatic
        fun getInstance(pref: AppPreferences): StoryAuthAppPrefRepository {
            return instance ?: synchronized(this) {
                instance = StoryAuthAppPrefRepository(pref)
                return instance ?: StoryAuthAppPrefRepository(pref)
            }
        }
    }
    fun getTokenAuth(): LiveData<String> {
        return pref.getTokenAuth().asLiveData()
    }

    suspend fun setTokenAuth(token: String) {
        return pref.setTokenAuth(token)
    }

    fun isAuthorized(): Flow<Boolean> {
        return pref.isAuthorized()
    }
}