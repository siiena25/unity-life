package com.llc.aceplace_ru.di.modules.global

import androidx.lifecycle.ViewModelProvider
import com.llc.aceplace_ru.di.ViewModelsFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelsFactory): ViewModelProvider.Factory
}