package com.example.moneylover.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moneylover.data.room.dao.UserDao
import com.example.moneylover.data.room.dao.WalletDao
import com.example.moneylover.data.room.model.User
import com.example.moneylover.data.room.model.Wallet

@Database(entities = [User::class, Wallet::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun walletDao() : WalletDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context) : LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, "money_lover_database.db").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}