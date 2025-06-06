package com.example.moneylover.ui.transaction

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneylover.R
import com.example.moneylover.adapter.CategoryViewPagerAdapter
import com.example.moneylover.databinding.ActivitySelectGroupBinding
import com.google.android.material.tabs.TabLayoutMediator

class SelectGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectGroupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goBack()
        initViewPager()
    }

    private fun goBack() {
        binding.btnBackSelectGroupActivity.setOnClickListener {
            finish()
        }
    }

    private fun initViewPager() {
        val adapter = CategoryViewPagerAdapter(this)
        binding.viewPagerSelectGroupActivity.adapter = adapter

        val tabTitles = listOf(getString(R.string.expense), getString(R.string.income))
        TabLayoutMediator(binding.tabLayoutSelectGroupActivity, binding.viewPagerSelectGroupActivity) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}