package com.rjial.storybook.data.paging.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rjial.storybook.data.database.database.StoryListDatabase
import com.rjial.storybook.data.database.entity.StoryListRemoteKeys
import com.rjial.storybook.network.endpoint.StoryEndpoint
import com.rjial.storybook.network.response.ListStoryItem
import com.rjial.storybook.util.wrapEspressoIdlingResource

@OptIn(ExperimentalPagingApi::class)
class StoryListRemoteMediator(
    private val database: StoryListDatabase,
    private val apiService: StoryEndpoint,
    private val withLoc: Boolean = false
): RemoteMediator<Int, ListStoryItem>() {


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey

            }
        }
        try {
            wrapEspressoIdlingResource {
                val responseData = apiService.getAllStoriesSus(location = if (withLoc) 1 else 0, page = page, size = state.config.pageSize)

            val endOfPaginationReached = responseData.listStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyListRemoteKeysDao().deleteAllStoryRemoteKeys()
                    database.storyListDao().deleteAllStories()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    StoryListRemoteKeys(id = it.id, prevKey, nextKey)
                }
                database.storyListRemoteKeysDao().insertAll(keys)
                database.storyListDao().insertStory(responseData.listStory)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }
        }catch (exc: Exception) {
            return MediatorResult.Error(exc)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): StoryListRemoteKeys? {
        return state.pages.lastOrNull {it.data.isNotEmpty()}?.data?.lastOrNull()?.let {
            database.storyListRemoteKeysDao().getStoryRemoteKeys(it.id)
        }
    }
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): StoryListRemoteKeys? {
        return state.pages.firstOrNull {it.data.isNotEmpty()}?.data?.firstOrNull()?.let {
            database.storyListRemoteKeysDao().getStoryRemoteKeys(it.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): StoryListRemoteKeys? {
        return state.anchorPosition?.let {position ->
            state.closestItemToPosition(position)?.id?.let {id ->
                database.storyListRemoteKeysDao().getStoryRemoteKeys(id)
            }
        }
    }
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}