package com.rjial.storybook.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rjial.storybook.R
import com.rjial.storybook.data.preference.datastore
import com.rjial.storybook.data.viewmodel.AppPreferencesViewModel
import com.rjial.storybook.data.viewmodel.StoryListViewModel
import com.rjial.storybook.data.viewmodel.factory.AppPrefVMFactory
import com.rjial.storybook.data.viewmodel.factory.StoryListVMFactory
import com.rjial.storybook.databinding.ActivityMainBinding
import com.rjial.storybook.ui.authentication.login.LoginAuthActivity
import com.rjial.storybook.ui.main.adapter.StoryListAdapter
import com.rjial.storybook.ui.map.StoryMapActivity
import com.rjial.storybook.ui.story.add.AddStoryActivity
import com.rjial.storybook.util.injection.StoryAuthAppPrefInjection
import com.rjial.storybook.util.injection.StoryListInjection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyListViewModel: StoryListViewModel
    private lateinit var adapter: StoryListAdapter
    private var firstLoad: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        storyListViewModel = ViewModelProvider(this, StoryListVMFactory(StoryListInjection.provideInjection(this)))[StoryListViewModel::class.java]
        adapter = StoryListAdapter()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rcvStory.layoutManager = layoutManager
        binding.rcvStory.addItemDecoration(dividerItemDecoration)
        binding.rcvStory.adapter = adapter
        loadStories()
        binding.btnCreateStoryFAB.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
        binding.appBarStory.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    val viewModel = ViewModelProvider(this@MainActivity, AppPrefVMFactory(
                        StoryAuthAppPrefInjection.provideRepository(application.datastore))
                    )[AppPreferencesViewModel::class.java]
                    viewModel.doLogout()
                    StoryListInjection.stopApiInstance()
                    finish()
                    val intent = Intent(this@MainActivity, LoginAuthActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_map -> {
                    val intent = Intent(this@MainActivity, StoryMapActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadingFunc(loading: Boolean) {
//        if (!firstLoad) {
            when(loading) {
                true -> {
                    binding.pbStoryLoading.visibility = View.VISIBLE
                    binding.rcvStory.visibility = View.GONE
                }
                false -> {
                    binding.pbStoryLoading.visibility = View.GONE
                    binding.rcvStory.visibility = View.VISIBLE
                }
            }
//            firstLoad = !firstLoad
//        }
    }

    override fun onResume() {
        super.onResume()
        loadStories()
    }

    private fun loadStories() {
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {loadState ->
                when(loadState.source.refresh) {
                    is LoadState.Error -> {
                        Toast.makeText(this@MainActivity, (loadState.source.refresh as LoadState.Error).error.message, Toast.LENGTH_SHORT).show()
                        loadingFunc(false)
                    }
                    LoadState.Loading -> {
                        if (firstLoad) loadingFunc(true)
                    }
                    is LoadState.NotLoading -> {
                        loadingFunc(false)
                        firstLoad = false
                    }
                }
            }
        }
        val load = storyListViewModel.getAllStories()
        if (load.hasActiveObservers()) {
            load.removeObservers(this)
        }
        load.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }
}