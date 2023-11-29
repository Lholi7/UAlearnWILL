package com.example.ualearn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bottomNavView = findViewById(R.id.bottomNavView)
        bottomNavView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.StudentSubjects -> {
                    // Handle Home item click
                     startActivity(Intent(this, StudentSubjects::class.java))
                    true
                }
                R.id.Add_Topics -> {
                    // Handle Dashboard item click
                     startActivity(Intent(this, Add_Topics::class.java))
                    true
                }
                R.id.Add_Notes -> {
                    // Handle Profile item click
                     startActivity(Intent(this, Add_Notes::class.java))
                    true
                }
                else -> false
            }
        }
    }
}