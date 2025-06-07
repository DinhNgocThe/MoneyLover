package com.example.moneylover.data.repository

import android.app.Application
import android.util.Log
import com.example.moneylover.data.firebasemodel.BudgetFirebase
import com.example.moneylover.data.room.LocalDatabase
import com.example.moneylover.data.room.model.Budget
import com.example.moneylover.data.room.model.BudgetWithCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await

class BudgetRepository(context: Application) {
    private val tag = "Budget repository"
    private val firestore = FirebaseFirestore.getInstance()
    private val localDatabase = LocalDatabase.getInstance(context)
    private val budgetDao = localDatabase.budgetDao()

    fun getBudgetsFromRoomByUid(uid: String, start: Long, end: Long) : Flow<List<BudgetWithCategory>?> {
        return try {
            budgetDao.getBudgetsByUid(uid, start, end)
        } catch (e: Exception) {
            Log.e(tag, "Error getting budgets by uid: ${e.message}", e)
            emptyFlow()
        }
    }

    suspend fun getBudgetFromFirestoreByUid(uid: String) : List<BudgetFirebase> {
        return try {
            val snapshot = firestore
                .collection("budgets")
                .whereEqualTo("uid", uid)
                .whereEqualTo("isActive", true)
                .get()
                .await()
            snapshot.toObjects(BudgetFirebase::class.java)
        } catch (e: Exception) {
            Log.e(tag, "Error getting budgets from firestore: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun insertBudgetsToRoom(budgets: List<Budget>) {
        try {
            budgetDao.insertBudgets(budgets)
        } catch (e: Exception) {
            Log.e(tag, "Error inserting budget to room: ${e.message}", e)
        }
    }

    suspend fun deleteBudgetFromRoom(budget: Budget) {
        try {
            budgetDao.deleteBudget(budget)
        } catch (e: Exception) {
            Log.e(tag, "Error deleting budget from room: ${e.message}", e)
        }
    }

    suspend fun deleteBudgetFromFirestore(id: String) {
        try {
            firestore
                .collection("budgets")
                .document(id)
                .update("isActive", false)
                .await()
        } catch (e: Exception) {
            Log.e(tag, "Error deleting budget from firestore: ${e.message}", e)
        }
    }

    suspend fun getAllBudget(uid: String): List<Budget> {
        return try {
            budgetDao.getAllBudget(uid)
        } catch (e: Exception) {
            Log.e(tag, "Error getting all budget: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun insertBudgetToFirestore(budget: BudgetFirebase) : String? {
        return try {
            val documentRef = firestore.collection("budgets").add(budget).await()
            documentRef.update("id", documentRef.id).await()
            Log.d(tag, "Budget added with ID: ${documentRef.id}")
            documentRef.id
        } catch (e: Exception) {
            Log.e(tag, "Error inserting budget to firestore: ${e.message}", e)
            null
        }
    }

    suspend fun insertBudgetToRoom(budget: Budget) {
        try {
            budgetDao.insertBudget(budget)
        } catch (e: Exception) {
            Log.e(tag, "Error inserting budget to room: ${e.message}", e)
        }
    }
}