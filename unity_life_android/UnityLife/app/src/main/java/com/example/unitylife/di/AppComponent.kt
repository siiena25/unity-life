package com.example.unitylife.di

import com.example.unitylife.di.components.LoginComponent
import com.example.unitylife.di.modules.global.ApiProvider
import com.example.unitylife.di.modules.global.DaoProvider
import com.example.unitylife.di.modules.global.DatabaseModule
import com.example.unitylife.di.modules.global.RetrofitModule
import com.example.unitylife.view_models.GlobalViewModel
import com.llc.aceplace_ru.di.modules.global.SharedPreferencesModule
import com.llc.aceplace_ru.di.modules.global.ViewModelFactoryModule
import dagger.Component
import utils.SharedPreferencesStorage
import javax.inject.Singleton

@Component(
        modules =
        [
            AppModule::class,
            SharedPreferencesModule::class,
            RetrofitModule::class,
            DatabaseModule::class,
            ViewModelFactoryModule::class,
            ApiProvider::class,
            DaoProvider::class,
        ]
)
@Singleton
interface AppComponent {
    fun plusLogin(): LoginComponent

    fun getGlobalViewModel(): GlobalViewModel

    fun getSharedPreferencesStorage(): SharedPreferencesStorage
}