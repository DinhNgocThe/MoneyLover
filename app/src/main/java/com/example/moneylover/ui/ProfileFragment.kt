package com.example.moneylover.ui

import android.app.AlertDialog
import com.example.moneylover.R
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.moneylover.data.firebasemodel.GoogleAuthClient
import com.example.moneylover.databinding.FragmentProfileBinding
import com.example.moneylover.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleAuthClient: GoogleAuthClient
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(
            this,
            UserViewModel.UserViewModelFactory(requireContext().applicationContext as Application)
        )[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserInformation()
        logOut()
    }

    private fun logOut() {
        googleAuthClient = GoogleAuthClient(requireContext())
        binding.btnLogOutProfileFragment.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }


    private fun performLogout() {
        lifecycleScope.launch {
            googleAuthClient.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private  fun loadUserInformation() {
        //Load User information
        lifecycleScope.launch {
            val user = userViewModel.getUserByUidFromRoom(firebaseAuth.currentUser?.uid.toString())
            binding.txtDisplayNameProfileFragment.text = user?.displayName
            binding.txtEmailProfileFragment.text = user?.email
            Glide.with(requireContext())
                .load(user?.photoUrl)
                .placeholder(R.drawable.img_default_user_photo)
                .error(R.drawable.img_default_user_photo)
                .into(binding.imgPhotoProfileFragment)
        }
    }
}