package com.example.moneylover.ui.transaction

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.moneylover.R
import com.example.moneylover.adapter.TransactionViewPagerAdapter
import com.example.moneylover.databinding.FragmentTransactionBinding
import com.example.moneylover.viewmodel.WalletViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class TransactionFragment : Fragment() {
    private lateinit var binding: FragmentTransactionBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val walletViewModel: WalletViewModel by lazy {
        ViewModelProvider(
            this,
            WalletViewModel.WalletViewModelFactory(requireContext().applicationContext as Application)
        )[WalletViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        loadWalletInformation()
    }

    private fun initViewPager() {
        val adapter = TransactionViewPagerAdapter(this)
        binding.viewPagerTransactionFragment.adapter = adapter

        val tabTitles = listOf(getString(R.string.monthly), getString(R.string.daily))
        TabLayoutMediator(binding.tabLayoutTransactionFragment, binding.viewPagerTransactionFragment) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    @SuppressLint("SetTextI18n")
    private fun loadWalletInformation() {
        walletViewModel.loadWalletFromRoomByUid(firebaseAuth.currentUser?.uid.toString())
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.wallet.collect { value ->
                    value?.let {
                        binding.txtBalanceTransactionFragment.text = formatCurrency(it.balance) + " " + getString(R.string.vnd)
                    }
                }
            }
        }
    }

    private fun formatCurrency(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        return DecimalFormat("#,###", symbols).format(value)
    }
}