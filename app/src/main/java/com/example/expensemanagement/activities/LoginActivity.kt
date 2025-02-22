package com.example.expensemanagement.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expensemanagement.R
import com.example.expensemanagement.adapter.LoginViewPagerAdapter
import com.example.expensemanagement.databinding.ActivityLoginBinding
import com.example.expensemanagement.fragments.LoginBottomSheetFragment
import com.example.expensemanagement.fragments.SignUpBottomSheetFragment

@SuppressLint("StaticFieldLeak")
private lateinit var loginBinding: ActivityLoginBinding
class LoginActivity : AppCompatActivity() {
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
        initControl()
        initEvents()
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