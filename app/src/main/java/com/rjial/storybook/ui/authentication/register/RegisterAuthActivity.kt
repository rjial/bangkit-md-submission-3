package com.rjial.storybook.ui.authentication.register

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
import com.rjial.storybook.databinding.ActivityRegisterAuthBinding
import com.rjial.storybook.network.response.StoryAuthRegisterResponse
import com.rjial.storybook.ui.authentication.login.LoginAuthActivity
import com.rjial.storybook.util.injection.StoryAuthAppPrefInjection
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterAuthBinding
    private lateinit var appPreferenceViewModel: AppPreferencesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAuthBinding.inflate(layoutInflater)
        appPreferenceViewModel = ViewModelProvider(this, AppPrefVMFactory.getInstance(
            StoryAuthAppPrefInjection.provideRepository(application.datastore)))[AppPreferencesViewModel::class.java]
        setContentView(binding.root)
        with(binding) {
            btnStoryRegisterProceed.setOnClickListener {
                doRegister(edtEmailRegister.text.toString(), edtNamaRegister.text.toString(), edtPasswordRegister.text.toString())
            }
            btnStoryToLogin.setOnClickListener {
                val intent = Intent(this@RegisterAuthActivity, LoginAuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun isLoading(loading: Boolean) {
        when(loading) {
            true -> {
                binding.btnStoryRegisterProceed.visibility = View.GONE
                binding.pbRegisterLoading.visibility = View.VISIBLE
            }
            false -> {
                binding.btnStoryRegisterProceed.visibility = View.VISIBLE
                binding.pbRegisterLoading.visibility = View.GONE
            }
        }
    }
    private fun doRegister(email: String, name: String, password: String) {
        isLoading(true)
        lifecycleScope.launch {
            val result = appPreferenceViewModel.doRegisterSus(email, name, password)
            result.onSuccess {
                isLoading(false)
                val intent = Intent(this@RegisterAuthActivity, LoginAuthActivity::class.java)
                startActivity(intent)
                finish()
            }.onFailure {
                when(it) {
                    is HttpException -> {
                        val errorBody = Gson().fromJson(it.response()?.errorBody()?.string(), StoryAuthRegisterResponse::class.java)
                        if (errorBody.error) Toast.makeText(this@RegisterAuthActivity, errorBody.message, Toast.LENGTH_SHORT).show()
                    }
                    is Exception -> {
                        Toast.makeText(this@RegisterAuthActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                isLoading(false)
            }
        }

    }
}