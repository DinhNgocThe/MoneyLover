package com.example.moneylover.data.firebasemodel

data class WalletFirebase(
    val id: String? = null,
    val uid: String = "",
    val balance: Double = 0.0,
    val limitAmount: Double = 0.0,
    val totalExpense: Double = 0.0
)