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
import androidx.lifecycle.lifecycleScope
import androidx.test.espresso.IdlingRegistry
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.rjial.storybook.R
import com.rjial.storybook.data.viewmodel.StoryListViewModel
import com.rjial.storybook.data.viewmodel.factory.StoryListVMFactory
import com.rjial.storybook.databinding.ActivityAddStoryBinding
import com.rjial.storybook.network.response.StoryAddResponse
import com.rjial.storybook.repository.StoryListRepository
import com.rjial.storybook.util.EspressoIdlingResource
import com.rjial.storybook.util.UriUtils
import com.rjial.storybook.util.injection.StoryListInjection
import com.rjial.storybook.util.reduceFileImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddStoryActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var storyViewModel: StoryListViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var storyListRepository: StoryListRepository
    private var currentImageUri: Uri? = null
    private lateinit var myLatLong: LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        storyListRepository = StoryListInjection.provideInjection(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
        binding.swToggleLocAddStory.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                getMyLastLocation()
            }
        }
        storyViewModel = ViewModelProvider(this, StoryListVMFactory(storyListRepository))[StoryListViewModel::class.java]
        binding.btnAddStoryImgGallery.setOnClickListener {
            startGallery()
        }
        binding.btnAddStoryImgCamera.setOnClickListener {
            startCamera()
        }
        binding.btnUploadAddStory.setOnClickListener {
            EspressoIdlingResource.increment()
            if (currentImageUri != null) {
                val imageFile = UriUtils().uriToFile(currentImageUri!!, this@AddStoryActivity).reduceFileImage()
                val description = binding.edtAddStoryDesc.text.toString()

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val latRb = if (binding.swToggleLocAddStory.isChecked) myLatLong.latitude.toString().toRequestBody("text/plain".toMediaType()) else null
                val lonRb = if (binding.swToggleLocAddStory.isChecked) myLatLong.longitude.toString().toRequestBody("text/plain".toMediaType()) else null
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                lifecycleScope.launch {
                    loadingFunc(true)
                    val uploadStoryRes = storyListRepository.uploadStorySus(multipartBody, requestBody, latRb, lonRb)
                    uploadStoryRes.onSuccess {
                        EspressoIdlingResource.decrement()
                        loadingFunc(false)
                        finish()
                    }.onFailure {
                        when(it) {
                            is HttpException -> {
                                val errorBody = Gson().fromJson(it.response()?.errorBody()?.string(), StoryAddResponse::class.java)
                                Toast.makeText(this@AddStoryActivity, errorBody.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is Exception -> {
                                Toast.makeText(this@AddStoryActivity, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        loadingFunc(false)
                    }
                }
//                storyViewModel.uploadRes.observe(this@AddStoryActivity) {
//                    if (it != null) {
//                        when(it) {
//                            is ResponseResult.Success -> {
//                                loadingFunc(false)
//                                storyViewModel.uploadRes.removeObservers(this@AddStoryActivity)
//                                finish()
//                            }
//                            is ResponseResult.Error -> {
//                                loadingFunc(false)
//                                Toast.makeText(this@AddStoryActivity, it.error, Toast.LENGTH_SHORT)
//                                    .show()
//                                storyViewModel.uploadRes.removeObservers(this@AddStoryActivity)
//                            }
//                            else -> loadingFunc(true)
//                        }
//                    }
//                }
//                storyViewModel.uploadStory(multipartBody, requestBody, latRb, lonRb)
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

    private var launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {
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
                    Toast.makeText(this@AddStoryActivity, "Location permission not granted", Toast.LENGTH_SHORT).show()
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
                    binding.swToggleLocAddStory.isChecked = true
                } else {
                    binding.swToggleLocAddStory.isChecked = false
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