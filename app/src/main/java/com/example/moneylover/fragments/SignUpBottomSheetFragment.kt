package com.example.moneylover.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import com.example.moneylover.R
import com.example.moneylover.activities.MainActivity
import com.example.moneylover.data.firebase.GoogleAuthClient
import com.example.moneylover.databinding.FragmentSignUpBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class SignUpBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSignUpBottomSheetBinding
    private lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dismissFragment(view)
        eventSignInWithGoogle()
    }

    private fun eventSignInWithGoogle() {
        googleAuthClient = GoogleAuthClient(requireContext())
        lifecycleScope.launch {
            val isSuccess = googleAuthClient.signIn()
            if (isSuccess) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun dismissFragment(view: View) {
        //Dismiss fragment
        binding.btnBackSignUpBottomSheet.setOnClickListener{
            dismiss()
        }
    }
}