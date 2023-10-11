package com.rjial.storybook.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.rjial.storybook.R
import com.rjial.storybook.data.viewmodel.StoryListViewModel
import com.rjial.storybook.data.viewmodel.factory.StoryListVMFactory
import com.rjial.storybook.databinding.ActivityStoryMapBinding
import com.rjial.storybook.network.response.StoryListResponse
import com.rjial.storybook.util.ResponseResult

class StoryMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapBinding
    private lateinit var storyListViewModel: StoryListViewModel
    private val boundsBuilder = LatLngBounds.builder()
//    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storyListViewModel = ViewModelProvider(this, StoryListVMFactory(application))[StoryListViewModel::class.java]

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
        loadStoriesWithLoc() {
            it.listStory.forEach { item ->
                if (item.lat != null && item.lon != null) {
                    val latLng = LatLng(item.lat.toDouble(), item.lon.toDouble())
                    mMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .title(item.name))
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
//        val bounds = boundsBuilder.build()
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
//            bounds,
//            resources.displayMetrics.widthPixels,
//            resources.displayMetrics.heightPixels,
//            300
//        ))
//        val bounds = boundsBuilder.build()
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
//            bounds,
//            resources.displayMetrics.widthPixels,
//            resources.displayMetrics.heightPixels,
//            300
//        ))

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        val testingLoc = LatLng(-7.40407, 109.36273)
//        mMap.addMarker(MarkerOptions()
//            .position(testingLoc)
//            .title("teslagi"))
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(testingLoc, 15f))
    }

    override fun onDestroy() {
        super.onDestroy()
//        coroutineScope.cancel()
    }

    private fun loadStoriesWithLoc(callback: (res: StoryListResponse) -> Unit) {
//        StoryListInjection.provideInjection(application).getAllStoriesFlow(true).collect {
//            it.map {item ->
//                callback(item)
//            }
//        }
        storyListViewModel.getAllStoriesNonPaging(true).observe(this) {
            if (it != null) {
                when(it) {
                    is ResponseResult.Loading -> {}
                    is ResponseResult.Success -> {
                        callback(it.data)
                        Log.d("LIST_STORY", it.data.toString())
                    }
                    is ResponseResult.Error -> {
                        Toast.makeText(this@StoryMapActivity, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}