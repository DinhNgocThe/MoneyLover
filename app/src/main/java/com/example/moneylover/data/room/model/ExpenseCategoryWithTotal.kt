package com.example.moneylover.data.room.model

data class ExpenseCategoryWithTotal(
    val id: String,
    val iconUrl: String,
    val name: String,
    val totalAmount: Double
)