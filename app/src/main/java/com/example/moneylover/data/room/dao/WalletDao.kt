package com.example.moneylover.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moneylover.data.room.model.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet)

    @Update
    suspend fun updateWallet(wallet: Wallet)

    // Load wallet by uid, use for home fragment
    @Query("SELECT * FROM tbl_wallet WHERE uid = :userId LIMIT 1")
    fun loadWalletsByUid(userId: String): Flow<Wallet?>

    // Get wallet by uid, use for edit balance activity
    @Query("SELECT * FROM tbl_wallet WHERE uid = :userId LIMIT 1")
    suspend fun getWalletsByUid(userId: String): Wallet?
}
