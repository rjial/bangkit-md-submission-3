package com.rjial.storybook.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rjial.storybook.R
import com.rjial.storybook.data.viewmodel.StoryListViewModel
import com.rjial.storybook.data.viewmodel.factory.StoryListVMFactory
import com.rjial.storybook.databinding.ActivityMainBinding
import com.rjial.storybook.ui.authentication.login.LoginAuthActivity
import com.rjial.storybook.ui.main.adapter.StoryListAdapter
import com.rjial.storybook.util.ResponseResult

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyListViewModel: StoryListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        storyListViewModel = ViewModelProvider(this, StoryListVMFactory(application))[StoryListViewModel::class.java]
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val adapter = StoryListAdapter()
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rcvStory.layoutManager = layoutManager
        binding.rcvStory.addItemDecoration(dividerItemDecoration)
        binding.rcvStory.adapter = adapter
        storyListViewModel.getAllStories().observe(this) {
            if (it != null) {
                when (it) {
                    is ResponseResult.Loading -> loadingFunc(true)
                    is ResponseResult.Success -> {
                        loadingFunc(false)
                        adapter.submitList(it.data.listStory)
                    }
                    is ResponseResult.Error -> {
                        loadingFunc(false)
                        Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadingFunc(loading: Boolean) {
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
    }
}