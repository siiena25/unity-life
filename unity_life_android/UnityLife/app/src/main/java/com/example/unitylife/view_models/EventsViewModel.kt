package com.example.unitylife.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitylife.R
import com.example.unitylife.data.models.EventModel
import com.example.unitylife.data.repository.EventRepository
import com.example.unitylife.network.ErrorHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EventState {
    data class GetAllEventsSuccess(val listOfEvents: List<EventModel>) : EventState()
    data class GetCurrentEventsSuccess(val listOfEvents: List<EventModel>) : EventState()
    object CreateEventSuccess : EventState()
    data class Error(val textId: Int) : EventState()
    object Default : EventState()
}

class EventsViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {
    private val state: MutableSharedFlow<EventState> = MutableStateFlow(EventState.Default)
    private val _state: SharedFlow<EventState> = state.asSharedFlow()

    fun getAllEvents(userId: Int) {
        println("getAllEvents: userid=" + userId)
        viewModelScope.launch {
            repository.getAllEvents(userId)
                .catch { onError(it) }
                .collect { state.emit(EventState.GetAllEventsSuccess(it)) }
        }
    }

    fun getCurrentEvents() {
        viewModelScope.launch {
            repository.getCurrentEvents()
                .catch { onError(it) }
                .collect { state.emit(EventState.GetCurrentEventsSuccess(it)) }
        }
    }

    fun createEvent(userId: Int, token: Int) {
        viewModelScope.launch {
            kotlin.runCatching { repository.createEvent(userId, token) }
                .onSuccess { state.emit(EventState.CreateEventSuccess) }
                .onFailure { onError(it) }
        }
    }

    private suspend fun onError(e: Throwable) {
        when(e) {
            is ErrorHandler.AuthorizationError -> state.emit(EventState.Error(R.string.error_authorization))
            is ErrorHandler.AccessForbiddenError -> state.emit(EventState.Error(R.string.error_access_forbidden))
            is ErrorHandler.ResourceNotFoundError -> state.emit(EventState.Error(R.string.error_resource_not_found))
            else -> {
                state.emit(EventState.Error(R.string.error_text_placeholder))
            }
        }
    }

    fun getState() = _state
}