package com.example.moneylover.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.moneylover.R
import com.example.moneylover.data.room.model.User
import com.example.moneylover.data.room.model.Wallet
import com.example.moneylover.databinding.FragmentHomeBinding
import com.example.moneylover.viewmodel.UserViewModel
import com.example.moneylover.viewmodel.WalletViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var wallet: Wallet
    private lateinit var user: User
    private lateinit var binding: FragmentHomeBinding
    private var isWalletInitialized = false
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
        loadUserInformation()
        loadWalletInformation()
        editBalance()
    }

    private fun loadUserInformation() {
        lifecycleScope.launch {
            user = userViewModel.getUserFromRoomByUid(firebaseAuth.currentUser?.uid.toString())!!
            binding.txtDisplayNameHomeFragment.text = user.displayName
            Glide.with(requireContext())
                .load(user.photoUrl)
                .signature(ObjectKey(user.photoUrl))
                .placeholder(R.drawable.img_default_user_photo)
                .error(R.drawable.img_default_user_photo)
                .into(binding.imgPhotoHomeFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadWalletInformation() {
        walletViewModel.loadWalletFromRoomByUid(firebaseAuth.currentUser?.uid.toString())
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.wallet.collect { value ->
                    value?.let {
                        wallet = it
                        isWalletInitialized = true
                        binding.txtBalanceHomeFragment.text = formatCurrency(wallet.balance) + " " + getString(R.string.vnd)
                    }
                }
            }
        }
    }

    private fun editBalance() {
        binding.btnEditBalanceHomeFragment.setOnClickListener {
            val intent = Intent(this.requireContext(), EditBalanceActivity::class.java)
            startActivity(intent)
        }
    }

    private fun formatCurrency(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        return DecimalFormat("#,###", symbols).format(value)
    }
}
