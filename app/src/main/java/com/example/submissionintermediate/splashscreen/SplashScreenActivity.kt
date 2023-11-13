package com.example.submissionintermediate.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.submissionintermediate.R
import com.example.submissionintermediate.ui.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 4000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val mainIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, splashTimeOut)
    }
}