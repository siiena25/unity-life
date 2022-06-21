package com.example.unitylife.di.modules.global

import androidx.annotation.NonNull
import com.example.unitylife.network.services.LoginService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiServicesModule {

    @Provides
    @Singleton
    @NonNull
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }
}