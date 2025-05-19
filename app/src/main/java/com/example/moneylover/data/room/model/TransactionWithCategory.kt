package com.example.moneylover.data.room.model

data class TransactionWithCategory(
    val id: String,
    val uid: String,
    val amount: Double,
    val description: String,
    val date: Long,
    val type: String,
    val iconUrl: String,
    val name: String
)