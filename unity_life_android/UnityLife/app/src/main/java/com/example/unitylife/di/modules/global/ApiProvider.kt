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
    fun getLoginService(retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    @NonNull
    fun getLEventService(retrofit: Retrofit): EventService =
        retrofit.create(EventService::class.java)
}