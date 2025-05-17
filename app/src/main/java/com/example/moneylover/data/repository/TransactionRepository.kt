package com.example.moneylover.data.repository

import android.app.Application
import android.util.Log
import com.example.moneylover.data.firebasemodel.TransactionFirebase
import com.example.moneylover.data.room.LocalDatabase
import com.example.moneylover.data.room.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TransactionRepository(context: Application) {
    private val tag = "TransactionRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val localDatabase = LocalDatabase.getInstance(context)
    private val transactionDao = localDatabase.transactionDao()

    suspend fun insertTransactionToFirestore(transaction: TransactionFirebase) : String? {
        return try {
            val documentRef = firestore.collection("transactions").add(transaction).await()
            documentRef.update("id", documentRef.id).await()
            Log.d(tag, "Transaction added with ID: ${documentRef.id}")
            documentRef.id
        } catch (e: Exception) {
            Log.e(tag, "Error inserting transaction to firestore: ${e.message}", e)
            null
        }
    }

    suspend fun insertTransactionToRoom(transaction: Transaction) {
        try {
            transactionDao.insertTransaction(transaction)
        } catch (e: Exception) {
            Log.e(tag, "Error inserting transaction to room: ${e.message}", e)
        }
    }
}