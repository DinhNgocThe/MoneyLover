package com.example.moneylover.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.moneylover.R
import com.example.moneylover.data.firebasemodel.ExpenseCategoryFirebase
import com.example.moneylover.databinding.ActivityAddCategoryBinding
import com.example.moneylover.viewmodel.ExpenseCategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCategoryBinding
    private val tag = "Add category activity"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val expenseCategoryViewModel: ExpenseCategoryViewModel by lazy {
        ViewModelProvider(
            this,
            ExpenseCategoryViewModel.ExpenseCategoryViewModelFactory(this.application)
        )[ExpenseCategoryViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goBack()
        saveCategory()
    }

    private fun goBack() {
        binding.btnBackAddCategoryActivity.setOnClickListener {
            finish()
        }
    }

    private fun saveCategory() {
        binding.btnSaveAddCategoryActivity.setOnClickListener {
            binding.btnSaveAddCategoryActivity.isEnabled = false
            lifecycleScope.launch {
                try {
                    val name = binding.edtCategoryNameAddCategoryActivity.text.toString()
                    val type = if (binding.radExpenseAddCategoryActivity.isChecked) "expense" else "income"

                    val category = ExpenseCategoryFirebase(
                        uid = firebaseAuth.currentUser?.uid ?: "",
                        name = name,
                        iconUrl = "https://i.ibb.co/QFjCCJHH/ic-other.png",
                        type = type
                    )
                    withContext(Dispatchers.IO) {
                        expenseCategoryViewModel.insertExpenseCategory(category)
                    }
                    finish()
                } catch (e: Exception) {
                    Log.e(tag, "Error adding category", e)
                    binding.btnSaveAddCategoryActivity.isEnabled = true
                }
            }
        }
    }
}