package ru.mail.search.assistant.interactor

import kotlinx.coroutines.flow.Flow
import ru.mail.search.assistant.common.permissions.PermissionsCallback
import ru.mail.search.assistant.entities.InputMethod
import ru.mail.search.assistant.entities.assistant.AssistantStatus
import ru.mail.search.assistant.entities.navigation.ActivityNavigationAction

interface AssistantStatusAdapter {

    fun observeNavigationRequests(): Flow<ActivityNavigationAction>

    fun observeInputRequests(): Flow<InputMethod>

    fun observeRecordingStatus(): Flow<AssistantStatus>

    fun observePermissionRequests(): Flow<PermissionsCallback.Request>

    fun observeInputText(): Flow<String>
}