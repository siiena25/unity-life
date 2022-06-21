package ru.mail.search.assistant.interactor

import kotlinx.coroutines.flow.Flow
import ru.mail.search.assistant.entities.InputMethod
import ru.mail.search.assistant.commands.processor.model.InteractionMethod
import ru.mail.search.assistant.common.permissions.PermissionsCallback
import ru.mail.search.assistant.data.AssistantContextRepository
import ru.mail.search.assistant.entities.assistant.AssistantStatus
import ru.mail.search.assistant.entities.navigation.ActivityNavigationAction

class AssistantContextInteractor(private val assistantContextRepository: AssistantContextRepository) {

    fun observeInputRequests(): Flow<InputMethod> {
        return assistantContextRepository.observeInputRequests()
    }

    fun observeRecordingStatus(): Flow<AssistantStatus> {
        return assistantContextRepository.observeRecordingStatus()
    }

    fun observeNavigationRequests(): Flow<ActivityNavigationAction> {
        return assistantContextRepository.observeNavigationRequests()
    }

    fun observePermissionRequests(): Flow<PermissionsCallback.Request> {
        return assistantContextRepository.observePermissionRequests()
    }

    fun observeInputText(): Flow<String> = assistantContextRepository.observeInputText()

    fun setInteractionMethod(method: InteractionMethod) {
        assistantContextRepository.setInteractionMethod(method)
    }

    fun requestUserInput(method: InputMethod) {
        assistantContextRepository.requestUserInput(method)
    }

    fun observeCommandErrors(): Flow<Throwable> {
        return assistantContextRepository.observeCommandErrors()
    }
}