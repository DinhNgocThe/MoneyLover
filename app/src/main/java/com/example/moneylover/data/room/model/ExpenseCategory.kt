package com.example.moneylover.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_expense_category")
data class ExpenseCategory(
    @PrimaryKey val id: String = "",
    val uid: String = "",
    val name: String = "",
    val iconUrl: String = ""
)