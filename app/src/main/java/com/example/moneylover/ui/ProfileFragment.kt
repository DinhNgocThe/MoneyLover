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
import com.bumptech.glide.signature.ObjectKey
import com.example.moneylover.data.firebasemodel.GoogleAuthClient
import com.example.moneylover.data.room.model.User
import com.example.moneylover.databinding.FragmentProfileBinding
import com.example.moneylover.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        logOut() //Event log out
        loadUserInformation() //Load user information
    }

    private fun logOut() {
        googleAuthClient = GoogleAuthClient(requireContext())
        binding.btnLogOutProfileFragment.setOnClickListener {
            val dialog = CustomAlertDialog(requireContext())
            dialog.setTitle(getString(R.string.log_out))
                .setMessage(getString(R.string.confirm_logout))
                .setOnAgreeClickListener { performLogout() }
                .setOnDegreeClickListener { dialog.dismiss() }
                .show()
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            googleAuthClient.signOut() // Sign out google
            userViewModel.clearAllTables() // Clear all tables in room
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
            startActivity(intent)
        }
    }

    private fun loadUserInformation() {
        //Load User information
        lifecycleScope.launch {
            val user: User
            withContext(Dispatchers.IO) {
                user = userViewModel.getUserFromRoomByUid(firebaseAuth.currentUser?.uid.toString())!! // Get user from room
            }
            binding.txtDisplayNameProfileFragment.text = user.displayName // Set display name
            binding.txtEmailProfileFragment.text = user.email // Set email
            Glide.with(requireContext()) // Load photoUrl
                .load(user.photoUrl)
                .signature(ObjectKey(user.photoUrl))
                .placeholder(R.drawable.img_default_user_photo)
                .error(R.drawable.img_default_user_photo)
                .into(binding.imgPhotoProfileFragment)
        }
    }
}