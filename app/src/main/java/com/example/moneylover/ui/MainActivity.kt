package com.example.moneylover.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.moneylover.R
import com.example.moneylover.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventNavBar() //Event select item nav bar
        addTransaction() //Event select add transaction button
    }

    private fun eventNavBar() {
        replaceFragment(HomeFragment()) //Open home fragment by default
        binding.bottomNavMainActivity.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> replaceFragment(HomeFragment())
                R.id.navProfile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    //Replace fragment
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun addTransaction() {
        binding.navBtnAddTransactionMainActivity.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }
}