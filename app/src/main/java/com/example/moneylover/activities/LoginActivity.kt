package com.example.moneylover.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneylover.R
import com.example.moneylover.adapter.LoginViewPagerAdapter
import com.example.moneylover.data.firebase.GoogleAuthClient
import com.example.moneylover.databinding.ActivityLoginBinding
import com.example.moneylover.fragments.LoginBottomSheetFragment
import com.example.moneylover.fragments.SignUpBottomSheetFragment

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(loginBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkSignIn()
        initControl()
        initEvents()
    }

    private fun checkSignIn() {
        googleAuthClient = GoogleAuthClient(this)

        if (googleAuthClient.isSignedIn()) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun initControl() {
        //Set Viewpager
        val listImage = listOf(R.drawable.img_login_vp1, R.drawable.img_login_vp2, R.drawable.img_login_vp3, R.drawable.img_login_vp4)
        val adapter = LoginViewPagerAdapter(listImage)
        loginBinding.vpLoginActivity.adapter = adapter
    }

    private fun initEvents() {
        //Go SignUp
        loginBinding.btnSignUpLoginActivity.setOnClickListener{
            val signUpBottomSheet = SignUpBottomSheetFragment()
            signUpBottomSheet.show(supportFragmentManager, "SignUpBottomSheetDialogFragment")
        }

        //Go Login
        loginBinding.btnLoginLoginActivity.setOnClickListener{
            val loginBottomSheet = LoginBottomSheetFragment()
            loginBottomSheet.show(supportFragmentManager, "LoginBottomSheetDialogFragment")
        }
    }

}