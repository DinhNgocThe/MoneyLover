package com.example.moneylover.ui.budget

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.firebasemodel.BudgetFirebase
import com.example.moneylover.data.room.model.ExpenseCategory
import com.example.moneylover.databinding.ActivityAddBudgetBinding
import com.example.moneylover.ui.transaction.SelectGroupActivity
import com.example.moneylover.viewmodel.BudgetViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class AddBudgetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBudgetBinding
    private lateinit var category: ExpenseCategory
    private val fireBaseAuth = FirebaseAuth.getInstance()
    private val budgetViewModel: BudgetViewModel by lazy {
        ViewModelProvider(
            this,
            BudgetViewModel.BudgetViewModelFactory(this.application)
        )[BudgetViewModel::class.java]
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val category = data?.getSerializableExtra("22") as? ExpenseCategory
            if (category != null) {
                this.category = category
                binding.txtTypeAddBudgetActivity.text = category.name
                Glide.with(this)
                    .load(category.iconUrl)
                    .placeholder(R.drawable.ic_type)
                    .error(R.drawable.ic_warning)
                    .into(binding.imgTypeAddBudgetActivity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBudgetBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goBack()
        formatEditText(binding.edtAmountAddBudgetActivity)
        selectGroup()
        addBudget()
    }

    private fun goBack() {
        binding.btnBackAddBudgetActivity.setOnClickListener {
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

    private fun selectGroup() {
        binding.cardViewTypeAddBudgetActivity.setOnClickListener {
            val intent = Intent(this, SelectExpenseActivity::class.java)
            launcher.launch(intent)
        }
    }

    private fun addBudget() {
        binding.btnCreateAddBudgerActivity.setOnClickListener {
            if (binding.edtAmountAddBudgetActivity.text.toString() == "") {
                binding.edtAmountAddBudgetActivity.requestFocus()
                return@setOnClickListener
            }
            if (binding.txtTypeAddBudgetActivity.text.toString() == getString(R.string.select_group)) {
                return@setOnClickListener
            }

            val budget = BudgetFirebase(
                id = null,
                uid = fireBaseAuth.currentUser?.uid ?: "",
                amount = binding.edtAmountAddBudgetActivity.text.toString().replace(".", "").toDouble(),
                category = category.id,
                isActive = true
            )
            budgetViewModel.insertBudgetToFirestoreAndInsertToRoom(budget)
            finish()
        }
    }
}