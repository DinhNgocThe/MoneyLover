package com.example.moneylover.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneylover.adapter.CategoryAdapter
import com.example.moneylover.data.room.model.ExpenseCategory
import com.example.moneylover.databinding.FragmentExpenseCategoryBinding
import com.example.moneylover.viewmodel.ExpenseCategoryViewModel

class ExpenseCategoryFragment : Fragment() {
    private lateinit var binding: FragmentExpenseCategoryBinding
    private lateinit var adapter: CategoryAdapter
    private val expenseCategoryViewModel: ExpenseCategoryViewModel by lazy {
        ViewModelProvider(
            this,
            ExpenseCategoryViewModel.ExpenseCategoryViewModelFactory(requireContext().applicationContext as Application)
        )[ExpenseCategoryViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpenseCategoryBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadExpenseCategories()
    }

    private fun loadExpenseCategories() {
        adapter = CategoryAdapter(requireContext(), onCategoryClick, {})
        binding.rcvExpenseCategoryExpenseCategoryFragment.adapter = adapter
        binding.rcvExpenseCategoryExpenseCategoryFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvExpenseCategoryExpenseCategoryFragment.setHasFixedSize(true)
        expenseCategoryViewModel.expenseCategories.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private val onCategoryClick: (ExpenseCategory) -> Unit = {
        val resultIntent = Intent().apply {
            putExtra("11", it)
        }
        requireActivity().setResult(Activity.RESULT_OK, resultIntent)
        requireActivity().finish()
    }
}