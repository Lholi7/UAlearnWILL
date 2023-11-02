package com.example.ualearn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class uaSPLASH : AppCompatActivity() {

    private val splashTimeout: Long = 3000 // 3 seconds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ua_splash)


        Handler().postDelayed({
            val intent = Intent(this, Register::class.java) // Replace with your main activity
            startActivity(intent)
            finish()
        }, splashTimeout)
    }
}