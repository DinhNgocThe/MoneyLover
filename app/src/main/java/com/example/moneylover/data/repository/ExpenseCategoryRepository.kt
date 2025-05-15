package com.example.moneylover.data.repository

import android.app.Application
import android.util.Log
import com.example.moneylover.data.firebasemodel.ExpenseCategoryFirebase
import com.example.moneylover.data.room.LocalDatabase
import com.example.moneylover.data.room.model.ExpenseCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExpenseCategoryRepository(context: Application) {
    private val tag = "ExpenseCategoryRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val localDatabase = LocalDatabase.getInstance(context)
    private val expenseCategoryDao = localDatabase.expenseCategoryDao()

    suspend fun getDefaultExpenseCategoriesFromFirestore(): MutableList<ExpenseCategoryFirebase> {
        return try {
            firestore.collection("expense_categories")
                .whereEqualTo("uid", "default")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(ExpenseCategoryFirebase::class.java) }
                .toMutableList()
        } catch (e: Exception) {
            Log.e(tag, "Error getting default expense categories from firestore: ${e.message}", e)
            emptyList<ExpenseCategoryFirebase>().toMutableList()
        }
    }

    suspend fun getExpenseCategoriesFromFirestoreByUid(uid: String): MutableList<ExpenseCategoryFirebase> {
        return try {
            firestore.collection("expense_categories")
                .whereEqualTo("uid", uid)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(ExpenseCategoryFirebase::class.java) }
                .toMutableList()
        } catch (e: Exception) {
            Log.e(tag, "Error getting expense categories from firestore by uid: ${e.message}", e)
            emptyList<ExpenseCategoryFirebase>().toMutableList()
        }
    }

    suspend fun insertExpenseCategoriesToRoom(expenseCategories: List<ExpenseCategory>) {
        try {
            expenseCategoryDao.insertAll(expenseCategories)
        } catch (e: Exception) {
            Log.e(tag, "Error inserting expense categories to room: ${e.message}", e)
        }
    }
}