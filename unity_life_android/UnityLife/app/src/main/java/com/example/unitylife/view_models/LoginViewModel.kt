package com.example.unitylife.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitylife.R
import com.example.unitylife.data.models.UserModel
import com.example.unitylife.data.repository.LoginRepository
import com.example.unitylife.network.ErrorHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object RegisterSuccess : LoginState()
    data class LoginSuccess(val token: Int) : LoginState()
    object LogoutSuccess : LoginState()
    data class Error(val textId: Int) : LoginState()
    object Default : LoginState()
}

class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {
    private val state: MutableSharedFlow<LoginState> = MutableStateFlow(LoginState.Default)
    private val _state: SharedFlow<LoginState> = state.asSharedFlow()

    fun register(userModel: UserModel) {
        viewModelScope.launch {
            kotlin.runCatching { repository.register(userModel) }
                .onSuccess { state.emit(LoginState.RegisterSuccess) }
                .onFailure { onError(it) }
        }
    }

    fun login(userId: Int) {
        viewModelScope.launch {
            repository.login(userId)
                .catch { onError(it) }
                .collect { state.emit(LoginState.LoginSuccess(it)) }
        }
    }

    fun logout(userId: Int) {
        viewModelScope.launch {
            kotlin.runCatching { repository.logout(userId) }
                .onSuccess { state.emit(LoginState.LogoutSuccess) }
                .onFailure { onError(it) }
        }
    }

    private suspend fun onError(e: Throwable) {
        when(e) {
            is ErrorHandler.AuthorizationError -> state.emit(LoginState.Error(R.string.error_authorization))
            is ErrorHandler.AccessForbiddenError -> state.emit(LoginState.Error(R.string.error_access_forbidden))
            is ErrorHandler.ResourceNotFoundError -> state.emit(LoginState.Error(R.string.error_resource_not_found))
            else -> {
                state.emit(LoginState.Error(R.string.error_text_placeholder))
            }
        }
    }

    fun getState() = _state
}