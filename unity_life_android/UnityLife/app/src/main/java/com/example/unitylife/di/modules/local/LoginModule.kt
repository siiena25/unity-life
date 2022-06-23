package com.example.unitylife.di.modules.local

import androidx.annotation.NonNull
import com.example.unitylife.data.repository.LoginRepository
import com.example.unitylife.data.source.remote.LoginRemoteDataSource
import com.example.unitylife.di.scope.LoginScope
import com.example.unitylife.network.services.LoginService
import dagger.Module
import dagger.Provides
import utils.SharedPreferencesStorage

@Module
class LoginModule {

    @Provides
    @LoginScope
    @NonNull
    fun provideRepository(
        storage: SharedPreferencesStorage,
        loginRemoteDataSource: LoginRemoteDataSource
    ): LoginRepository = LoginRepository(storage, loginRemoteDataSource)

    @Provides
    @LoginScope
    @NonNull
    fun provideRemoteDataSource(
        loginService: LoginService
    ): LoginRemoteDataSource = LoginRemoteDataSource(loginService)
}