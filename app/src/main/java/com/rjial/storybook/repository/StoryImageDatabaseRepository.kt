package com.rjial.storybook.repository

import android.content.Context
import com.rjial.storybook.data.database.dao.StoryImageDao
import com.rjial.storybook.data.database.database.StoryImageDatabase
import com.rjial.storybook.data.database.entity.StoryImageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StoryImageDatabaseRepository(context: Context) {
    private var storyImageDao: StoryImageDao
    private var executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = StoryImageDatabase.getInstance(context.applicationContext)
        storyImageDao = db.storyImageDao()
    }

    fun getAllStoryImagesFlow(): Flow<List<StoryImageEntity>> = flow {
        emit(storyImageDao.getAllStoryImagesFlow())
    }

    fun insert(storyImage: StoryImageEntity) {
        executorService.execute { storyImageDao.insert(storyImage) }
    }

    fun deleteAllStoryImages() {
        executorService.execute { storyImageDao.deleteAllStoryImage() }
    }
}