package com.example.moneylover.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.room.model.TransactionWithCategory
import com.example.moneylover.databinding.ActivityTransactionDetailsBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionDetailsBinding
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
    }

    @SuppressLint("SetTextI18n")
    private fun initControls() {
        val transaction = intent.getSerializableExtra("EXTRA TransactionWithCategory") as TransactionWithCategory
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
}