package com.example.moneylover.ui

import com.example.moneylover.R
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
            .setTitle(R.string.log_out)
            .setMessage(R.string.confirm_logout)
            .setPositiveButton(R.string.log_out) { _, _ ->
                performLogout()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }


    private fun performLogout() {
        lifecycleScope.launch {
            googleAuthClient.signOut()
            userViewModel.clearAllTables()
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
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.imgPhotoProfileFragment)
        }
    }
}