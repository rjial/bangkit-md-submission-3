package com.rjial.storybook.data.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rjial.storybook.network.endpoint.StoryEndpoint
import com.rjial.storybook.network.response.ListStoryItem

class StoryListPagingSource(private val storyEndpoint: StoryEndpoint, private val location: Boolean = false): PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = storyEndpoint.getAllStoriesSus(location = if (location) 1 else 0, page = position, size = params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 2
            )
        } catch (exc: Exception) {
            return LoadResult.Error(exc)
        }
    }
}