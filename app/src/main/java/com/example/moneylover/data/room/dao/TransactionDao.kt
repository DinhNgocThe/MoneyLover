package com.example.moneylover.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moneylover.data.room.model.Transaction
import com.example.moneylover.data.room.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Query("""
        SELECT t.id, t.uid, t.amount, t.description, t.date, t.type, c.iconUrl, c.name 
        FROM tbl_transaction t
        INNER JOIN tbl_expense_category c ON t.type = c.id
        WHERE t.uid = :uid AND t.date BETWEEN :start AND :end
        ORDER BY t.date DESC
    """)
    fun getTransactionsWithCategoryByUidAndTime(
        uid: String,
        start: Long,
        end: Long
    ): Flow<List<TransactionWithCategory>>

    @Query("""
    SELECT IFNULL(SUM(t.amount), 0) 
    FROM tbl_transaction t
    INNER JOIN tbl_expense_category c ON t.type= c.id
    WHERE c.type = 'expense' AND t.date BETWEEN :start AND :end
    """)
    fun getTotalMonthlyExpenses(start: Long, end: Long): Flow<Float?>
}