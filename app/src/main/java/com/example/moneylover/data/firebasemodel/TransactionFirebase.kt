package com.example.moneylover.data.firebasemodel

import com.google.firebase.Timestamp

data class TransactionFirebase(
    val id: String? = null,
    val uid: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val type: String = ""
)