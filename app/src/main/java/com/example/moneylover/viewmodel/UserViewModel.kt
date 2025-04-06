package com.example.moneylover.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneylover.data.firebasemodel.UserFirebase
import com.example.moneylover.data.repository.UserRepository
import com.example.moneylover.data.room.model.User
import kotlinx.coroutines.launch

class UserViewModel(context: Application) : ViewModel() {
    private val userRepository = UserRepository(context)

    suspend fun getUserByUidFromRoom(uid: String) : User? {
        return userRepository.getUserByUidFromRoom(uid)
    }

    suspend fun getUserByUidFromFirestore(uid: String) : UserFirebase? {
        return userRepository.getUserByUidFromFirestore(uid)
    }

    fun saveUserToRoom(user: User) {
        viewModelScope.launch {
            userRepository.saveUserToRoom(user)
        }
    }

    fun saveUserToFirestore(userFirebase: UserFirebase) {
        viewModelScope.launch {
            userRepository.saveUserToFirestore(userFirebase)
        }
    }

    class UserViewModelFactory(private val context: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(context) as T
            }
            throw IllegalArgumentException("Unable construct UserViewModelFactory")
        }
    }
}