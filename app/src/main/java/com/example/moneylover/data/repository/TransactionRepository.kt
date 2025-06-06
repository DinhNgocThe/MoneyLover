package com.example.moneylover.data.repository

import android.app.Application
import android.util.Log
import com.example.moneylover.data.firebasemodel.TransactionFirebase
import com.example.moneylover.data.room.LocalDatabase
import com.example.moneylover.data.room.model.ExpenseCategoryWithTotal
import com.example.moneylover.data.room.model.Transaction
import com.example.moneylover.data.room.model.TransactionWithCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

class TransactionRepository(context: Application) {
    private val tag = "TransactionRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val transactionDao = LocalDatabase.getInstance(context).transactionDao()

    suspend fun insertTransactionToFirestore(transaction: TransactionFirebase): String? {
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

    suspend fun getTransactionsFromFirestoreByUid(uid: String): List<TransactionFirebase> {
        return try {
            val (startTime, endTime) = get12MonthRangeUtc()

            val snapshot = firestore.collection("transactions")
                .whereEqualTo("uid", uid)
                .whereGreaterThanOrEqualTo("date", startTime)
                .whereLessThanOrEqualTo("date", endTime)
                .get()
                .await()

            snapshot.toObjects(TransactionFirebase::class.java)
        } catch (e: Exception) {
            Log.e(tag, "Error fetching transactions: ${e.message}", e)
            emptyList()
        }
    }

    private fun get12MonthRangeUtc(): Pair<Long, Long> {
        val now = ZonedDateTime.now(ZoneOffset.UTC)

        val start = now
            .withDayOfMonth(1)
            .minusMonths(11)
            .toInstant()
            .toEpochMilli()

        val end = now
            .with(TemporalAdjusters.lastDayOfMonth())
            .with(LocalDateTime.MAX.toLocalTime())
            .toInstant()
            .toEpochMilli()

        return Pair(start, end)
    }

    suspend fun insertTransactionsToRoom(transactions: List<Transaction>) {
        try {
            transactionDao.insertTransactions(transactions)
        } catch (e: Exception) {
            Log.e(tag, "Error saving transactions to room: ${e.message}", e)
        }
    }

    fun getTransactionsFromRoomByUidAndTime(uid: String, start: Long, end: Long) : Flow<List<TransactionWithCategory>> {
        return try {
            transactionDao.getTransactionsWithCategoryByUidAndTime(uid, start, end)
        } catch (e: Exception) {
            Log.e(tag, "Error getting transactions from room: ${e.message}", e)
            return emptyFlow()
        }
    }

    fun getTotalMonthlyExpenses(start: Long, end: Long): Flow<Float?> {
        return try {
            transactionDao.getTotalMonthlyExpenses(start, end)
        } catch (e: Exception) {
            Log.e(tag, "Error getting total monthly expenses: ${e.message}", e)
            return emptyFlow()
        }
    }

    suspend fun calculateTotalExpenses(start: Long, end: Long): Float? {
        return try {
            transactionDao.calculateTotalExpenses(start, end)
        } catch (e: Exception) {
            Log.e(tag, "Error calculating total expenses: ${e.message}", e)
            null
        }
    }

    suspend fun deleteTransactionById(id: String) {
        try {
            transactionDao.deleteTransactionById(id)
            firestore.collection("transactions").document(id).delete().await()
        } catch (e: Exception) {
            Log.e(tag, "Error deleting transaction: ${e.message}", e)
        }
    }

    fun getTopExpenseCategories(start: Long, end: Long, uid: String): Flow<List<ExpenseCategoryWithTotal>?> {
        return try {
            transactionDao.getTopExpenseCategories(start, end, uid)
        } catch (e: Exception) {
            Log.e(tag, "Error getting top expense categories: ${e.message}", e)
            emptyFlow()
        }
    }
}
