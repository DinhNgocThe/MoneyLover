package com.example.moneylover.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.moneylover.R
import com.example.moneylover.adapter.LoginViewPagerAdapter
import com.example.moneylover.data.firebasemodel.GoogleAuthClient
import com.example.moneylover.data.firebasemodel.UserFirebase
import com.example.moneylover.data.room.model.User
import com.example.moneylover.databinding.ActivityLoginBinding
import com.example.moneylover.viewmodel.UserViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleAuthClient: GoogleAuthClient
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(
            this,
            UserViewModel.UserViewModelFactory(this.application)
        )[UserViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
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
        binding.vpLoginActivity.adapter = adapter
    }

    private fun initEvents() {
        //Login with Google
        binding.btnLoginWithGoogleLoginActivity.setOnClickListener {
            lifecycleScope.launch {
                val isSuccess = googleAuthClient.signIn()
                if (isSuccess) {
                    val currentUser = firebaseAuth.currentUser
                    val userFirebase = UserFirebase(
                        uid = currentUser?.uid ?: "",
                        email = currentUser?.email,
                        displayName = currentUser?.displayName,
                        photoUrl = currentUser?.photoUrl?.toString(),
                        createdAt = currentUser?.metadata?.creationTimestamp?.let {
                            Timestamp(it / 1000, ((it % 1000) * 1000000).toInt())
                        }
                    )
                    val user = User(
                        uid = currentUser?.uid ?: "",
                        email = currentUser?.email,
                        displayName = currentUser?.displayName,
                        photoUrl = currentUser?.photoUrl?.toString(),
                        createdAt = currentUser?.metadata?.creationTimestamp?.let {
                            it / 1000
                        }
                    )
                    userViewModel.saveUserToFirestore(userFirebase)
                    userViewModel.saveUserToRoom(user)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}