package com.example.moneylover.ui.budget

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneylover.R
import com.example.moneylover.adapter.CategoryAdapter
import com.example.moneylover.data.room.model.Budget
import com.example.moneylover.data.room.model.ExpenseCategory
import com.example.moneylover.databinding.ActivitySelectExpenseBinding
import com.example.moneylover.viewmodel.BudgetViewModel
import com.example.moneylover.viewmodel.ExpenseCategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectExpenseBinding
    private lateinit var adapter: CategoryAdapter
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val expenseCategoryViewModel: ExpenseCategoryViewModel by lazy {
        ViewModelProvider(
            this,
            ExpenseCategoryViewModel.ExpenseCategoryViewModelFactory(this.application)
        )[ExpenseCategoryViewModel::class.java]
    }
    private val budgetViewModel: BudgetViewModel by lazy {
        ViewModelProvider(
            this,
            BudgetViewModel.BudgetViewModelFactory(this.application)
        )[BudgetViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectExpenseBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goBack()
        loadExpenseCategories()
    }

    private fun goBack() {
        binding.btnBackSelectExpenseActivity.setOnClickListener {
            finish()
        }
    }

    private fun loadExpenseCategories() {
        lifecycleScope.launch {
            val listBudget = withContext(Dispatchers.IO) {
                budgetViewModel.getAllBudget(firebaseAuth.currentUser?.uid ?: "")
            }

            val budgetCategoryIds = listBudget.map { it.category }.toSet() // O(n), tra cứu sau này O(1)

            adapter = CategoryAdapter(this@SelectExpenseActivity, onCategoryClick)
            binding.rcvExpenseCategorySelectExpenseActivity.adapter = adapter
            binding.rcvExpenseCategorySelectExpenseActivity.layoutManager =
                LinearLayoutManager(this@SelectExpenseActivity)
            binding.rcvExpenseCategorySelectExpenseActivity.setHasFixedSize(true)

            expenseCategoryViewModel.expenseCategories.observe(this@SelectExpenseActivity) { categories ->
                val filtered = categories.filter { it.id !in budgetCategoryIds }
                adapter.submitList(filtered)
            }
        }
    }

    private val onCategoryClick: (ExpenseCategory) -> Unit = {
        val resultIntent = Intent().apply {
            putExtra("22", it)
        }
        this.setResult(RESULT_OK, resultIntent)
        finish()
    }
}