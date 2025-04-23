package com.example.moneylover.ui

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.room.model.User
import com.example.moneylover.data.room.model.Wallet
import com.example.moneylover.databinding.FragmentHomeBinding
import com.example.moneylover.viewmodel.UserViewModel
import com.example.moneylover.viewmodel.WalletViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class HomeFragment : Fragment() {
    private lateinit var wallet: Wallet
    private lateinit var user: User
    private lateinit var binding: FragmentHomeBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(
            this,
            UserViewModel.UserViewModelFactory(requireContext().applicationContext as Application)
        )[UserViewModel::class.java]
    }
    private val walletViewModel: WalletViewModel by lazy {
        ViewModelProvider(
            this,
            WalletViewModel.WalletViewModelFactory(requireContext().applicationContext as Application)
        )[WalletViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor()
        loadUserInformation()
        loadWalletInformation()
        hiddenBalance()
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val window = requireActivity().window
            val insetsController = window.insetsController
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.background)
            insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun hiddenBalance() {
        binding.btnHiddenBalanceHomeFragment.setOnClickListener {
            if (binding.btnHiddenBalanceHomeFragment.drawable.constantState == ContextCompat.getDrawable(requireContext(), R.drawable.ic_show)?.constantState) {
                binding.txtBalanceHomeFragment.text = getString(R.string.balance_when_hidden)
                binding.btnHiddenBalanceHomeFragment.setImageResource(R.drawable.ic_hidden)
            } else {
                binding.txtBalanceHomeFragment.text = formatCurrency(wallet.balance) + " " + getString(R.string.vnd)
                binding.btnHiddenBalanceHomeFragment.setImageResource(R.drawable.ic_show)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadWalletInformation() {
        lifecycleScope.launch {
            wallet = walletViewModel.getWalletFromRoomByUid(firebaseAuth.currentUser?.uid.toString())!!
            binding.txtBalanceHomeFragment.text = getString(R.string.balance_when_hidden)
        }
    }

    private fun loadUserInformation() {
        lifecycleScope.launch {
            user = userViewModel.getUserByUidFromRoom(firebaseAuth.currentUser?.uid.toString())!!
            binding.txtDisplayNameHomeFragment.text = user?.displayName
            Glide.with(requireContext())
                .load(user.photoUrl)
                .placeholder(R.drawable.img_default_user_photo)
                .error(R.drawable.img_default_user_photo)
                .into(binding.imgPhotoHomeFragment)
        }
    }

    private fun formatCurrency(balance: Double): String {
        val decimalFormat = DecimalFormat("#,###.###")
        return decimalFormat.format(balance)
    }
}
