package com.example.moneylover.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.moneylover.R
import com.example.moneylover.data.firebasemodel.WalletFirebase
import com.example.moneylover.data.room.model.Wallet
import com.example.moneylover.databinding.ActivityEditBalanceBinding
import com.example.moneylover.viewmodel.WalletViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class EditBalanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBalanceBinding
    private val tag = "Edit balance activity"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var wallet: Wallet
    private var originalBalance: Double = 0.0 // Original balance and limit amount to enable save button when 1 of 2 changes
    private var originalLimitAmount: Double = 0.0

    private val walletViewModel: WalletViewModel by lazy {
        ViewModelProvider(
            this,
            WalletViewModel.WalletViewModelFactory(application)
        )[WalletViewModel::class.java]
    }

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
        disableButtonSave() // Disable save button by default
        initControls()
        goBack() // Event back button
        formatEditText(binding.edtBalanceEditBalanceActivity) // Format edit text balance
        formatEditText(binding.edtLimitAmountEditBalanceActivity)
        updateWallet()
    }

    private fun disableButtonSave() {
        binding.btnSaveEditBalanceActivity.isEnabled = false
        binding.btnSaveEditBalanceActivity.setTextColor(getColor(R.color.brown))
    }

    private fun initControls() {
        lifecycleScope.launch {
            wallet = walletViewModel.getWalletFromRoomByUid(firebaseAuth.currentUser?.uid.toString())!!
            originalBalance = wallet.balance
            originalLimitAmount = wallet.limitAmount
            // Load wallet information
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
        edt.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s.isNullOrEmpty()) return

                isEditing = true

                // Remove all non-numeric characters except for a period (.)
                val clean = s.toString().replace(".", "")
                try {
                    val value = clean.toDouble()
                    val formatted = formatCurrency(value)

                    // Adjust cursor position to make sure it stays in the right place
                    val cursorOffset = formatted.length - s.length
                    edt.setText(formatted)
                    edt.setSelection((s.length + cursorOffset).coerceIn(0, formatted.length))
                } catch (_: NumberFormatException) {
                    edt.setText("")
                }

                isEditing = false
                enableSaveButton()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun formatCurrency(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        return DecimalFormat("#,###", symbols).format(value)
    }

    private fun updateWallet() {
        binding.btnSaveEditBalanceActivity.setOnClickListener {
            binding.btnSaveEditBalanceActivity.isEnabled = false
            try {
                val balanceText = binding.edtBalanceEditBalanceActivity.text.toString().replace(".", "")
                val limitAmountText = binding.edtLimitAmountEditBalanceActivity.text.toString().replace(".", "")

                // Convert string to double
                val balance = if (balanceText.isNotEmpty()) balanceText.toDoubleOrNull() ?: 0.0 else 0.0
                val limitAmount = if (limitAmountText.isNotEmpty()) limitAmountText.toDoubleOrNull() ?: 0.0 else 0.0

                // Update wallet to room and firestore
                val walletRoom = Wallet(
                    wallet.id,
                    wallet.uid,
                    balance,
                    limitAmount,
                    wallet.totalExpense
                )
                val walletFirebase = WalletFirebase(
                    wallet.id,
                    wallet.uid,
                    balance,
                    limitAmount,
                    wallet.totalExpense
                )

                walletViewModel.updateWalletToRoom(walletRoom)
                walletViewModel.updateWalletToFirestore(walletFirebase)
                finish()
            } catch (e: Exception) {
                Log.e(tag, "Error updating wallet", e)
                binding.btnSaveEditBalanceActivity.isEnabled = true
            }
        }
    }

    private fun enableSaveButton() {
        val currentBalance = binding.edtBalanceEditBalanceActivity.text.toString().replace(".", "").toDoubleOrNull() ?: 0.0
        val currentLimitAmount = binding.edtLimitAmountEditBalanceActivity.text.toString().replace(".", "").toDoubleOrNull() ?: 0.0

        // Compare current value with the original values
        if (currentBalance != originalBalance || currentLimitAmount != originalLimitAmount) {
            binding.btnSaveEditBalanceActivity.isEnabled = true
            binding.btnSaveEditBalanceActivity.setTextColor(getColor(R.color.main_color)) // Change color to indicate it's enabled
        } else {
            disableButtonSave() // Disable button if values are the same
        }
    }
}
