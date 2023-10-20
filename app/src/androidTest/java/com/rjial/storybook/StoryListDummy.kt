package com.rjial.storybook

import com.rjial.storybook.network.response.ListStoryItem

object StoryListDummy {
    fun generateStoryListEntity(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 1..10) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1696969373297_GVQkveZw.jpg",
                "2023-10-10T20:22:53.305Z",
                "coyy",
                "tes",
                null,
                "story-rF3QTNdBc5rej_hN",
                null
            )
            storyList.add(story)
        }
        return storyList
    }
}