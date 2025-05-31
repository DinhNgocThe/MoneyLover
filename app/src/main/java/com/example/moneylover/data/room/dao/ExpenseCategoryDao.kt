package com.example.moneylover.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moneylover.data.room.model.ExpenseCategory

@Dao
interface ExpenseCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenseCategories: List<ExpenseCategory>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseCategory(expenseCategory: ExpenseCategory)

    @Query("SELECT * FROM tbl_expense_category WHERE type = 'expense' ORDER BY name COLLATE UNICODE") // Use for RecycleView
    fun getExpenseCategories(): LiveData<List<ExpenseCategory>>

    @Query("SELECT * FROM tbl_expense_category WHERE type = 'income' ORDER BY name COLLATE UNICODE") // Use for RecycleView
    fun getIncomeCategories(): LiveData<List<ExpenseCategory>>

    @Query("SELECT iconUrl FROM tbl_expense_category") // Use to preload
    suspend fun getCategoryIcons(): List<String>
}