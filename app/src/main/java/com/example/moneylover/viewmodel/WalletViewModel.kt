package com.example.moneylover.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneylover.data.firebasemodel.WalletFirebase
import com.example.moneylover.data.repository.WalletRepository
import com.example.moneylover.data.room.model.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletViewModel(context: Application) : ViewModel() {
    private val walletRepository = WalletRepository(context)
    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet: StateFlow<Wallet?> = _wallet

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

    fun loadWalletFromRoomByUid(uid: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                walletRepository.loadWalletFromRoomByUid(uid).collect { value ->
                    _wallet.value = value
                }
            }
        }
    }

    suspend fun getWalletFromRoomByUid(uid: String): Wallet? {
        return walletRepository.getWalletFromRoomByUid(uid)
    }

    suspend fun getWalletFromFirestoreByUid(uid: String): WalletFirebase? {
        return walletRepository.getWalletFromFirestoreByUid(uid)
    }

    fun updateWalletToFirestore(walletFirebase: WalletFirebase) {
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.updateWalletToFirestore(walletFirebase)
        }
    }

    fun updateWalletToRoom(wallet: Wallet) {
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.updateWalletToRoom(wallet)
        }
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