package com.example.submission1.view.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.submission1.R
import com.example.submission1.data.api.ApiConfig
import com.example.submission1.data.pref.UserPreference
import com.example.submission1.data.pref.dataStore
import com.example.submission1.data.response.AddStoryGuestResponse
import com.example.submission1.databinding.ActivityAddstoryBinding
import com.example.submission1.view.main.MainActivity
import com.example.submission1.view.main.getImageUri
import com.example.submission1.view.main.reduceFileImage
import com.example.submission1.view.main.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryAddActivity : AppCompatActivity() {
    private lateinit var userPref: UserPreference
    private lateinit var binding: ActivityAddstoryBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddstoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPref = UserPreference.getInstance(dataStore)
        setupUI()
        setupWindowInsets()
    }

    private fun setupUI() {
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.sendStory.setOnClickListener { uploadImage() }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom)
            insets
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launchIntentCamera.launch(currentImageUri!!)
    }

    private val launchIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) showImage()
    }

    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launchGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            showImage()
        } ?: Log.d("Photo Picker", "No Media Selected")
    }

    private fun uploadImage() {
        val description = binding.descText.text.toString()
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

            lifecycleScope.launch {
                userPref.getSession().collect { user ->
                    val token = user.token
                    if (token.isNotEmpty()) {
                        uploadStory(token, multipartBody, requestBody)
                    } else {
                        showToast(getString(R.string.token))
                        showLoading(false)
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image))
    }

    private suspend fun uploadStory(token: String, multipartBody: MultipartBody.Part, requestBody: RequestBody) {
        try {
            val apiService = ApiConfig.getApiService(token)
            val successResponse = apiService.uploadStory("Bearer $token", multipartBody, requestBody)
            showToast(successResponse.message)
            showSuccessDialog()
        } catch (e: retrofit2.HttpException) {
            handleError(e)
        } finally {
            showLoading(false)
        }
    }

    private fun handleError(e: retrofit2.HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        val errorResponse = Gson().fromJson(errorBody, AddStoryGuestResponse::class.java)
        showToast(errorResponse.message)
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.uploadSuccess))
            setMessage(getString(R.string.storyUploadSuccess))
            setPositiveButton(getString(R.string.next)) { _, _ ->
                val intent = Intent(this@StoryAddActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ImageViewResult.setImageURI(it)
        }
    }
}



