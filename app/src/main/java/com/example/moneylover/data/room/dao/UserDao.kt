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
    // Thêm hoặc cập nhật user (REPLACE để cập nhật nếu đã tồn tại)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    //Cập nhật user
    @Update
    suspend fun updateUser(user: User)

    // Lấy user theo UID
    @Query("SELECT * FROM user_tbl WHERE uid = :uid")
    fun getUserById(uid: String): Flow<User?>

    // Xóa user theo UID
    @Query("DELETE FROM user_tbl WHERE uid = :uid")
    suspend fun deleteUser(uid: String)

    // Xoá user
    @Delete
    suspend fun deleteUser(user: User)
}