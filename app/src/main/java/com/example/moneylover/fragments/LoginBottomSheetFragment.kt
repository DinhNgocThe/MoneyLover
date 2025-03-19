package com.example.moneylover.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.moneylover.activities.MainActivity
import com.example.moneylover.data.firebase.GoogleAuthClient
import com.example.moneylover.databinding.FragmentLoginBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class LoginBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentLoginBottomSheetBinding
    private lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleAuthClient = GoogleAuthClient(requireContext())
        eventDismissFragment()
        eventSignInWithGoogle()
    }

    private fun eventDismissFragment() {
        binding.btnBackLoginBottomSheet.setOnClickListener{
            dismiss()
        }
    }

    private fun eventSignInWithGoogle() {
        binding.btnGoogleLoginBottomSheet.setOnClickListener {
            lifecycleScope.launch {
                val isSuccess = googleAuthClient.signIn()
                if (isSuccess) {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }
}