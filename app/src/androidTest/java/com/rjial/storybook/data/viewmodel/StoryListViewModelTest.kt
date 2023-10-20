package com.rjial.storybook.data.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rjial.storybook.MainDispatcherRule
import com.rjial.storybook.data.database.database.StoryListDatabase
import com.rjial.storybook.getOrAwaitValue
import com.rjial.storybook.network.endpoint.StoryEndpoint
import com.rjial.storybook.network.service.StoryListService
import com.rjial.storybook.repository.StoryListRepository
import com.rjial.storybook.ui.main.adapter.StoryListAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var storyListRepository: StoryListRepository

    private val mockWebServer = MockWebServer()
    private lateinit var apiService: StoryEndpoint
    private lateinit var mockResponse: MockResponse
    private lateinit var mockDispatcher: Dispatcher
    private lateinit var database: StoryListDatabase

    @Before
    fun setup() {
//        mockDispatcher = object : Dispatcher() {
//            override fun dispatch(request: RecordedRequest): MockResponse {
//                return when(request.path) {
//                    "/stories" -> {
//                        val response = MockResponse()
//                            .setResponseCode(HttpURLConnection.HTTP_OK)
//                            .setBody("{\"error\":false,\"message\":\"Stories fetched successfully\",\"listStory\":[{\"id\":\"story-4FaMgfYKayr_SWRz\",\"name\":\"Reviewer\",\"description\":\"Gallery\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697097305195_VOZo9X0a.jpg\",\"createdAt\":\"2023-10-12T07:55:05.197Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-7gWVHg5gWcvh3-s4\",\"name\":\"Reviewer\",\"description\":\"Camera\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096889742_Gj5pv4Bq.jpg\",\"createdAt\":\"2023-10-12T07:48:09.746Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-KgUtbJn-tUco3mOP\",\"name\":\"kusuemon\",\"description\":\"jsjsjsjs\\nsjjsjjsjs\\njsjsjsjjs\\n\\n\\nksksksksksk\\njzjsjsjjs\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096786306_V3dFZh8E.jpg\",\"createdAt\":\"2023-10-12T07:46:26.308Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-jTB9if86M5lUbBCq\",\"name\":\"kusuemon\",\"description\":\"jsjs\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096762524_hOw-gB8M.jpg\",\"createdAt\":\"2023-10-12T07:46:02.527Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-Z7PyAEmJ9J2b8lj7\",\"name\":\"kusuemon\",\"description\":\"hshsjs\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096744795_d0xlAht3.jpg\",\"createdAt\":\"2023-10-12T07:45:44.797Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-eNs7BZhBNuzG5g58\",\"name\":\"kusuemon\",\"description\":\"hshsjs\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096744313_j15rkxBD.jpg\",\"createdAt\":\"2023-10-12T07:45:44.315Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-gGHJBuk0C7rtFP-l\",\"name\":\"kusuemon\",\"description\":\"hshsjs\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096743965_DRwQTBbi.jpg\",\"createdAt\":\"2023-10-12T07:45:43.967Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-GMjTMs8UmFYhSgHX\",\"name\":\"kusuemon\",\"description\":\"hshsjs\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096743419_jGyRBTSX.jpg\",\"createdAt\":\"2023-10-12T07:45:43.421Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-E3NBPXCG-vPBqKfe\",\"name\":\"Maguire\",\"description\":\"kiw kiw\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096436009_-rsjHzmR.jpg\",\"createdAt\":\"2023-10-12T07:40:36.012Z\",\"lat\":null,\"lon\":null},{\"id\":\"story-R-HNhWG5MlhFZtKu\",\"name\":\"Maguire\",\"description\":\"tess\",\"photoUrl\":\"https://story-api.dicoding.dev/images/stories/photos-1697096349322_bK7Ur2Dc.jpg\",\"createdAt\":\"2023-10-12T07:39:09.324Z\",\"lat\":null,\"lon\":null}]}")
//                        return response
//                    }
//                    else -> MockResponse().setResponseCode(404)
//                }
//
//            }
//
//        }
//        mockWebServer.dispatcher = mockDispatcher
//        mockWebServer.start()
        apiService = StoryListService.getInstance().getService("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWpOR2VaU243MXJfb3FVSFkiLCJpYXQiOjE2OTUxNzc5NTR9.2gL0HuOtgrnSawJ4pQb-O_H7KTJAy4RHIAu41OKaQ6A")
//        apiService = Retrofit.Builder()
//            .baseUrl(mockWebServer.url("/"))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(StoryEndpoint::class.java)
//        mockWebServer.start()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoryListDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
//        mockWebServer.shutdown()
    }
    @Test
    fun shouldNotNull() = runTest {
        val latch = CountDownLatch(1)

//        val latch = CountDownLatch(1)
//        val remoteMediator = StoryListRemoteMediator(
//            database,
//            apiService
//        )
//        val pagingState = PagingState<Int, ListStoryItem>(
//            listOf(),
//            null,
//            PagingConfig(10),
//            10
//        )
//        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
//        when(result) {
//            is RemoteMediator.MediatorResult.Success -> {
//                Log.d("RESULT", result.toString())
//                assertNotNull(result)
//            }
//
//            is RemoteMediator.MediatorResult.Error -> {
//                fail("Expected a success result")
//            }
//        }
//        if(result is RemoteMediator.MediatorResult.Success) {
////            val resultData = (result as RemoteMediator.MediatorResult.Success).data
//            assertNotNull(result)
//        } else {
//            fail("Expected a success result")
//        }
        storyListRepository = StoryListRepository(apiService, database)
//        val dummyStory = StoryListDummy.generateStoryListEntity()
//        val data: PagingData<ListStoryItem> = StoryListFakePagingSource.snapshot(dummyStory)
//        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
//        expectedStories.value = data
//        Log.d("API_TEST", storyListRepository.getAllStoriesNew(false).getOrAwaitValue().toString())
//        Mockito.`when`(storyListRepository.getAllStoriesNew(false)).thenReturn(expectedStories)

        val storyListViewModel = StoryListViewModel(storyListRepository)
        val actualStories = storyListViewModel.getAllStories().getOrAwaitValue()
//        val actualStories = storyListViewModel.getAllStories().observeForever {items ->
//            items.map {
//                Log.d("API_TEST", it.toString())
//            }
//        }

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.StoryListDiffCallback,
            updateCallback = noopListUpdateCallback,
        )
        differ.submitData(actualStories)
//
        Log.d("API_TEST", differ.snapshot().toString())
//        differ.submitData(actualStories)
//        Log.d("API_TEST", differ.snapshot().items.toString())

//        assertNotNull(differ.snapshot().items)
//        assertTrue(differ.snapshot().items.isNotEmpty())
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
//class StoryListFakePagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>() {
//    companion object {
//        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
//            return PagingData.from(items)
//        }
//    }
//    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
//        return 0
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
//        return LoadResult.Page(emptyList(), 0, 1)
//    }
//
//}
