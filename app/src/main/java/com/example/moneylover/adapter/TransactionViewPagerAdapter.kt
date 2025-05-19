package com.example.moneylover.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moneylover.ui.TransactionDailyFragment
import com.example.moneylover.ui.TransactionMonthlyFragment

class TransactionViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TransactionMonthlyFragment()
            1 -> TransactionDailyFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}