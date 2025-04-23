package com.example.moneylover.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.moneylover.data.room.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Get user by UID
    @Query("SELECT * FROM tbl_user WHERE uid = :uid")
    suspend fun getUserByUid(uid: String): User?
}