package com.rjial.storybook.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rjial.storybook.data.database.entity.StoryImageEntity

@Dao
interface StoryImageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(image:  StoryImageEntity)
    @Update
    fun update(image:  StoryImageEntity)
    @Delete
    fun delete(image: StoryImageEntity)

    @Query("DELETE FROM storyimageentity")
    fun deleteAllStoryImage()
    @Query("SELECT * from storyimageentity")
    fun getAllStoryImages(): LiveData<List<StoryImageEntity>>
    @Query("SELECT * from storyimageentity")
    fun getAllStoryImagesFlow(): List<StoryImageEntity>

}