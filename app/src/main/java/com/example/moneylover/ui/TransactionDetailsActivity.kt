package com.example.moneylover.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.firebasemodel.WalletFirebase
import com.example.moneylover.data.room.model.TransactionWithCategory
import com.example.moneylover.data.room.model.Wallet
import com.example.moneylover.databinding.ActivityTransactionDetailsBinding
import com.example.moneylover.viewmodel.TransactionViewModel
import com.example.moneylover.viewmodel.WalletViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionDetailsBinding
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private lateinit var transaction: TransactionWithCategory
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val transactionViewModel: TransactionViewModel by lazy {
        ViewModelProvider(
            this,
            TransactionViewModel.TransactionViewModelFactory(this.application)
        ) [TransactionViewModel::class.java]
    }

    private val walletViewModel: WalletViewModel by lazy {
        ViewModelProvider(
            this,
            WalletViewModel.WalletViewModelFactory(this.application)
        )[WalletViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goBack()
        initControls()
        deleteTransaction()
    }

    @SuppressLint("SetTextI18n")
    private fun initControls() {
        transaction = intent.getSerializableExtra("EXTRA TransactionWithCategory") as TransactionWithCategory
        binding.txtCategoryNameInTransactionDetailsActivity.text = transaction.name
        binding.txtDateTransactionDetailsActivity.text = simpleDateFormat.format(transaction.date)
        binding.txtAmountTransactionDetailsActivity.text = formatCurrency(transaction.amount) + " " + getString(R.string.vnd)
        binding.edtNoteTransactionDetailsActivity.setText(transaction.description)
        Glide.with(this)
            .load(transaction.iconUrl)
            .placeholder(R.drawable.ic_type)
            .error(R.drawable.ic_warning)
            .into(binding.imgCategoryTransactionDetailsActivity)
    }

    private fun formatCurrency(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        return DecimalFormat("#,###", symbols).format(value)
    }

    private fun goBack() {
        binding.btnBackTransactionDetailsActivity.setOnClickListener {
            finish()
        }
    }

    private fun deleteTransaction() {
        binding.btnDeleteTransactionDetailsActivity.setOnClickListener {
            val dialog = CustomAlertDialog(this)
            dialog.setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.confirm_delete_transaction))
                .setOnDegreeClickListener { dialog.dismiss() }
                .setOnAgreeClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        transactionViewModel.deleteTransactionById(transaction.id)
                        val wallet = walletViewModel.getWalletFromRoomByUid(firebaseAuth.currentUser?.uid ?: "")
                        var amount = 0.0
                        amount = if (transaction.transactionType == "expense") transaction.amount else -transaction.amount
                        val newWallet = Wallet(
                            wallet?.id ?: "",
                            wallet?.uid ?: "",
                            wallet?.balance?.plus(amount) ?: 0.0,
                            wallet?.limitAmount ?: 0.0,
                            wallet?.totalExpense ?: 0.0
                        )
                        val walletFirebase = WalletFirebase(
                            newWallet.id,
                            newWallet.uid,
                            newWallet.balance,
                            newWallet.limitAmount,
                            newWallet.totalExpense
                        )
                        walletViewModel.updateWalletToRoom(newWallet)
                        walletViewModel.updateWalletToFirestore(
                            walletFirebase
                        )
                    }
                    finish()
                }
                .show()
        }
    }

}