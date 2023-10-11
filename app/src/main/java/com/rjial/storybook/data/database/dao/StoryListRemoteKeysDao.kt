package com.rjial.storybook.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rjial.storybook.data.database.entity.StoryListRemoteKeys

@Dao
interface StoryListRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<StoryListRemoteKeys>)

    @Query("SELECT * FROM story_entity_keys WHERE id = :id")
    suspend fun getStoryRemoteKeys(id: String): StoryListRemoteKeys?

    @Query("DELETE FROM story_entity_keys")
    suspend fun deleteAllStoryRemoteKeys()
}