package com.example.moneylover.data.room.model

data class BudgetWithCategory(
    val id: String = "",
    val uid: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val name: String = "",
    val iconUrl: String = "",
    val spent: Double = 0.0
)