package com.example.moneylover.data.repository

import android.app.Application
import android.util.Log
import com.example.moneylover.data.firebasemodel.UserFirebase
import com.example.moneylover.data.room.LocalDatabase
import com.example.moneylover.data.room.dao.UserDao
import com.example.moneylover.data.room.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(context: Application) {
    private val tag = "UserRepository"
    private val fireStore = FirebaseFirestore.getInstance()
    private val localDatabase: LocalDatabase = LocalDatabase.getInstance(context)
    private val userDao: UserDao = localDatabase.userDao()

    suspend fun getUserByUidFromRoom(uid: String) : User? {
        try {
            return userDao.getUserByUid(uid)
        } catch (e: Exception) {
            Log.e(tag, "Error getting user by uid from room: ${e.message}", e)
            return null
        }
    }

    suspend fun getUserByUidFromFirestore(uid: String) : UserFirebase? {
        try {
            return fireStore.collection("users").document(uid).get().await().toObject(UserFirebase::class.java)
        } catch (e: Exception) {
            Log.e(tag, "Error getting user by uid from firestore: ${e.message}", e)
            return null
        }
    }

    suspend fun saveUserToRoom(user: User) {
        try {
            userDao.insertUser(user)
        } catch (e: Exception) {
            Log.e(tag, "Error saving user to room: ${e.message}", e)
        }
    }

    suspend fun saveUserToFirestore(user: UserFirebase) {
        try {
            fireStore.collection("users").document(user.uid).set(user).await()
        } catch (e: Exception) {
            Log.e(tag, "Error saving user to firestore: ${e.message}", e)
        }
    }

    suspend fun clearAllTables() {
        withContext(Dispatchers.IO) {
            localDatabase.clearAllTables()
        }
    }
}