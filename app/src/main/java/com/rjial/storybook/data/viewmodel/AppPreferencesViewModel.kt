package com.rjial.storybook.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.rjial.storybook.util.ResponseResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AppPreferencesViewModel(private val repository: StoryAuthAppPrefRepository): ViewModel() {
    private val authService: StoryAuthEndpoint = StoryAuthService.getInstance().getService()
    private val _loginResponse: MutableLiveData<ResponseResult<StoryAuthLoginResponse?>> = MutableLiveData<ResponseResult<StoryAuthLoginResponse?>>()
    var loginResponse: LiveData<ResponseResult<StoryAuthLoginResponse?>> = _loginResponse
    private val _registerResponse: MutableLiveData<ResponseResult<StoryAuthRegisterResponse?>> = MutableLiveData<ResponseResult<StoryAuthRegisterResponse?>>()
    var registerResponse: LiveData<ResponseResult<StoryAuthRegisterResponse?>> = _registerResponse
    fun getTokenAuth(): LiveData<String> {
        return repository.getTokenAuth()
    }

    fun setTokenAuth(token: String) {
        viewModelScope.launch {
            repository.setTokenAuth(token)
        }
    }

    fun doLogin(email: String, password: String) {
        try {
            _loginResponse.value = ResponseResult.Loading
            authService.loginFunc(StoryAuthLoginBody(email, password)).enqueue(object: Callback<StoryAuthLoginResponse> {
                override fun onResponse(
                    call: Call<StoryAuthLoginResponse>,
                    response: Response<StoryAuthLoginResponse>
                ) {
                        val body = response.body()
                        if (response.isSuccessful && body != null) {
                            if(!body.error) {
                                setTokenAuth(body.loginResult!!.token!!)
                            } else {
                                val jsonObject = JSONObject(response.errorBody()?.string()!!)
                                _loginResponse.value = ResponseResult.Error("Failed to login : ${jsonObject.getString("message")}")
                            }
                            _loginResponse.value = ResponseResult.Success(body)
                        } else {
                            val jsonObject = JSONObject(response.errorBody()?.string()!!)
                            _loginResponse.value = ResponseResult.Error("Failed to login : ${jsonObject.getString("message")}")
                        }
                }

                override fun onFailure(call: Call<StoryAuthLoginResponse>, t: Throwable) {
                    _loginResponse.value = ResponseResult.Error("Failed to login : ${t.message}")
                }

            })
        }catch (exc: Exception) {
            _loginResponse.value = ResponseResult.Error("Failed to login : ${exc.message}")
        }
    }
    fun doRegister(email: String, name: String, password: String, registerCallback: (String) -> Unit) {
        try {
            _registerResponse.value = ResponseResult.Loading
            authService.registerFunc(StoryAuthRegisterBody(email, name, password)).enqueue(object: Callback<StoryAuthRegisterResponse> {
                override fun onResponse(
                    call: Call<StoryAuthRegisterResponse>,
                    response: Response<StoryAuthRegisterResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        if(!body.error!!) {
                            registerCallback(body.message!!)
                        } else {
                            val jsonObject = JSONObject(response.errorBody()?.string()!!)
                            _registerResponse.value = ResponseResult.Error("Failed to login : ${jsonObject.getString("message")}")
                        }
                        _registerResponse.value = ResponseResult.Success(body)
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string()!!)
                        _registerResponse.value = ResponseResult.Error("Failed to login : ${jsonObject.getString("message")}")
                    }
                }

                override fun onFailure(call: Call<StoryAuthRegisterResponse>, t: Throwable) {
                    _registerResponse.value = ResponseResult.Error("Failed to login : ${t.message}")
                }

            })
        } catch (exc: Exception){
            _registerResponse.value = ResponseResult.Error("Failed to login : ${exc.message}")
        }
    }


    fun isAuthorized(): LiveData<Boolean> {
        return repository.isAuthorized().asLiveData()
    }
}