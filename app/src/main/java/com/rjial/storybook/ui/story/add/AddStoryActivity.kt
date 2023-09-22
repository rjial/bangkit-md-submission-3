package com.rjial.storybook.ui.story.add

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
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
import java.net.URI

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var storyViewModel: StoryListViewModel
    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                val uploadStory = storyViewModel.uploadStory(multipartBody, requestBody)
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
                    text = "Loading"
                    isEnabled = false
                }
            }
            false -> {
                binding.btnUploadAddStory.apply {
                    text = "Upload"
                    isEnabled = true
                }
            }
        }

    }
}