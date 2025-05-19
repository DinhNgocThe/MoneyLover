package com.example.moneylover.data.firebasemodel

data class TransactionFirebase(
    val id: String? = null,
    val uid: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val type: String = ""
)