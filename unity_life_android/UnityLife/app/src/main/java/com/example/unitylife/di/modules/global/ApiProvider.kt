package com.example.unitylife.di.modules.global

import androidx.annotation.NonNull
import com.example.unitylife.network.services.*
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiProvider {

    inline fun <reified T> createService(service: Class<T>, retrofit: Retrofit): T =
        retrofit.create(T::class.java)

    @Provides
    @Singleton
    @NonNull
    fun getAppealService(retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)
}