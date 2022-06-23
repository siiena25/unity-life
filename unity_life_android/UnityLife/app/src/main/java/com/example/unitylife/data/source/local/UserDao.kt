package com.example.unitylife.data.source.local

import androidx.room.*
import com.example.unitylife.data.models.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(list: List<UserModel>)

    @Update
    suspend fun update(vararg userModel: UserModel)

    @Delete
    suspend fun delete(vararg userModel: UserModel)

    @Query("SELECT * FROM users WHERE userId =:userId")
    suspend fun getUsersById(userId: String): UserModel?

    @Query("UPDATE users SET token=:token WHERE userId=:userId")
    suspend fun deleteTokenByUserId(userId: Int, token: Int?)
}