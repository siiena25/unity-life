package com.example.unitylife.di

import android.content.Context
import androidx.annotation.NonNull
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AppModule @Inject constructor(context: Context) {
    private val appContext = context

    @Provides
    @Singleton
    @NonNull
    fun appContext(): Context = appContext
}