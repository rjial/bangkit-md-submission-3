package com.rjial.storybook.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rjial.storybook.network.response.ListStoryItem

@Dao
interface StoryListItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<ListStoryItem>)

    @Query("SELECT * from liststoryitem")
    fun getAllStories(): PagingSource<Int, ListStoryItem>

    @Query("SELECT * from liststoryitem")
    suspend fun getAllStoriesSus(): List<ListStoryItem>

    @Query("DELETE from liststoryitem")
    suspend fun deleteAllStories()
}