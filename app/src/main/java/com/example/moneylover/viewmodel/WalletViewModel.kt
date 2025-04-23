package com.example.moneylover.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneylover.data.firebasemodel.WalletFirebase
import com.example.moneylover.data.repository.WalletRepository
import com.example.moneylover.data.room.model.Wallet
import kotlinx.coroutines.launch

class WalletViewModel(context: Application) : ViewModel() {
    private val walletRepository = WalletRepository(context)

    fun insertWalletToRoom(wallet: Wallet) {
        viewModelScope.launch {
            walletRepository.insertWalletToRoom(wallet)
        }
    }

    fun insertWalletToFirestore(walletFirebase: WalletFirebase) {
        viewModelScope.launch {
            walletRepository.insertWalletToFirestore(walletFirebase)
        }
    }

    suspend fun getWalletFromRoomByUid(uid: String): Wallet? {
        return walletRepository.getWalletByUidFromRoom(uid)
    }

    suspend fun getWalletFromFirestoreByUid(uid: String): WalletFirebase? {
        return walletRepository.getWalletByUidFromFirestore(uid)
    }

    class WalletViewModelFactory(private val context: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WalletViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct UserViewModelFactory")
        }
    }
}