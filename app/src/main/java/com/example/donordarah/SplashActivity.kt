package com.example.donordarah

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.donordarah.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private var _binding : ActivitySplashBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moveToMain()
    }

    private fun moveToMain(){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            startActivity(Intent(this,MainActivity::class.java).also { finish() })
        },1500)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}