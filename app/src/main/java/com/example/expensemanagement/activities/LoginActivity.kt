package com.example.expensemanagement.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expensemanagement.R
import com.example.expensemanagement.bottomsheets.LanguageBottomSheetFragment
import com.example.expensemanagement.databinding.ActivityLoginBinding

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
        initEvents()
    }

    private fun initEvents() {
        loginBinding.btnChangeLanguageLoginActivity.setOnClickListener{
            val bottomSheet = LanguageBottomSheetFragment()
            bottomSheet.show(supportFragmentManager, "LanguageBottomSheet")
        }
    }
}