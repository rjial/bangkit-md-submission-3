package com.rjial.storybook.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rjial.storybook.data.preference.AppPreferences
import com.rjial.storybook.network.endpoint.StoryAuthEndpoint
import com.rjial.storybook.network.response.StoryAuthLoginBody
import com.rjial.storybook.network.response.StoryAuthLoginResponse
import com.rjial.storybook.network.service.StoryAuthService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppPreferencesViewModel(private val pref: AppPreferences): ViewModel() {
    private val authService: StoryAuthEndpoint = StoryAuthService.getInstance().getService()
    private val _loginResponse: MutableLiveData<StoryAuthLoginResponse?> = MutableLiveData<StoryAuthLoginResponse?>()
    var loginResponse: LiveData<StoryAuthLoginResponse?> = _loginResponse
    fun getTokenAuth(): LiveData<String> {
        return pref.getTokenAuth().asLiveData()
    }

    fun setTokenAuth(token: String) {
        viewModelScope.launch {
            pref.setTokenAuth(token)
        }
    }

    fun doLogin(email: String, password: String) {
        authService.loginFunc(StoryAuthLoginBody(email, password)).enqueue(object: Callback<StoryAuthLoginResponse> {
            override fun onResponse(
                call: Call<StoryAuthLoginResponse>,
                response: Response<StoryAuthLoginResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if(!body.error) {
                        setTokenAuth(body.loginResult!!.token!!)
                    }
                    _loginResponse.value = body
                } else {
                    throw Exception("Failed to login ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryAuthLoginResponse>, t: Throwable) {
                throw Exception("Error : ${t.message}")
            }

        })
    }

    fun isAuthorized(): LiveData<Boolean> {
        return pref.isAuthorized().asLiveData()
    }
}