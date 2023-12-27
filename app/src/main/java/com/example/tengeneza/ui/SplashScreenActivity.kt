package com.example.tengeneza.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tengeneza.R
import com.example.tengeneza.databinding.ActivitySplashScreenBinding
import java.util.Timer
import java.util.TimerTask

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        // Set the duration for which the splash screen will be displayed (e.g., 2000 milliseconds)
        val splashDuration = 2000L

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                // Start the main activity after the splash duration
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }
        }, splashDuration)
    }
}