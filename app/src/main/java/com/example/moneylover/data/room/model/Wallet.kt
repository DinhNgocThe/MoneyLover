package com.example.moneylover.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_wallet")
data class Wallet(
    @PrimaryKey val id: String,
    val uid: String = "",
    val balance: Double = 0.0,
    val limitAmount: Double = 0.0,
    val totalExpense: Double = 0.0
)