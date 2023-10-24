package com.rjial.storybook.ui.authentication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.rjial.storybook.data.preference.datastore
import com.rjial.storybook.data.viewmodel.AppPreferencesViewModel
import com.rjial.storybook.data.viewmodel.factory.AppPrefVMFactory
import com.rjial.storybook.databinding.ActivityLoginAuthBinding
import com.rjial.storybook.network.response.StoryAuthLoginResponse
import com.rjial.storybook.ui.authentication.register.RegisterAuthActivity
import com.rjial.storybook.ui.main.MainActivity
import com.rjial.storybook.util.injection.StoryAuthAppPrefInjection
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginAuthBinding
    private lateinit var appPreferenceViewModel: AppPreferencesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAuthBinding.inflate(layoutInflater)
        appPreferenceViewModel = ViewModelProvider(this, AppPrefVMFactory.getInstance(StoryAuthAppPrefInjection.provideRepository(application.datastore)))[AppPreferencesViewModel::class.java]


//        val loginObserver: Observer<ResponseResult<StoryAuthLoginResponse?>> = Observer {
//            when(it) {
//                is ResponseResult.Success -> {
//                    binding.btnStoryLoginProceed.visibility = View.VISIBLE
//                    binding.pbLoginLoading.visibility = View.GONE
//                    appPreferenceViewModel.loginResponse.removeObservers(this@LoginAuthActivity)
//                    val intent = Intent(this@LoginAuthActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//                is ResponseResult.Error -> {
//                    binding.btnStoryLoginProceed.visibility = View.VISIBLE
//                    binding.pbLoginLoading.visibility = View.GONE
//                    Toast.makeText(this@LoginAuthActivity, it.error, Toast.LENGTH_SHORT).show()
//                    appPreferenceViewModel.loginResponse.removeObservers(this@LoginAuthActivity)
//                }
//
//                ResponseResult.Loading -> {
//                    binding.btnStoryLoginProceed.visibility = View.GONE
//                    binding.pbLoginLoading.visibility = View.VISIBLE
//                    Toast.makeText(this@LoginAuthActivity, "Loading", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
        setContentView(binding.root)

        with(binding) {
            btnStoryLoginProceed.setOnClickListener {
                doLogin(edtEmailLogin.text.toString(), edtPasswordLogin.text.toString())
//                appPreferenceViewModel.doLogin(edtEmailLogin.text.toString(), edtPasswordLogin.text.toString())
//                appPreferenceViewModel.loginResponse.observe(this@LoginAuthActivity, loginObserver)
            }
            btnStoryToRegister.setOnClickListener {
                val intent = Intent(this@LoginAuthActivity, RegisterAuthActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun isLoading(loading: Boolean) {
        when(loading) {
            true -> {
                binding.btnStoryLoginProceed.visibility = View.GONE
                binding.pbLoginLoading.visibility = View.VISIBLE
            }
            false -> {
                binding.btnStoryLoginProceed.visibility = View.VISIBLE
                binding.pbLoginLoading.visibility = View.GONE
            }

        }
    }
    private fun doLogin(email: String, password: String) {
        isLoading(true)
        lifecycleScope.launch {
            val result = appPreferenceViewModel.doLoginSus(email, password)
            result.onSuccess {
                isLoading(false)
                val intent = Intent(this@LoginAuthActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.onFailure {
                when(it) {
                    is HttpException -> {
                        val errorBody = Gson().fromJson(it.response()?.errorBody()?.string(), StoryAuthLoginResponse::class.java)
                        if (errorBody.error) Toast.makeText(this@LoginAuthActivity, errorBody.message, Toast.LENGTH_SHORT).show()
                    }
                    is Exception -> {
                        Toast.makeText(this@LoginAuthActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                isLoading(false)
            }

        }

    }
}