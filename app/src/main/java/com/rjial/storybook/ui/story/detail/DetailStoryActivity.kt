package com.rjial.storybook.ui.story.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rjial.storybook.R
import com.rjial.storybook.databinding.ActivityDetailStoryBinding
import com.rjial.storybook.network.response.ListStoryItem

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.mapDetailStory.onCreate(savedInstanceState)

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
                if (extras.lat != null && extras.lon != null) {
                    mapDetailStory.visibility = View.VISIBLE
                    mapDetailStory.getMapAsync { map ->
                        val storyLoc = LatLng(extras.lat.toDouble(), extras.lon.toDouble())
                        map.addMarker(MarkerOptions()
                            .position(storyLoc)
                            .title(extras.name)
                            .snippet(extras.description))
                        map.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(storyLoc, 25f))
                    }
                } else {
                    mapDetailStory.visibility = View.GONE
                }
            }
        } else {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapDetailStory.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapDetailStory.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapDetailStory.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapDetailStory.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapDetailStory.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapDetailStory.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapDetailStory.onLowMemory()
    }

    companion object {
        const val DETAIL_STORY_EXTRA = "detail_story_extra"
    }

}