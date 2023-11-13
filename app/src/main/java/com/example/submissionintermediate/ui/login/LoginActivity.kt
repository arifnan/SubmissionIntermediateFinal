package com.example.submissionintermediate.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.example.submissionintermediate.R
import com.example.submissionintermediate.auth.Result
import com.example.submissionintermediate.auth.UserModel
import com.example.submissionintermediate.auth.ViewModelFactory
import com.example.submissionintermediate.databinding.ActivityLoginBinding
import com.example.submissionintermediate.ui.main.MainActivity
import com.example.submissionintermediate.ui.main.MainViewModel
import com.example.submissionintermediate.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener{ processLogin() }

        binding.tvregister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setupView()
        playAnimation()
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(300)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(300)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(300)
        val emailEditView = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val passwordEditView = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(300)
        val tvsignup = ObjectAnimator.ofFloat(binding.tvquestionregister, View.ALPHA, 1f).setDuration(300)
        val signup = ObjectAnimator.ofFloat(binding.tvregister, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(login, tvsignup, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, message, emailTextView, emailEditView, passwordTextView, passwordEditView, together)
            startDelay = 100
            start()
        }
    }

    private fun processLogin(){
        binding.apply {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.login(email, password).observe(this@LoginActivity) { result ->
                Log.wtf("Result Login", result.toString())
                if (result != null){
                    when(result){
                        is Result.Loading -> {
                            showLoading(true)
                            loginButton.isEnabled = false
                        }
                        is Result.Success -> {
                            showLoading(false)
                            loginButton.isEnabled = true
                            viewModel.setlogin(UserModel(email, result.data?.loginResult?.token.orEmpty(), isLogin = true))
                            showToast(getString(R.string.login_succes))
                            moveToMainActivity()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            loginButton.isEnabled = true
                            showToast(getString(R.string.login_failed))
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun moveToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}