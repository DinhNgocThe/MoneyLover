package com.example.moneylover.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneylover.data.firebasemodel.TransactionFirebase
import com.example.moneylover.data.repository.TransactionRepository
import com.example.moneylover.data.room.model.Transaction
import com.example.moneylover.data.room.model.TransactionWithCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(private val context: Application) : ViewModel() {
    private val transactionRepository = TransactionRepository(context)
    private val _transactions = MutableStateFlow<List<TransactionWithCategory>>(emptyList())
    val transactions: StateFlow<List<TransactionWithCategory>> = _transactions
    private var roomCollectJob: Job? = null

    suspend fun insertTransaction(transactionFirebase: TransactionFirebase) {
        val id = transactionRepository.insertTransactionToFirestore(transactionFirebase)
        if (id != null) {
            val transaction = Transaction(
                id = id,
                uid = transactionFirebase.uid,
                amount = transactionFirebase.amount,
                description = transactionFirebase.description,
                date = transactionFirebase.date,
                type = transactionFirebase.type
            )
            transactionRepository.insertTransactionToRoom(transaction)
        }
    }

    fun getTransactionsFromFirestoreByUidAndSaveToRoom(uid: String) {
        viewModelScope.launch {
            val transactionsFirebase = transactionRepository.getTransactionsFromFirestoreByUid(uid)
            val transactions = transactionsFirebase.map { it.toTransaction() }
            transactionRepository.insertTransactionsToRoom(transactions)
        }
    }

    private fun TransactionFirebase.toTransaction(): Transaction {
        return Transaction(
            id = id ?: "",
            uid = uid,
            amount = amount,
            description = description,
            date = date,
            type = type
        )
    }

    fun getTransactionsFromRoomByUidAndTime(uid: String, start: Long, end: Long) {
        roomCollectJob?.cancel()

        roomCollectJob = viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.getTransactionsFromRoomByUidAndTime(uid, start, end)
                .collect { list ->
                    _transactions.value = list
                }
        }
    }

    class TransactionViewModelFactory(private val context: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct TransactionViewModelFactory")
        }
    }
}