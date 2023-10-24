package com.rjial.storybook.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.rjial.storybook.network.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore("preferences")
class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        @Volatile
        private var instance: AppPreferences? = null

        @JvmStatic
        fun getInstance(dataStore: DataStore<Preferences>): AppPreferences {
            return instance ?: synchronized(this) {
                return instance ?: AppPreferences(dataStore)
            }
        }
    }

    private val tokenAuth = stringPreferencesKey("token_auth")
    private val userAuth = stringPreferencesKey("user_auth")

    fun getTokenAuth(): Flow<String> {
        return dataStore.data.map {
            it[tokenAuth] ?: ""
        }
    }

    suspend fun setTokenAuth(token: String) {
        dataStore.edit {
            it[tokenAuth] = token
        }
    }

    fun getUserAuth(): Flow<LoginResult> {
        return dataStore.data.map {
            val gson = Gson().fromJson<LoginResult>(it[userAuth], LoginResult::class.java)
            return@map gson!!
        }
    }

    suspend fun setUserAuth(user: String) {
        dataStore.edit {
            it[userAuth] = user
        }
    }

    suspend fun deleteAuth() {
        dataStore.edit {
            it.remove(tokenAuth)
            it.remove(userAuth)
        }
    }

    fun isAuthorized(): Flow<Boolean> {
        return dataStore.data.map {
            if (it[tokenAuth] != null) {
                it[tokenAuth]?.length!! > 0
            } else {
                false
            }
        }
    }


}