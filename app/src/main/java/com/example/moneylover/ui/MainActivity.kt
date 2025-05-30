package com.example.moneylover.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.room.model.ExpenseCategory
import com.example.moneylover.databinding.ActivityMainBinding
import com.example.moneylover.viewmodel.ExpenseCategoryViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
        preloadImages() //
    }

    private fun preloadImages() {
        expenseCategoryViewModel.expenseCategories.observe(this) { categories ->
            for (category in categories) {
                Glide.with(this)
                    .load(category.iconUrl)
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
}