package com.example.moneylover.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.moneylover.R
import com.example.moneylover.alert.NotificationIds
import com.example.moneylover.alert.OverspendingAlarmManager
import com.example.moneylover.databinding.ActivityMainBinding
import com.example.moneylover.ui.transaction.AddTransactionActivity
import com.example.moneylover.viewmodel.ExpenseCategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val expenseCategoryViewModel: ExpenseCategoryViewModel by lazy {
        ViewModelProvider(
            this,
            ExpenseCategoryViewModel.ExpenseCategoryViewModelFactory(this.application)
        )[ExpenseCategoryViewModel::class.java]
    }

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
        preloadImages()
        startAlarm()
    }

    private fun preloadImages() {
        lifecycleScope.launch {
            val icons = expenseCategoryViewModel.getCategoryIcons()
            for (icon in  icons) {
                Glide.with(this@MainActivity)
                    .load(icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload()
            }
        }
    }

    private fun eventNavBar() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragmentMainActivity) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavMainActivity, navController)
    }

    private fun addTransaction() {
        binding.navBtnAddTransactionMainActivity.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startAlarm() {
        OverspendingAlarmManager(this).scheduleDailyAlarm(firebaseAuth.currentUser?.uid ?: "")
    }
}