package com.example.moneylover.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneylover.data.firebasemodel.BudgetFirebase
import com.example.moneylover.data.repository.BudgetRepository
import com.example.moneylover.data.room.model.Budget
import com.example.moneylover.data.room.model.BudgetWithCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(context: Application) : ViewModel() {
    private val budgetRepository = BudgetRepository(context)
    private val _budgets = MutableStateFlow<List<BudgetWithCategory>>(emptyList())
    val budgets: StateFlow<List<BudgetWithCategory>> = _budgets

    fun getBudgetsFromFirestoreByUidAndSaveToRoom(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val budgetsFirebase = budgetRepository.getBudgetFromFirestoreByUid(uid)
            val budgets = budgetsFirebase.map { it.toBudget() }
            budgetRepository.insertBudgetsToRoom(budgets)
        }
    }

    fun getBudgetsFromRoomByUid(uid: String, start: Long, end: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.getBudgetsFromRoomByUid(uid, start, end).collect { list ->
                if (list != null) {
                    _budgets.value = list
                }
            }
        }
    }

    fun deleteBudget(budgetWithCategory: BudgetWithCategory) {
        val budget = Budget(
            id = budgetWithCategory.id,
            uid = budgetWithCategory.uid,
            amount = budgetWithCategory.amount,
            category = budgetWithCategory.category
        )
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.deleteBudgetFromRoom(budget)
            budgetRepository.deleteBudgetFromFirestore(budgetWithCategory.id)
        }
    }

    suspend fun getAllBudget(uid: String): List<Budget> {
        return budgetRepository.getAllBudget(uid)
    }

    fun insertBudgetToFirestoreAndInsertToRoom(budget: BudgetFirebase) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = budgetRepository.insertBudgetToFirestore(budget)
            if (id != null) {
                val budget = Budget(
                    id = id,
                    uid = budget.uid,
                    amount = budget.amount,
                    category = budget.category
                )
                budgetRepository.insertBudgetToRoom(budget)
            }
        }
    }

    class BudgetViewModelFactory(private val context: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BudgetViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct BudgetViewModelFactory")
        }
    }
}