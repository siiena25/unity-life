package com.example.unitylife.di.modules.global

import androidx.annotation.NonNull
import com.example.unitylife.db.AppDatabase
import com.example.unitylife.data.source.local.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DaoProvider {

    @Provides
    @Singleton
    @NonNull
    fun getUserDao(db: AppDatabase): UserDao = db.getUserDao()
}