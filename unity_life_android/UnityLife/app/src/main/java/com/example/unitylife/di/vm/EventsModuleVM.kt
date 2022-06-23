package com.example.unitylife.di.vm

import androidx.lifecycle.ViewModel
import com.example.unitylife.view_models.EventsViewModel
import com.example.unitylife.view_models.LoginViewModel
import com.llc.aceplace_ru.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EventsModuleVM {

    @Binds
    @IntoMap
    @ViewModelKey(EventsViewModel::class)
    internal abstract fun bindsViewModel(eventsViewModel: EventsViewModel): ViewModel
}