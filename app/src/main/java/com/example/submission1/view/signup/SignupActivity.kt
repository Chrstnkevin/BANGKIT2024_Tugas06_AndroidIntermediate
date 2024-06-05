package com.example.submission1.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.submission1.R
import com.example.submission1.databinding.ActivitySignupBinding
import com.example.submission1.view.main.ViewModelFactory
import com.example.submission1.view.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupView()
        playAnimation()

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom)
            insets
        }
    }

//    private fun setupAction() {
//        binding.signupButton.setOnClickListener {
//            val name = binding.nameEditText.text.toString()
//            val email = binding.emailEditText.text.toString()
//            val password = binding.passwordEditText.text.toString()
//
//            when {
//                name.isEmpty() -> {
//                    binding.nameEditText.error = getString(R.string.nameinvalid)
//                }
//                email.isEmpty() -> {
//                    binding.emailEditText.error = getString(R.string.emailinvaild)
//                }
//                password.isEmpty() -> {
//                    binding.passwordEditText.error = getString(R.string.passwordinvalid)
//                }
//                else -> {
//                    showLoading(true)
//                    viewModel.signup(name, email, password)
//                    viewModel.isLoading.observe(this, Observer { isLoading ->
//                        showLoading(isLoading)
//                        if (!isLoading) {
//                            AlertDialog.Builder(this).apply {
//                                setTitle("Yahh!")
//                                val message = getString(R.string.contentalertdialog, email)
//                                setMessage(message)
//                                setPositiveButton(getString(R.string.next)) { _, _ ->
//                                    val intent = Intent(context, LoginActivity::class.java)
//                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                                    startActivity(intent)
//                                    finish()
//                                }
//                                create()
//                                show()
//                            }.also {
//                                Snackbar.make(binding.root, getString(R.string.signupFail), Snackbar.LENGTH_SHORT).show()
//                            }
//                        }
//                    })
//                }
//            }
//        }
//    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener{
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.toString()

            when {
                name.isEmpty()-> {
                    binding.nameEditText.error = getString(R.string.nameinvalid)
                }
                email.isEmpty()-> {
                    binding.emailEditText.error  = getString(R.string.emailinvaild)
                }
                password.isEmpty()-> {
                    binding.passwordEditText.error = getString(R.string.passwordinvalid)
                } else -> {
                showLoading(true)
                viewModel.signup(name, email, password)
                viewModel.isLoading.observe(this, Observer { isLoading ->
                    if (isLoading){
                        AlertDialog.Builder(this).apply {
                            setTitle("Yosh!")
                            val message = getString(R.string.account_created, email)
                            setMessage(message)
                            setPositiveButton(getString(R.string.next)){ _, _ ->
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    } else {
                        Snackbar.make(binding.root, getString(R.string.signupFail), Snackbar.LENGTH_SHORT).show()
                    }
                    showLoading(false)
                })
                setupView()
            }
            }
        }
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewSignup, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextViewSignup, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}
