package com.rjial.storybook.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.rjial.storybook.data.preference.AppPreferences
import com.rjial.storybook.data.preference.datastore
import com.rjial.storybook.data.viewmodel.AppPreferencesViewModel
import com.rjial.storybook.data.viewmodel.factory.AppPrefVMFactory
import com.rjial.storybook.databinding.ActivitySplashScreenBinding
import com.rjial.storybook.network.service.StoryAuthService
import com.rjial.storybook.ui.authentication.login.LoginAuthActivity
import com.rjial.storybook.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainScope.launch {
            delay(3000L)
            val datastore = application.datastore
            val preferences = AppPreferences.getInstance(datastore)
            val viewModel = ViewModelProvider(this@SplashScreenActivity, AppPrefVMFactory(preferences))[AppPreferencesViewModel::class.java]
            viewModel.isAuthorized().observe(this@SplashScreenActivity) {
                if (it) {
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@SplashScreenActivity, LoginAuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}