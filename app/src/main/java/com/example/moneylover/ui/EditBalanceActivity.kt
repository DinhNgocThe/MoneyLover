package com.example.moneylover.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.moneylover.R
import com.example.moneylover.data.firebasemodel.WalletFirebase
import com.example.moneylover.data.room.model.Wallet
import com.example.moneylover.databinding.ActivityEditBalanceBinding
import com.example.moneylover.viewmodel.WalletViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class EditBalanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBalanceBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var wallet: Wallet
    private val walletViewModel: WalletViewModel by lazy {
        ViewModelProvider(
            this,
            WalletViewModel.WalletViewModelFactory(application)
        )[WalletViewModel::class.java]
    }

    private var originalBalance: Double = 0.0
    private var originalLimitAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditBalanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        disableButtonSave()
        initControls()
        goBack()
        formatEditText(binding.edtBalanceEditBalanceActivity)
        formatEditText(binding.edtLimitAmountEditBalanceActivity)
        updateWallet()
    }

    private fun updateWallet() {
        binding.btnSaveEditBalanceActivity.setOnClickListener {
            val walletRoom = Wallet(
                wallet.id,
                wallet.uid,
                binding.edtBalanceEditBalanceActivity.text.toString().replace(",", "").toDouble(),
                binding.edtLimitAmountEditBalanceActivity.text.toString().replace(",", "").toDouble(),
                wallet.totalExpense
            )
            val walletFirebase = WalletFirebase(
                wallet.id,
                wallet.uid,
                binding.edtBalanceEditBalanceActivity.text.toString().replace(",", "").toDouble(),
                binding.edtLimitAmountEditBalanceActivity.text.toString().replace(",", "").toDouble(),
                wallet.totalExpense
            )

            walletViewModel.updateWalletToRoom(walletRoom)
            walletViewModel.updateWalletToFirestore(walletFirebase)
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun initControls() {
        lifecycleScope.launch {
            wallet = walletViewModel.getWalletFromRoomByUid(firebaseAuth.currentUser?.uid.toString())!!
            originalBalance = wallet.balance
            originalLimitAmount = wallet.limitAmount

            binding.edtBalanceEditBalanceActivity.setText(formatCurrency(wallet.balance))
            binding.edtLimitAmountEditBalanceActivity.setText(formatCurrency(wallet.limitAmount))
        }
    }

    private fun goBack() {
        binding.btnBackEditBalanceActivity.setOnClickListener {
            finish()
        }
    }

    private fun formatEditText(edt: EditText) {
        var current = ""
        edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input == current) return

                edt.removeTextChangedListener(this)
                current = input

                if (input.isEmpty() || input == "." || input.endsWith(".")) {
                    edt.setText(input)
                } else {
                    val number = input.replace(",", "").toDoubleOrNull() ?: 0.0
                    edt.setText(formatCurrency(number))
                }

                edt.setSelection(edt.text.length)
                edt.addTextChangedListener(this)

                // Enable Save button if balance or limitAmount has changed
                enableSaveButton()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun enableSaveButton() {
        val currentBalance = binding.edtBalanceEditBalanceActivity.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val currentLimitAmount = binding.edtLimitAmountEditBalanceActivity.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        // Compare current value with the original values
        if (currentBalance != originalBalance || currentLimitAmount != originalLimitAmount) {
            binding.btnSaveEditBalanceActivity.isEnabled = true
            binding.btnSaveEditBalanceActivity.setTextColor(getColor(R.color.main_color)) // Change color to indicate it's enabled
        } else {
            disableButtonSave() // Disable button if values are the same
        }
    }

    private fun disableButtonSave() {
        binding.btnSaveEditBalanceActivity.isEnabled = false
        binding.btnSaveEditBalanceActivity.setTextColor(getColor(R.color.brown))
    }

    private fun formatCurrency(balance: Double): String {
        return DecimalFormat("#,###.###").format(balance)
    }
}
