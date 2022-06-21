package ru.mail.search.assistant.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.mail.search.assistant.commands.processor.model.InteractionMethod
import ru.mail.search.assistant.common.permissions.PermissionsCallback
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.entities.InputMethod
import ru.mail.search.assistant.entities.assistant.AssistantStatus
import ru.mail.search.assistant.entities.navigation.ActivityNavigationAction
import ru.mail.search.assistant.interactor.AssistantStatusAdapter

class AssistantContextRepository(private val logger: Logger?) : AssistantStatusAdapter {

    private companion object {

        private const val TAG = "AssistantContextRep"
    }

    private val userInputRequests = BroadcastChannel<InputMethod>(1)
    private val recordingStatus = ConflatedBroadcastChannel(AssistantStatus.DEFAULT)
    private val activityNavigationChannel = BroadcastChannel<ActivityNavigationAction>(1)
    private val permissionRequestChannel = BroadcastChannel<PermissionsCallback.Request>(1)
    private val inputTextChannel = BroadcastChannel<String>(1)
    private val recordStack = RecordStack()
    private val commandErrorsChannel = BroadcastChannel<Throwable>(1)

    @Volatile
    private var currentInteractionMethod = InteractionMethod.VOICE

    override fun observeInputRequests(): Flow<InputMethod> {
        return userInputRequests.asFlow()
    }

    override fun observeRecordingStatus(): Flow<AssistantStatus> {
        return recordingStatus.asFlow().distinctUntilChanged()
    }

    override fun observeNavigationRequests(): Flow<ActivityNavigationAction> {
        return activityNavigationChannel.asFlow()
    }

    override fun observePermissionRequests(): Flow<PermissionsCallback.Request> {
        return permissionRequestChannel.asFlow()
    }

    override fun observeInputText(): Flow<String> = inputTextChannel.asFlow()

    fun setInteractionMethod(method: InteractionMethod) {
        currentInteractionMethod = method
    }

    fun getInteractionMethod(): InteractionMethod {
        return currentInteractionMethod
    }

    suspend fun setRecordingStatus(status: AssistantStatus, owner: String) {
        val actualState = try {
            recordStack.define(status, owner)
        } catch (error: Throwable) {
            recordStack.clear()
            if (error !is CancellationException) {
                logger?.e(TAG, error)
                recordStack.define(status, owner)
            } else {
                throw error
            }
        }
        recordingStatus.offer(actualState)
    }

    fun requestUserInput(method: InputMethod) {
        userInputRequests.offer(method)
    }

    fun requestUrlNavigation(url: String) {
        requestNavigation(ActivityNavigationAction.OpenUrl(url))
    }

    fun requestPhoneCallNavigation(phoneNumber: String) {
        requestNavigation(ActivityNavigationAction.OpenPhoneCall(phoneNumber))
    }

    fun requestSmsNavigation(phoneNumber: String, text: String) {
        requestNavigation(ActivityNavigationAction.OpenSms(phoneNumber, text))
    }

    fun onCommandError(error: Throwable) {
        commandErrorsChannel.offer(error)
    }

    fun requestPermission(permission: List<String>, requestCode: Int) {
        permissionRequestChannel.offer(PermissionsCallback.Request(permission, requestCode))
    }

    fun observeCommandErrors(): Flow<Throwable> {
        return commandErrorsChannel.asFlow()
    }

    fun setTextToInput(text: String) = inputTextChannel.offer(text)

    private fun requestNavigation(action: ActivityNavigationAction) {
        activityNavigationChannel.offer(action)
    }
}