package com.example.unitylife.di.components

import com.example.unitylife.di.modules.local.LoginModule
import com.example.unitylife.di.scope.LoginScope
import com.example.unitylife.di.vm.LoginModuleVM
import com.example.unitylife.ui.fragments.AuthorizationFragment
import dagger.Subcomponent

@LoginScope
@Subcomponent(
    modules = [LoginModuleVM::class, LoginModule::class]
)
interface LoginComponent {
    fun inject(fragment: AuthorizationFragment)
}