package com.example.unitylife.di.modules.global

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Room
import com.example.unitylife.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @NonNull
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "coursework_db_android"
        ).fallbackToDestructiveMigration()
                .build()
    }
}