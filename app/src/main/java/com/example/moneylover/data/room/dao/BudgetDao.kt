package com.example.moneylover.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moneylover.data.room.model.Budget
import com.example.moneylover.data.room.model.BudgetWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Insert
    suspend fun insertBudgets(budgets: List<Budget>)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("""
        SELECT 
        b.id,
        b.uid,
        b.amount,
        b.category,
        ec.name,
        ec.iconUrl,
        IFNULL(SUM(t.amount), 0) AS spent
        FROM tbl_budget b
        INNER JOIN tbl_expense_category ec ON b.category = ec.id
        LEFT JOIN tbl_transaction t ON t.type = b.category AND t.uid = b.uid AND t.date BETWEEN :start AND :end
        WHERE b.uid = :uid
        GROUP BY b.id, b.uid, b.amount, b.category, ec.name, ec.iconUrl
    """)
    fun getBudgetsByUid(uid: String, start: Long, end: Long): Flow<List<BudgetWithCategory>?>

    @Query("SELECT * FROM tbl_budget WHERE uid = :uid")
    suspend fun getAllBudget(uid: String): List<Budget>
}