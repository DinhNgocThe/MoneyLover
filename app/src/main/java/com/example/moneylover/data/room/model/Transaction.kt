package com.example.moneylover.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_transaction")
data class Transaction(
    @PrimaryKey val id: String,
    val uid: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val type: String = ""
)