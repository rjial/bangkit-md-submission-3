package com.rjial.storybook.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rjial.storybook.network.endpoint.StoryAuthEndpoint
import com.rjial.storybook.network.response.StoryAuthLoginBody
import com.rjial.storybook.network.response.StoryAuthLoginResponse
import com.rjial.storybook.network.response.StoryAuthRegisterBody
import com.rjial.storybook.network.response.StoryAuthRegisterResponse
import com.rjial.storybook.network.service.StoryAuthService
import com.rjial.storybook.repository.StoryAuthAppPrefRepository
import kotlinx.coroutines.launch

class AppPreferencesViewModel(private val repository: StoryAuthAppPrefRepository): ViewModel() {
    private val authService: StoryAuthEndpoint = StoryAuthService.getInstance().getService()
    fun getTokenAuth(): LiveData<String> {
        return repository.getTokenAuth()
    }

    fun setTokenAuth(token: String) {
        viewModelScope.launch {
            repository.setTokenAuth(token)
        }
    }

    fun doLogout() {
        viewModelScope.launch {
            repository.purgeAuth()
        }
    }

    suspend fun doLoginSus(email: String, password: String): Result<StoryAuthLoginResponse> {
        val loginRes = kotlin.runCatching { authService.loginFuncSus(StoryAuthLoginBody(email, password)) }
        loginRes.onSuccess {
            setTokenAuth(it.loginResult?.token!!)
        }.onFailure {
            return Result.failure(it)
        }
        return loginRes
    }

    suspend fun doRegisterSus(email: String,name: String, password: String): Result<StoryAuthRegisterResponse> {
        val registerRes = kotlin.runCatching { authService.registerFuncSus(StoryAuthRegisterBody(name, email, password)) }
        registerRes.onFailure {
            return Result.failure(it)
        }
        return registerRes
    }

    fun isAuthorized(): LiveData<Boolean> {
        return repository.isAuthorized().asLiveData()
    }
}