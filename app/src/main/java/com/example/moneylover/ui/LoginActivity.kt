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
import com.example.moneylover.data.firebasemodel.WalletFirebase
import com.example.moneylover.data.room.model.User
import com.example.moneylover.data.room.model.Wallet
import com.example.moneylover.databinding.ActivityLoginBinding
import com.example.moneylover.viewmodel.UserViewModel
import com.example.moneylover.viewmodel.WalletViewModel
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
    private val walletViewModel: WalletViewModel by lazy {
        ViewModelProvider(
            this,
            WalletViewModel.WalletViewModelFactory(this.application)
        )[WalletViewModel::class.java]
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
        //If user is already signed in, go to MainActivity
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
                    //Save user to room and firestore
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

                    //Save wallet to room and firestore
                    var walletFirebase = walletViewModel.getWalletFromFirestoreByUid(currentUser?.uid ?: "")
                    if (walletFirebase == null) {
                        val newWallet = WalletFirebase(
                            uid = currentUser?.uid ?: "",
                            balance = 0.0,
                            limitAmount = 0.0,
                            totalExpense = 0.0
                        )
                        walletViewModel.insertWalletToFirestore(newWallet)
                        walletFirebase = walletViewModel.getWalletFromFirestoreByUid(currentUser?.uid ?: "")
                    }
                    val wallet = Wallet(
                        walletFirebase?.id ?: "",
                        walletFirebase?.uid ?: "",
                        walletFirebase?.balance ?: 0.0,
                        walletFirebase?.limitAmount ?: 0.0,
                        walletFirebase?.totalExpense ?: 0.0
                    )
                    walletViewModel.insertWalletToRoom(wallet)

                    //Go to MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}