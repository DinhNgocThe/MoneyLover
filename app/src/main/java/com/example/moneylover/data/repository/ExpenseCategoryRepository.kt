package com.example.moneylover.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
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

    suspend fun getDefaultExpenseCategoriesFromFirestore(): List<ExpenseCategoryFirebase> {
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

    suspend fun getExpenseCategoriesFromFirestoreByUid(uid: String): List<ExpenseCategoryFirebase> {
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

    suspend fun insertExpenseCategoryToRoom(expenseCategory: ExpenseCategory) {
        try {
            expenseCategoryDao.insertExpenseCategory(expenseCategory)
        } catch (e: Exception) {
            Log.e(tag, "Error inserting expense category to room: ${e.message}", e)
        }
    }

    suspend fun insertExpenseCategoryToFirestore(expenseCategoryFirebase: ExpenseCategoryFirebase) : String? {
        return try {
            val documentRef = firestore.collection("expense_categories").add(expenseCategoryFirebase).await()
            documentRef.update("id", documentRef.id).await()
            Log.d(tag, "Expense category added with ID: ${documentRef.id}")
            documentRef.id
        } catch (e: Exception) {
            Log.e(tag, "Error inserting expense to firestore: ${e.message}", e)
            null
        }
    }

    fun getExpenseCategoriesFromRoom(): LiveData<List<ExpenseCategory>> {
        return expenseCategoryDao.getExpenseCategories()
    }

    fun getIncomeCategoriesFromRoom(): LiveData<List<ExpenseCategory>> {
        return expenseCategoryDao.getIncomeCategories()
    }
}