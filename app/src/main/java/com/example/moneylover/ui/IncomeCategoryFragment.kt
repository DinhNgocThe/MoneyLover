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
import com.example.moneylover.databinding.FragmentIncomeCategoryBinding
import com.example.moneylover.viewmodel.ExpenseCategoryViewModel

class IncomeCategoryFragment : Fragment() {
    private lateinit var binding: FragmentIncomeCategoryBinding
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
        binding = FragmentIncomeCategoryBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadExpenseCategories()
        addCategory()
    }

    private fun loadExpenseCategories() {
        adapter = CategoryAdapter(requireContext(), onCategoryClick)
        binding.rcvIncomeCategoryIncomeCategoryFragment.adapter = adapter
        binding.rcvIncomeCategoryIncomeCategoryFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvIncomeCategoryIncomeCategoryFragment.setHasFixedSize(true)
        expenseCategoryViewModel.incomeCategories.observe(viewLifecycleOwner) {
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

    private fun addCategory() {
        binding.btnAddCategoryIncomeCategoryFragment.setOnClickListener {
            val intent = Intent(this.requireContext(), AddCategoryActivity::class.java)
            startActivity(intent)
        }
    }
}