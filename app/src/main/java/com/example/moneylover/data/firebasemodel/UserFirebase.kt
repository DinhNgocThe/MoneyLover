package com.example.moneylover.data.firebasemodel

import com.google.firebase.Timestamp

data class UserFirebase(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)