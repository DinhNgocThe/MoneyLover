package com.example.moneylover.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_budget")
data class Budget(
    @PrimaryKey val id: String = "",
    val uid: String = "",
    val amount: Double = 0.0,
    val category: String = ""
)