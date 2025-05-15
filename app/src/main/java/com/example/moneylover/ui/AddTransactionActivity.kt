package com.example.moneylover.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneylover.R
import com.example.moneylover.databinding.ActivityAddTransactionBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private val calendar = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private lateinit var datePicker: MaterialDatePicker<Long>

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
        }
    }
}