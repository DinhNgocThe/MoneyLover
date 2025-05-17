package com.example.moneylover.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moneylover.data.firebasemodel.TransactionFirebase
import com.example.moneylover.data.repository.TransactionRepository
import com.example.moneylover.data.room.model.Transaction

class TransactionViewModel(private val context: Application) : ViewModel() {
    private val transactionRepository = TransactionRepository(context)

    suspend fun insertTransaction(transactionFirebase: TransactionFirebase) {
        val id = transactionRepository.insertTransactionToFirestore(transactionFirebase)
        if (id != null) {
            val transaction = transactionFirebase.toTransaction()
            transactionRepository.insertTransactionToRoom(transaction)
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