package com.rjial.storybook.ui.splashscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.rjial.storybook.data.preference.AppPreferences
import com.rjial.storybook.data.preference.datastore
import com.rjial.storybook.data.viewmodel.AppPreferencesViewModel
import com.rjial.storybook.data.viewmodel.factory.AppPrefVMFactory
import com.rjial.storybook.databinding.ActivitySplashScreenBinding
import com.rjial.storybook.repository.StoryAuthAppPrefRepository
import com.rjial.storybook.ui.authentication.login.LoginAuthActivity
import com.rjial.storybook.ui.main.MainActivity
import com.rjial.storybook.util.injection.StoryAuthAppPrefInjection
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
        val animY = ObjectAnimator.ofFloat(binding.imgSplashScreen, View.TRANSLATION_Y, 120f, 0f).apply {
            duration = 1000
        }
        val animAlpha = ObjectAnimator.ofFloat(binding.imgSplashScreen, View.ALPHA, 0f, 100f).apply {
            duration = 10000
        }
        AnimatorSet().apply {
            playTogether(animY, animAlpha)
            start()
        }
        mainScope.launch {
            delay(3000L)
            val viewModel = ViewModelProvider(this@SplashScreenActivity, AppPrefVMFactory(StoryAuthAppPrefInjection.provideRepository(application.datastore)))[AppPreferencesViewModel::class.java]
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