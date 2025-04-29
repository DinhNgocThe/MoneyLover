package com.example.moneylover.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moneylover.data.room.model.Wallet

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet)

    @Update
    suspend fun updateWallet(wallet: Wallet)

    // Get wallet by uid
    @Query("SELECT * FROM tbl_wallet WHERE uid = :userId LIMIT 1")
    suspend fun getWalletsByUid(userId: String): Wallet?
}
