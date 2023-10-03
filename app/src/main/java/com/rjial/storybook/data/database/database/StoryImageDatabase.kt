package com.rjial.storybook.data.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rjial.storybook.data.database.dao.StoryImageDao
import com.rjial.storybook.data.database.entity.StoryImageEntity

@Database(entities = [StoryImageEntity::class], version = 1)
abstract class StoryImageDatabase: RoomDatabase() {
    abstract fun storyImageDao(): StoryImageDao

    companion object {
        @Volatile
        private var instance: StoryImageDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): StoryImageDatabase {
            return instance ?: synchronized(this) {
                instance = Room.databaseBuilder(context.applicationContext, StoryImageDatabase::class.java, "story_image_database").build()
                return requireNotNull(instance)
            }
        }
    }
}