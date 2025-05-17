package com.example.moneylover.data.firebasemodel

data class ExpenseCategoryFirebase(
    val id: String? = null,
    val uid: String = "",
    val name: String = "",
    val iconUrl: String = "",
    val type: String = "expense"
)