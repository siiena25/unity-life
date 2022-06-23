package com.example.unitylife.di.components

import com.example.unitylife.di.modules.local.EventsModule
import com.example.unitylife.di.scope.EventScope
import com.example.unitylife.di.vm.EventsModuleVM
import com.example.unitylife.ui.fragments.AllEventsFragment
import com.example.unitylife.ui.fragments.CurrentEventsFragment
import dagger.Subcomponent

@EventScope
@Subcomponent(
    modules = [EventsModuleVM::class, EventsModule::class]
)
interface EventsComponent {
    fun inject(fragment: AllEventsFragment)

    fun inject(fragment: CurrentEventsFragment)
}