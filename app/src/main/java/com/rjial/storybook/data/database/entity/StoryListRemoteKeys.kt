package com.rjial.storybook.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("story_entity_keys")
data class StoryListRemoteKeys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)