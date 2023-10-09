package com.rjial.storybook.ui.story.add

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.rjial.storybook.R
import com.rjial.storybook.data.viewmodel.StoryListViewModel
import com.rjial.storybook.data.viewmodel.factory.StoryListVMFactory
import com.rjial.storybook.databinding.ActivityAddStoryBinding
import com.rjial.storybook.util.ResponseResult
import com.rjial.storybook.util.UriUtils
import com.rjial.storybook.util.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var storyViewModel: StoryListViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentImageUri: Uri? = null
    private lateinit var myLatLong: LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
        binding.txtLocationAddStory.setOnClickListener {
            getMyLastLocation()
        }
        storyViewModel = ViewModelProvider(this, StoryListVMFactory(application))[StoryListViewModel::class.java]
        binding.btnAddStoryImgGallery.setOnClickListener {
            startGallery()
        }
        binding.btnAddStoryImgCamera.setOnClickListener {
            startCamera()
        }
        binding.btnUploadAddStory.setOnClickListener {
            if (currentImageUri != null) {
                val imageFile = UriUtils().uriToFile(currentImageUri!!, this@AddStoryActivity).reduceFileImage()
                val description = binding.edtAddStoryDesc.text.toString()

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val latRb = if (::myLatLong.isInitialized) myLatLong.latitude.toString().toRequestBody("text/plain".toMediaType()) else null
                val lonRb = if (::myLatLong.isInitialized) myLatLong.longitude.toString().toRequestBody("text/plain".toMediaType()) else null
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                val uploadStory = storyViewModel.uploadStory(multipartBody, requestBody, latRb, lonRb)
                uploadStory.observe(this@AddStoryActivity) {
                    if (it != null) {
                        when(it) {
                            is ResponseResult.Loading -> loadingFunc(true)
                            is ResponseResult.Success -> {
                                loadingFunc(false)
                                uploadStory.removeObservers(this@AddStoryActivity)
                                finish()
                            }
                            is ResponseResult.Error -> {
                                loadingFunc(false)
                                uploadStory.removeObservers(this@AddStoryActivity)
                                Toast.makeText(this@AddStoryActivity, it.error, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Select image first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private fun showImage() {
        currentImageUri?.let {
            binding.imgAddStory.setImageURI(it)
        }
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if (it != null) {
            currentImageUri = it
            showImage()
        } else {
            Toast.makeText(this@AddStoryActivity, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        currentImageUri = UriUtils().getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            showImage()
        }
    }

    private fun loadingFunc(loading: Boolean) {
        when(loading) {
            true -> {
                binding.btnUploadAddStory.apply {
                    text = getString(R.string.loading)
                    isEnabled = false
                }
            }
            false -> {
                binding.btnUploadAddStory.apply {
                    text = getString(R.string.upload)
                    isEnabled = true
                }
            }
        }

    }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    binding.txtLocationAddStory.text = "Location permission not granted"
                    // No location access granted.
                }
            }
        }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    myLatLong = LatLng(location.latitude, location.longitude)
                    binding.txtLocationAddStory.text = getString(R.string.location, location.latitude.toString(), location.longitude.toString())
//                    showStartMarker(location)
                } else {
                    binding.txtLocationAddStory.text = "Location is not found. Try Again"
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}