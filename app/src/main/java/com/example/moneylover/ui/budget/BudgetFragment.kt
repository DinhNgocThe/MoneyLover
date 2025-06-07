package com.example.moneylover.ui.budget

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneylover.R
import com.example.moneylover.adapter.BudgetAdapter
import com.example.moneylover.data.room.model.BudgetWithCategory
import com.example.moneylover.databinding.FragmentBudgetBinding
import com.example.moneylover.ui.customview.CustomAlertDialog
import com.example.moneylover.viewmodel.BudgetViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class BudgetFragment : Fragment() {
    private lateinit var binding: FragmentBudgetBinding
    private lateinit var adapter: BudgetAdapter
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val budgetViewModel: BudgetViewModel by lazy {
        ViewModelProvider(
            this,
            BudgetViewModel.BudgetViewModelFactory(requireContext().applicationContext as Application)
        )[BudgetViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBudgetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadBudgets()
        createBudget()
    }

    private fun loadBudgets() {
        val (start, end) = getStartAndEndOfCurrentMonth()
        adapter = BudgetAdapter(requireContext(), onDelete)
        binding.rcvBudgetBudgetFragment.adapter = adapter
        binding.rcvBudgetBudgetFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvBudgetBudgetFragment.setHasFixedSize(true)
        budgetViewModel.getBudgetsFromRoomByUid(firebaseAuth.currentUser?.uid ?: "", start, end)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                budgetViewModel.budgets.collect { list ->
                    adapter.submitList(list)
                    if (list.isEmpty()) {
                        binding.layoutEmptyBudgetBudgetFragment.visibility = View.VISIBLE
                    } else {
                        binding.layoutEmptyBudgetBudgetFragment.visibility = View.GONE
                    }
                }
            }
        }
    }

    private val onDelete: (BudgetWithCategory) -> Unit = {
        val dialog = CustomAlertDialog(requireContext())
        dialog.setTitle(getString(R.string.confirm))
            .setMessage(getString(R.string.confirm_delete_budget))
            .setOnDegreeClickListener { dialog.dismiss() }
            .setOnAgreeClickListener { budgetViewModel.deleteBudget(it) }
            .show()
    }

    fun getStartAndEndOfCurrentMonth(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis

        return Pair(startOfMonth, endOfMonth)
    }

    private fun createBudget() {
        val intent = Intent(requireContext(), AddBudgetActivity::class.java)
        binding.btnAddBudgetBudgetFragment.setOnClickListener {
            startActivity(intent)
        }
    }
}