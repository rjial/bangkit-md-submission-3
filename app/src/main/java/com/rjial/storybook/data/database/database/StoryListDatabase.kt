package com.rjial.storybook.data.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rjial.storybook.data.database.dao.StoryImageDao
import com.rjial.storybook.data.database.dao.StoryListItemDao
import com.rjial.storybook.data.database.dao.StoryListRemoteKeysDao
import com.rjial.storybook.data.database.entity.StoryImageEntity
import com.rjial.storybook.data.database.entity.StoryListRemoteKeys
import com.rjial.storybook.network.response.ListStoryItem

@Database(entities = [StoryImageEntity::class, ListStoryItem::class, StoryListRemoteKeys::class], version = 2)
abstract class StoryListDatabase: RoomDatabase() {
    abstract fun storyImageDao(): StoryImageDao
    abstract fun storyListDao(): StoryListItemDao
    abstract fun storyListRemoteKeysDao(): StoryListRemoteKeysDao

    companion object {
        @Volatile
        private var instance: StoryListDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): StoryListDatabase {
            return instance ?: synchronized(this) {
                instance = Room.databaseBuilder(context.applicationContext, StoryListDatabase::class.java, "story_image_database")
                    .fallbackToDestructiveMigration()
                    .build()
                return requireNotNull(instance)
            }
        }
    }
}