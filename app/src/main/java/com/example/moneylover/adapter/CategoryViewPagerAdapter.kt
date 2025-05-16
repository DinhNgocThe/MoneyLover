package com.example.moneylover.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moneylover.ui.ExpenseCategoryFragment
import com.example.moneylover.ui.IncomeCategoryFragment

class CategoryViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ExpenseCategoryFragment()
            1 -> IncomeCategoryFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
