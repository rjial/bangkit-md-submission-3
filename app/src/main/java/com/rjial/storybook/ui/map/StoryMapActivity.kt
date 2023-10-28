package com.rjial.storybook.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rjial.storybook.R
import com.rjial.storybook.data.viewmodel.StoryListViewModel
import com.rjial.storybook.data.viewmodel.factory.StoryListVMFactory
import com.rjial.storybook.databinding.ActivityStoryMapBinding
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.util.injection.StoryListInjection

class StoryMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapBinding
    private lateinit var storyListViewModel: StoryListViewModel
    private val boundsBuilder = LatLngBounds.builder()
    private lateinit var builderSingle: AlertDialog.Builder
    private lateinit var mapTypeListAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storyListViewModel = ViewModelProvider(this, StoryListVMFactory(StoryListInjection.provideInjection(this)))[StoryListViewModel::class.java]

        builderSingle = AlertDialog.Builder(this)
        mapTypeListAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        builderSingle.setTitle("Map Type")
        builderSingle.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        mapTypeListAdapter.add("Normal")
        mapTypeListAdapter.add("Satellite")
        mapTypeListAdapter.add("Terrain")
        mapTypeListAdapter.add("Hybrid")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {


        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map_style))
        loadStoriesWithLoc() {
            it.listStory.forEach { item ->
                if (item.lat != null && item.lon != null) {
                    val latLng = LatLng(item.lat.toDouble(), item.lon.toDouble())
                    mMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .title(item.name)
                        .snippet(item.description))
                    boundsBuilder.include(latLng)
                }

            }
            val bounds = boundsBuilder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            ))
        }
        builderSingle.setAdapter(mapTypeListAdapter) { _, which ->
            when(mapTypeListAdapter.getItem(which)) {
                "Normal" -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
                "Satellite" -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                }
                "Terrain" -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                }
                "Hybrid" -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                }
            }
        }

        binding.btnMapTypeFAB.setOnClickListener {
            builderSingle.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun loadStoriesWithLoc(callback: (res: StoryListResponse) -> Unit) {
        storyListViewModel.getAllStoriesLoc {
            callback(it)
        }
    }
}