package com.rjial.storybook.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rjial.storybook.R
import com.rjial.storybook.databinding.ItemStoryBinding
import com.rjial.storybook.network.response.ListStoryItem
import com.rjial.storybook.ui.story.detail.DetailStoryActivity

class StoryListAdapter: ListAdapter<ListStoryItem, StoryListAdapter.ViewHolder>(StoryListDiffCallback) {

    object StoryListDiffCallback: DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.name == newItem.name && oldItem.photoUrl == newItem.photoUrl
        }

    }
    class ViewHolder(val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: ListStoryItem) {
            with(binding) {
                imgStoryItem.load(storyItem.photoUrl) {
                    placeholder(R.drawable.ic_launcher_background)
                }
                txtTitleStory.text = storyItem.name
                txtSubtitleStory.text = storyItem.description
                root.setOnClickListener {
                    val intent = Intent(it.context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.DETAIL_STORY_EXTRA, storyItem)
                    it.context.startActivity(intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            it.context as Activity,
                            Pair(imgStoryItem, "imgdetail"),
                            Pair(txtTitleStory, "title"),
                            Pair(txtSubtitleStory, "desc")
                        ).toBundle()
                    )
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}