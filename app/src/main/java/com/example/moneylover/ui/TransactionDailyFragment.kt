package com.example.moneylover.ui

import android.app.Application
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
import com.example.moneylover.databinding.FragmentTransactionDailyBinding
import com.example.moneylover.viewmodel.TransactionViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionDailyFragment : Fragment() {
    private lateinit var binding: FragmentTransactionDailyBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var adapter: TransactionAdapter
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
    ): View? {
        binding = FragmentTransactionDailyBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTransactions()
        initDatePicker()
    }

    private fun loadTransactions() {
        adapter = TransactionAdapter(requireContext(), {})
        binding.rcvTransactionDailyFragment.adapter = adapter
        binding.rcvTransactionDailyFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvTransactionDailyFragment.setHasFixedSize(true)
        val (startOfDay, endOfDay) = getStartAndEndOfDay(calendar.time.time)
        transactionViewModel.getTransactionsFromRoomByUidAndTime(firebaseAuth.currentUser?.uid ?: "", startOfDay, endOfDay)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.transactions.collect { list ->
                    adapter.submitList(list) {
                        binding.rcvTransactionDailyFragment.scrollToPosition(0)
                        if (list.isEmpty()) {
                            binding.txtStateTransactionDailyFragment.visibility = View.VISIBLE
                        } else {
                            binding.txtStateTransactionDailyFragment.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    fun getStartAndEndOfDay(dateInMillis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
        }

        val startOfDay = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endOfDay = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        return startOfDay to endOfDay
    }

    private fun initDatePicker() {
        val today = calendar.time
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(today.time)
            .build()
        binding.txtDateTransactionDailyFragment.text = simpleDateFormat.format(today)

        // Event show date picker
        binding.btnDatePickerTransactionDailyFragment.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER_ADD_TRANSACTION")
        }

        // Event date picker
        datePickerOnPositiveButtonClick()
    }

    private fun datePickerOnPositiveButtonClick() {
        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            binding.txtDateTransactionDailyFragment.text = simpleDateFormat.format(date)
            adapter.submitList(emptyList())
            val (startOfDay, endOfDay) = getStartAndEndOfDay(date.time)
            transactionViewModel.getTransactionsFromRoomByUidAndTime(firebaseAuth.currentUser?.uid ?: "", startOfDay, endOfDay)
        }
    }
}