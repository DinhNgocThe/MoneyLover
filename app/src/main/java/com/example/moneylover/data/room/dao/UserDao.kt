package com.example.moneylover.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.moneylover.data.room.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // Lấy user theo UID
    @Query("SELECT * FROM user_tbl WHERE uid = :uid")
    suspend fun getUserByUid(uid: String): User?

    // Xóa user theo UID
    @Query("DELETE FROM user_tbl WHERE uid = :uid")
    suspend fun deleteUser(uid: String)
}