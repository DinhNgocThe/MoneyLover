package com.example.moneylover.data.repository

import android.app.Application
import android.util.Log
import com.example.moneylover.data.firebasemodel.WalletFirebase
import com.example.moneylover.data.room.LocalDatabase
import com.example.moneylover.data.room.dao.WalletDao
import com.example.moneylover.data.room.model.Wallet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await

class WalletRepository(context: Application) {
    private val tag = "WalletRepository"
    private val fireStore = FirebaseFirestore.getInstance()
    private val localDatabase = LocalDatabase.getInstance(context)
    private val walletDao = localDatabase.walletDao()

    suspend fun insertWalletToRoom(wallet: Wallet) {
        try {
            walletDao.insertWallet(wallet)
        } catch (e: Exception) {
            Log.e(tag, "Error inserting wallet: ${e.message}", e)
        }
    }

    suspend fun insertWalletToFirestore(walletFirebase: WalletFirebase) {
        try {
            val documentRef = fireStore.collection("wallets").add(walletFirebase).await()
            documentRef.update("id", documentRef.id).await()
            Log.d(tag, "Wallet added with ID: ${documentRef.id}")
        } catch (e: Exception) {
            Log.e(tag, "Error inserting wallet to firestore: ${e.message}", e)
        }
    }

    fun loadWalletFromRoomByUid(uid: String) : Flow<Wallet?> {
        try {
            return walletDao.loadWalletsByUid(uid)
        } catch (e: Exception) {
            Log.e(tag, "Error getting wallet by uid from room: ${e.message}", e)
            return emptyFlow()
        }
    }

    suspend fun getWalletFromRoomByUid(uid: String) : Wallet? {
        try {
            return walletDao.getWalletsByUid(uid)
        } catch (e: Exception) {
            Log.e(tag, "Error getting wallet by uid from room: ${e.message}", e)
            return null
        }
    }

    suspend fun getWalletFromFirestoreByUid(uid: String): WalletFirebase? {
        try {
            return fireStore.collection("wallets").whereEqualTo("uid", uid).get().await().documents[0].toObject(WalletFirebase::class.java)
        } catch (e: Exception) {
            Log.e(tag, "Error getting wallet by uid from firestore: ${e.message}", e)
            return null
        }
    }

    suspend fun updateWalletToFirestore(walletFirebase: WalletFirebase) {
        try {
            walletFirebase.id?.let { id ->
                fireStore.collection("wallets").document(id).set(walletFirebase).await()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating wallet in Firestore: ${e.message}", e)
        }
    }

    suspend fun updateWalletToRoom(wallet: Wallet) {
        try {
            walletDao.updateWallet(wallet)
        } catch (e: Exception) {
            Log.e(tag, "Error updating wallet to room: ${e.message}", e)
        }
    }
}