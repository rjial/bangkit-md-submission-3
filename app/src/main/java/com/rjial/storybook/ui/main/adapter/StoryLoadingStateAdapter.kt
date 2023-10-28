package com.rjial.storybook.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rjial.storybook.databinding.ItemPagingRetryBinding

class StoryLoadingStateAdapter(private val retry:() -> Unit): LoadStateAdapter<StoryLoadingStateAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemPagingRetryBinding, retry: () -> Unit): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetryPaging.setOnClickListener { retry.invoke() }
        }
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.txtErrorMsgPaging.text = loadState.error.localizedMessage
            }
            binding.pbPagingLoading.isVisible = loadState is LoadState.Loading
            binding.btnRetryPaging.isVisible = loadState is LoadState.Error
            binding.txtErrorMsgPaging.isVisible = loadState is LoadState.Error

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = ItemPagingRetryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, retry)
    }
}