package com.example.moneylover.ui

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
import com.example.moneylover.adapter.TransactionAdapter
import com.example.moneylover.data.room.model.TransactionWithCategory
import com.example.moneylover.databinding.FragmentTransactionMonthlyBinding
import com.example.moneylover.viewmodel.TransactionViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionMonthlyFragment : Fragment() {
    private lateinit var binding: FragmentTransactionMonthlyBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var adapter: TransactionAdapter
    private val simpleDateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
    private lateinit var datePicker: MaterialDatePicker<Long>
    private val calendar = Calendar.getInstance()

    private val transactionViewModel: TransactionViewModel by lazy {
        ViewModelProvider(
            this,
            TransactionViewModel.TransactionViewModelFactory(requireContext().applicationContext as Application)
        )[TransactionViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionMonthlyBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTransactions()
        initDatePicker()
    }

    private fun loadTransactions() {
        adapter = TransactionAdapter(requireContext(), onClick)
        binding.rcvTransactionMonthlyFragment.adapter = adapter
        binding.rcvTransactionMonthlyFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvTransactionMonthlyFragment.setHasFixedSize(true)
        val (startOfDay, endOfDay) = getStartAndEndOfMonth(calendar.time.time)
        transactionViewModel.getTransactionsFromRoomByUidAndTime(firebaseAuth.currentUser?.uid ?: "", startOfDay, endOfDay)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.transactions.collect { list ->
                    adapter.submitList(list) {
                        binding.rcvTransactionMonthlyFragment.scrollToPosition(0)
                        if (list.isEmpty()) {
                            binding.txtStateTransactionMonthlyFragment.visibility = View.VISIBLE
                        } else {
                            binding.txtStateTransactionMonthlyFragment.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private val onClick: (TransactionWithCategory) -> Unit = {
        val intent = Intent(this.requireContext(), TransactionDetailsActivity::class.java)
        intent.putExtra("EXTRA TransactionWithCategory", it)
        startActivity(intent)
    }

    fun getStartAndEndOfMonth(dateInMillis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonth = calendar.timeInMillis

        calendar.apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endOfMonth = calendar.timeInMillis

        return startOfMonth to endOfMonth
    }


    private fun initDatePicker() {
        val today = calendar.time
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(today.time)
            .build()
        binding.txtDateTransactionMonthlyFragment.text = simpleDateFormat.format(today)

        // Event show date picker
        binding.btnDatePickerTransactionMonthlyFragment.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER_ADD_TRANSACTION")
        }

        // Event date picker
        datePickerOnPositiveButtonClick()
    }

    private fun datePickerOnPositiveButtonClick() {
        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            binding.txtDateTransactionMonthlyFragment.text = simpleDateFormat.format(date)
            adapter.submitList(emptyList())
            val (startOfDay, endOfDay) = getStartAndEndOfMonth(date.time)
            transactionViewModel.getTransactionsFromRoomByUidAndTime(firebaseAuth.currentUser?.uid ?: "", startOfDay, endOfDay)
        }
    }
}