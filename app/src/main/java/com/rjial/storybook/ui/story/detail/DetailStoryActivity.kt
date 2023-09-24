package com.rjial.storybook.ui.story.detail

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import coil.load
import com.rjial.storybook.R
import com.rjial.storybook.databinding.ActivityDetailStoryBinding
import com.rjial.storybook.network.response.ListStoryItem

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val DETAIL_STORY_EXTRA = "detail_story_extra"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val extras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DETAIL_STORY_EXTRA, ListStoryItem::class.java)
        } else {
            intent.getParcelableExtra<ListStoryItem>(DETAIL_STORY_EXTRA)
        }
        if (extras != null) {
            with(binding) {
                txtTitleDetailStory.text = extras.name
                txtDescDetailStory.text = extras.description
                imgDetailStory.load(extras.photoUrl) {
                    placeholder(AppCompatResources.getDrawable(this@DetailStoryActivity, R.drawable.ic_launcher_background))
                }
            }
        } else {
            finish()
        }
    }
}