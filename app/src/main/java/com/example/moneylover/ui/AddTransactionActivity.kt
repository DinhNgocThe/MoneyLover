package com.example.moneylover.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.firebasemodel.TransactionFirebase
import com.example.moneylover.data.firebasemodel.WalletFirebase
import com.example.moneylover.data.room.model.ExpenseCategory
import com.example.moneylover.data.room.model.Wallet
import com.example.moneylover.databinding.ActivityAddTransactionBinding
import com.example.moneylover.viewmodel.TransactionViewModel
import com.example.moneylover.viewmodel.WalletViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private val tag = "Add transaction activity"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val calendar = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var category: ExpenseCategory
    private var dateFirebase by Delegates.notNull<Long>()

    private val transactionViewModel: TransactionViewModel by lazy {
        ViewModelProvider(
            this,
            TransactionViewModel.TransactionViewModelFactory(this.applicationContext as Application)
        )[TransactionViewModel::class.java]
    }

    private val walletViewModel: WalletViewModel by lazy {
        ViewModelProvider(
            this,
            WalletViewModel.WalletViewModelFactory(this.applicationContext as Application)
        )[WalletViewModel::class.java]
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val category = data?.getSerializableExtra("11") as? ExpenseCategory
            if (category != null) {
                this.category = category
                binding.txtTypeAddTransactionActivity.text = category.name
                Glide.with(this)
                    .load(category.iconUrl)
                    .placeholder(R.drawable.ic_type)
                    .error(R.drawable.ic_warning)
                    .into(binding.imgTypeAddTransactionActivity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goBack()
        formatEditText(binding.edtAmountAddTransactionActivity)
        initDatePicker()
        selectGroup()
        addTransaction()
    }

    private fun goBack() {
        binding.btnBackAddTransactionActivity.setOnClickListener {
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
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun formatCurrency(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        return DecimalFormat("#,###", symbols).format(value)
    }

    private fun initDatePicker() {
        val today = calendar.time
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(today.time)
            .build()
        dateFirebase = today.time
        binding.txtDateAddTransactionActivity.text = simpleDateFormat.format(today)

        // Event show date picker
        binding.cardViewDateAddTransactionActivity.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER_ADD_TRANSACTION")
        }
        // Event date picker
        datePickerOnPositiveButtonClick()
    }

    private fun datePickerOnPositiveButtonClick() {
        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            binding.txtDateAddTransactionActivity.text = simpleDateFormat.format(date)
            dateFirebase = date.time
        }
    }

    private fun selectGroup() {
        binding.cardViewTypeAddTransactionActivity.setOnClickListener {
            val intent = Intent(this, SelectGroupActivity::class.java)
            launcher.launch(intent)
        }
    }

    private fun addTransaction() {
        binding.btnAddAddTransactionActivity.setOnClickListener {
            if (binding.edtAmountAddTransactionActivity.text.toString() == "") {
                binding.txtWarningAddTransactionActivity.text = getString(R.string.amount_warning)
                binding.edtAmountAddTransactionActivity.requestFocus()
                return@setOnClickListener
            }
            if (binding.txtTypeAddTransactionActivity.text.toString() == getString(R.string.select_group)) {
                binding.txtWarningAddTransactionActivity.text = getString(R.string.category_type_warning)
                return@setOnClickListener
            }
            binding.btnAddAddTransactionActivity.isEnabled = false
            lifecycleScope.launch {
                try {
                    var amount = binding.edtAmountAddTransactionActivity.text.toString().replace(".", "").toDouble()
                    val description = binding.edtNoteAddTransactionActivity.text.toString()
                    val transactionFirebase = TransactionFirebase(
                        uid = firebaseAuth.currentUser?.uid ?: "",
                        amount = amount,
                        description = description,
                        date = dateFirebase,
                        type = category.id
                    )
                    if (category.type == "expense") amount = -amount
                    // Insert transaction and update wallet
                    withContext(Dispatchers.IO) {
                        val wallet = walletViewModel.getWalletFromRoomByUid(firebaseAuth.currentUser?.uid ?: "")
                        val newWallet = Wallet(
                            wallet?.id ?: "",
                            wallet?.uid ?: "",
                            wallet?.balance?.plus(amount) ?: 0.0,
                            wallet?.limitAmount ?: 0.0,
                            wallet?.totalExpense ?: 0.0
                        )
                        val newWalletFirebase = WalletFirebase(
                            newWallet.id,
                            newWallet.uid,
                            newWallet.balance,
                            newWallet.limitAmount,
                            newWallet.totalExpense
                        )
                        walletViewModel.updateWalletToRoom(newWallet)
                        walletViewModel.updateWalletToFirestore(newWalletFirebase)
                        transactionViewModel.insertTransaction(transactionFirebase)
                    }
                    finish()
                } catch (e: Exception) {
                    Log.e(tag, "Error adding transaction", e)
                    binding.btnAddAddTransactionActivity.isEnabled = true
                }
            }
        }
    }
}