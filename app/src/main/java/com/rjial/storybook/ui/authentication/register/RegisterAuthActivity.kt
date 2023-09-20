package com.rjial.storybook.ui.authentication.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rjial.storybook.R
import com.rjial.storybook.data.preference.datastore
import com.rjial.storybook.data.viewmodel.AppPreferencesViewModel
import com.rjial.storybook.data.viewmodel.factory.AppPrefVMFactory
import com.rjial.storybook.databinding.ActivityRegisterAuthBinding
import com.rjial.storybook.network.response.StoryAuthRegisterResponse
import com.rjial.storybook.ui.authentication.login.LoginAuthActivity
import com.rjial.storybook.ui.main.MainActivity
import com.rjial.storybook.util.ResponseResult
import com.rjial.storybook.util.injection.StoryAuthAppPrefInjection

class RegisterAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterAuthBinding
    private lateinit var appPreferenceViewModel: AppPreferencesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAuthBinding.inflate(layoutInflater)
        appPreferenceViewModel = ViewModelProvider(this, AppPrefVMFactory.getInstance(
            StoryAuthAppPrefInjection.provideRepository(application.datastore)))[AppPreferencesViewModel::class.java]
        setContentView(binding.root)
        val registerObserver: Observer<ResponseResult<StoryAuthRegisterResponse?>> = Observer<ResponseResult<StoryAuthRegisterResponse?>> {
            when(it) {
                is ResponseResult.Loading -> {
                    binding.btnStoryRegisterProceed.visibility = View.GONE
                    binding.pbRegisterLoading.visibility = View.VISIBLE
                }
                is ResponseResult.Success -> {
                    binding.btnStoryRegisterProceed.visibility = View.VISIBLE
                    binding.pbRegisterLoading.visibility = View.GONE
                    appPreferenceViewModel.registerResponse.removeObservers(this@RegisterAuthActivity)
                }
                is ResponseResult.Error -> {
                    binding.btnStoryRegisterProceed.visibility = View.VISIBLE
                    binding.pbRegisterLoading.visibility = View.GONE
                    appPreferenceViewModel.registerResponse.removeObservers(this@RegisterAuthActivity)
                    Toast.makeText(this@RegisterAuthActivity, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        with(binding) {
            btnStoryRegisterProceed.setOnClickListener {
                appPreferenceViewModel.doRegister(edtEmailRegister.text.toString(), edtNamaRegister.text.toString(), edtPasswordRegister.text.toString()) {
                    Toast.makeText(this@RegisterAuthActivity, it, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterAuthActivity, LoginAuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                appPreferenceViewModel.registerResponse.observe(this@RegisterAuthActivity, registerObserver)
            }
            btnStoryToLogin.setOnClickListener {
                val intent = Intent(this@RegisterAuthActivity, LoginAuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}