package com.example.moneylover.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneylover.data.firebasemodel.ExpenseCategoryFirebase
import com.example.moneylover.data.repository.ExpenseCategoryRepository
import com.example.moneylover.data.room.model.ExpenseCategory
import kotlinx.coroutines.launch

class ExpenseCategoryViewModel(private val context: Application) : ViewModel() {
    private val expenseCategoryRepository = ExpenseCategoryRepository(context)
    val expenseCategories = expenseCategoryRepository.getExpenseCategoriesFromRoom()
    val incomeCategories = expenseCategoryRepository.getIncomeCategoriesFromRoom()

    fun getDefaultExpenseCategoriesFromFirestoreAndSaveToRoom() {
        viewModelScope.launch {
            val defaultExpenseCategories = expenseCategoryRepository
                .getDefaultExpenseCategoriesFromFirestore()
                .map { it.toExpenseCategory() }

            // Change name by res/strings of name
            val localizedList = defaultExpenseCategories.map { expenseCategory ->
                val resId = context.resources.getIdentifier(
                    expenseCategory.name,
                    "string",
                    context.packageName
                )
                val localizedName = if (resId != 0) context.getString(resId) else expenseCategory.name
                expenseCategory.copy(name = localizedName)
            }

            expenseCategoryRepository.insertExpenseCategoriesToRoom(localizedList)
        }
    }

    fun getExpenseCategoriesFromFirestoreByUidAndSaveToRoom(uid: String) {
        viewModelScope.launch {
            val expenseCategories = expenseCategoryRepository
                .getExpenseCategoriesFromFirestoreByUid(uid)
                .map { it.toExpenseCategory() }
            expenseCategoryRepository.insertExpenseCategoriesToRoom(expenseCategories)
        }
    }

    suspend fun insertExpenseCategory(expenseCategoryFirebase: ExpenseCategoryFirebase) {
        val id = expenseCategoryRepository.insertExpenseCategoryToFirestore(expenseCategoryFirebase)
        if (id != null) {
            val expenseCategory = expenseCategoryFirebase.toExpenseCategory().copy(id = id)
            expenseCategoryRepository.insertExpenseCategoryToRoom(expenseCategory)
        }
    }

    suspend fun getCategoryIcons() : List<String> {
        return expenseCategoryRepository.getCategoryIconsFromRoom()
    }

    private fun ExpenseCategoryFirebase.toExpenseCategory(): ExpenseCategory {
        return ExpenseCategory(
            id = id ?: "",
            uid = uid,
            name = name,
            iconUrl = iconUrl,
            type = type
        )
    }

    class ExpenseCategoryViewModelFactory(private val context: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExpenseCategoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ExpenseCategoryViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct ExpenseCategoryViewModelFactory")
        }
    }
}