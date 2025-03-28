package com.example.moneylover.data.firebasemodel

import com.google.firebase.Timestamp

data class UserFirebase(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val createdAt: Timestamp? = null
)