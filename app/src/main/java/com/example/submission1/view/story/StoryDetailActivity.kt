package com.example.submission1.view.story

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.submission1.R
import com.example.submission1.databinding.ActivityStorydetailBinding

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStorydetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStorydetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detailstory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL)
        val createdAt = intent.getStringExtra(EXTRA_CREATED_AT)
        val name = intent.getStringExtra(EXTRA_NAME)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val lon = intent.getDoubleExtra(EXTRA_LON, 0.0)
        val id = intent.getStringExtra(EXTRA_ID)
        val lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        detailView(photoUrl, createdAt, name, description, lon, id, lat)


    }

    private fun detailView(photoUrl: String?, createdAt: String?, name: String?, description: String?, lon: Double, id: String?, lat: Double) {
        Glide.with(this@StoryDetailActivity)
            .load(photoUrl)
            .into(binding.profileImageView)
        binding.nameTextView.text = name
        binding.dateTextView.text = createdAt
        binding.descTextView.text = description
    }

    companion object {
        const val EXTRA_PHOTO_URL = "EXTRA_PHOTO_URL"
        const val EXTRA_CREATED_AT = "EXTRA_CREATED_AT"
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_LON = "EXTRA_LON"
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_LAT = "EXTRA_LAT"
    }
}