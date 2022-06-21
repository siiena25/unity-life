package com.example.unitylife.data.source.local

import androidx.room.*
import com.example.unitylife.data.models.LogInfoModel

@Dao
interface LogInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logInfoModel: LogInfoModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(logInfoModel: LogInfoModel)

    @Delete
    suspend fun delete(logInfoModel: LogInfoModel)

    @Query("SELECT * FROM log_info WHERE userId =:userId")
    suspend fun logoutByUserId(userId: Int)

    @Query("DELETE FROM log_info")
    suspend fun deleteAll()
}