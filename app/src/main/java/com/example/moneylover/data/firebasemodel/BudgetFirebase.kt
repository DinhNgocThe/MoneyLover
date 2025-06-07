package com.example.moneylover.data.firebasemodel

import com.example.moneylover.data.room.model.Budget

data class BudgetFirebase(
    val id: String? = "",
    val uid: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val isActive: Boolean = true
) {
    fun toBudget(): Budget {
        return Budget(
            id = id ?: "",
            uid = uid,
            amount = amount,
            category = category
        )
    }
}