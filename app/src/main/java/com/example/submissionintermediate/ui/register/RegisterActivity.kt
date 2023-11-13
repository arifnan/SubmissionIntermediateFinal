package com.example.submissionintermediate.ui.register

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
import com.example.submissionintermediate.auth.ViewModelFactory
import com.example.submissionintermediate.databinding.ActivityRegisterBinding
import com.example.submissionintermediate.ui.login.LoginActivity
import com.example.submissionintermediate.ui.main.MainViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener{ processRegister() }

        binding.tvlogin.setOnClickListener{ moveToLogin() }

        setupView()
        setupAnimation()
    }

    private fun setupAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatCount = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(300)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(300)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(300)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(300)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(300)
        val tvlogin = ObjectAnimator.ofFloat(binding.tvquestionlogin, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.tvlogin, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(signup, tvlogin, login)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                together
            )
            startDelay = 100
        }.start()
    }


    private fun processRegister(){

        binding.apply {
            val name = nameEditTextView.text.toString()
            val email = emailEditTextView.text.toString()
            val password = passwordEditTextView.text.toString()

            viewModel.register(name, email, password).observe(this@RegisterActivity){ result ->
                Log.wtf("Result Register", result.toString())
                if(result != null){
                    when(result){
                        is Result.Loading -> {
                            showLoading(true)
                            signupButton.isEnabled = false
                        }
                        is Result.Success -> {
                            showLoading(false)
                            signupButton.isEnabled = true
                            showToast(getString(R.string.create_account_succes))
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            signupButton.isEnabled = true
                            showToast(getString(R.string.create_account_failed))
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAGS_CHANGED,
                WindowManager.LayoutParams.FLAGS_CHANGED
            )
        }
        supportActionBar?.hide()
    }
    private fun moveToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}