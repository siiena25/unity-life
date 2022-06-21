package com.example.unitylife.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.unitylife.data.models.*
import com.example.unitylife.data.source.local.*

@Database(
        entities = [
            UserModel::class,
        ],
        version = 1,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}