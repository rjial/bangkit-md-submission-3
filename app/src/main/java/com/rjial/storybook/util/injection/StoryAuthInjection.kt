package com.rjial.storybook.util.injection

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rjial.storybook.data.preference.AppPreferences
import com.rjial.storybook.repository.StoryAuthAppPrefRepository

object StoryAuthAppPrefInjection {
    fun provideRepository(dataStore: DataStore<Preferences>): StoryAuthAppPrefRepository {
        val preferences = AppPreferences.getInstance(dataStore)
        return StoryAuthAppPrefRepository.getInstance(preferences)
    }
}