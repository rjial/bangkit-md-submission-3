package com.rjial.storybook.util.injection

import android.content.Context
import com.rjial.storybook.data.preference.AppPreferences
import com.rjial.storybook.data.preference.datastore
import com.rjial.storybook.network.service.StoryListService
import com.rjial.storybook.repository.StoryListRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object StoryListInjection {
    fun provideInjection(context: Context): StoryListRepository {
        val datastore = context.datastore
        val pref = AppPreferences.getInstance(datastore)
        val token = runBlocking { pref.getTokenAuth().first() }
        val apiService = StoryListService.getInstance().getService(token)
        return StoryListRepository.getInstance(apiService)
    }

}