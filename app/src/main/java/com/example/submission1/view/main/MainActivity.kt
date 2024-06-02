package com.example.submission1.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission1.R
import com.example.submission1.view.story.StoryAdapter
import com.example.submission1.data.response.ListStoryItem
import com.example.submission1.databinding.ActivityMainBinding
import com.example.submission1.view.login.LoginActivity
import com.example.submission1.view.story.StoryAddActivity
import com.example.submission1.view.story.StoryDetailActivity
import com.example.submission1.view.welcome.WelcomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        observeSession()
        observeLogout()
        fetchAllStories()
        observeListStory()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, StoryAddActivity::class.java))
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun observeLogout(){
        binding.bottomAppBar.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId){
                R.id.logoutmenu -> {
                    binding.bottomAppBar.setOnMenuItemClickListener(null)

                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.confirmExit))
                        setMessage(getString(R.string.exit))
                        setPositiveButton(getString(R.string.yes)) {dialog, _ ->
                            dialog.dismiss()
                            viewModel.logout()
                            binding.bottomAppBar.setOnMenuItemClickListener{innerMenu ->
                                observeLogout()
                                true
                            }
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton(getString(R.string.no)) {dialog, _ ->
                            dialog.dismiss()
                            binding.bottomAppBar.setOnMenuItemClickListener{innerMenu ->
                                observeLogout()
                                true
                            }
                        }
                        create()
                        show()
                    }
                    true
                }
                R.id.action_home -> {
                    recreate()
                    true
                }
                else -> false
            }
        }
    }

// error gatau kenapa
//    private fun observeLogout() {
//        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.logoutmenu -> {
//                    showLogoutDialog()
//                    true
//                }
//                R.id.action_home -> {
//                    recreate()
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//
//    private fun showLogoutDialog() {
//        AlertDialog.Builder(this).apply {
//            setTitle(getString(R.string.confirmExit))
//            setMessage(getString(R.string.exit))
//            setPositiveButton(getString(R.string.yes)) { dialog, _ ->
//                dialog.dismiss()
//                viewModel.logout()
//                navigateToLogin()
//            }
//            setNegativeButton(getString(R.string.no)) { dialog, _ ->
//                dialog.dismiss()
//            }
//            create()
//            show()
//        }
//    }
//
//    private fun navigateToLogin() {
//        val intent = Intent(this, LoginActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//        }
//        startActivity(intent)
//        finish()
//    }

    private fun fetchAllStories() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                viewModel.getAllStory()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error: ${e.message}")
            }
        }
    }

    private fun observeListStory() {
        viewModel.storyList.observe(this) { storyList ->
            if (storyList.isNotEmpty()) {
                setupStoryAdapter(storyList)
            } else {
                showEmptyStoryToast()
                setupEmptyStoryAdapter()
            }
        }
    }

    private fun setupStoryAdapter(storyList: List<ListStoryItem>) {
        storyAdapter = StoryAdapter(storyList, object : StoryAdapter.OnAdapterListener {
            override fun onClick(story: ListStoryItem) {
                navigateToDetailStory(story)
            }
        })
        binding.rvStory.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupEmptyStoryAdapter() {
        storyAdapter = StoryAdapter(mutableListOf(), object : StoryAdapter.OnAdapterListener {
            override fun onClick(story: ListStoryItem) {
                // Handle empty state
            }
        })
        binding.rvStory.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun showEmptyStoryToast() {
        Toast.makeText(this, getString(R.string.storyEmpty), Toast.LENGTH_SHORT).show()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun navigateToDetailStory(story: ListStoryItem) {
        Intent(this, StoryDetailActivity::class.java).apply {
            putExtra(StoryDetailActivity.EXTRA_PHOTO_URL, story.photoUrl)
            putExtra(StoryDetailActivity.EXTRA_CREATED_AT, story.createdAt)
            putExtra(StoryDetailActivity.EXTRA_NAME, story.name)
            putExtra(StoryDetailActivity.EXTRA_DESCRIPTION, story.description)
            putExtra(StoryDetailActivity.EXTRA_LON, story.lon)
            putExtra(StoryDetailActivity.EXTRA_ID, story.id)
            putExtra(StoryDetailActivity.EXTRA_LAT, story.lat)
        }.also {
            startActivity(it)
        }
    }
}
