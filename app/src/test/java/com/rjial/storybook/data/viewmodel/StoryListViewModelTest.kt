package com.rjial.storybook.data.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.rjial.storybook.MainDispatcherRule
import com.rjial.storybook.StoryListDummy
import com.rjial.storybook.getOrAwaitValue
import com.rjial.storybook.network.response.ListStoryItem
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.repository.StoryListRepository
import com.rjial.storybook.ui.main.adapter.StoryListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private var storyListRepository: StoryListRepository = Mockito.mock(StoryListRepository::class.java)
    private lateinit var dummyStories: List<ListStoryItem>

    @Before
    fun beforeRun() {
        dummyStories = StoryListDummy.generateStoryListEntity()
    }

    @Test
    fun `Memastikan data tidak null`() = runTest {
        val data: PagingData<ListStoryItem> = StoryListFakePagingSource.snapshot(StoryListResponse(dummyStories, false, "success"))
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyListRepository.getAllStoriesPagerLV(false)).thenReturn(expectedStories)

        val storyListViewModel = StoryListViewModel(storyListRepository)
        val actualStories: PagingData<ListStoryItem> = storyListViewModel.getAllStories(false).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.StoryListDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)
        assertNotNull(differ.snapshot())
    }
    @Test
    fun `Memastikan jumlah data sesuai dengan yang diharapkan`() = runTest {
        val data: PagingData<ListStoryItem> = StoryListFakePagingSource.snapshot(StoryListResponse(dummyStories, false, "success"))
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyListRepository.getAllStoriesPagerLV(false)).thenReturn(expectedStories)

        val storyListViewModel = StoryListViewModel(storyListRepository)
        val actualStories: PagingData<ListStoryItem> = storyListViewModel.getAllStories(false).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.StoryListDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)
        assertEquals(dummyStories.size, differ.snapshot().items.size)
    }
    @Test
    fun `Memastikan data pertama yang dikembalikan sesuai`() = runTest {
        val data: PagingData<ListStoryItem> = StoryListFakePagingSource.snapshot(StoryListResponse(dummyStories, false, "success"))
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyListRepository.getAllStoriesPagerLV(false)).thenReturn(expectedStories)

        val storyListViewModel = StoryListViewModel(storyListRepository)
        val actualStories: PagingData<ListStoryItem> = storyListViewModel.getAllStories(false).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.StoryListDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)
        assertEquals(dummyStories[0], differ.snapshot().items[0])
    }
    @Test
    fun `Memastikan jumlah data yang dikembalikan nol`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyListRepository.getAllStoriesPagerLV(false)).thenReturn(expectedStories)

        val storyListViewModel = StoryListViewModel(storyListRepository)
        val actualStories: PagingData<ListStoryItem> = storyListViewModel.getAllStories(false).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.StoryListDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)
        assertEquals(0, differ.snapshot().items.size)
    }
    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}

class StoryListFakePagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>() {

    companion object {
        fun snapshot(res: StoryListResponse): PagingData<ListStoryItem> {
            return PagingData.from(res.listStory)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

}