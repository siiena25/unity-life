package com.llc.aceplace_ru.di.modules.global

import android.content.Context
import androidx.annotation.NonNull
import dagger.Module
import dagger.Provides
import utils.SharedPreferencesStorage
import javax.inject.Singleton

@Module
class SharedPreferencesModule {

    @Provides
    @NonNull
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferencesStorage {
        return SharedPreferencesStorage(context)
    }
}