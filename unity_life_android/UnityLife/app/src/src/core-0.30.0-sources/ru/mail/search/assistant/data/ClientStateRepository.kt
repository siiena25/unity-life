package ru.mail.search.assistant.data

import ru.mail.search.assistant.api.phrase.ClientState
import ru.mail.search.assistant.common.ui.PermissionManager
import ru.mail.search.assistant.common.util.updateAndGetCompat
import java.util.concurrent.atomic.AtomicReference

internal class ClientStateRepository(private val permissionManager: PermissionManager? = null) {

    private val currentPhrase = AtomicReference<Phrase?>(null)

    fun onStartUserRequest(requestId: Int) {
        currentPhrase.updateAndGetCompat { phrase ->
            Phrase(
                requestId = requestId,
                phraseId = null,
                interruptedPhraseId = phrase?.phraseId
            )
        }
    }

    fun getPhraseClientState(requestId: Int): ClientState {
        val phrase = currentPhrase.get()
        return if (phrase?.requestId == requestId && phrase.interruptedPhraseId != null) {
            ClientState(
                interruptedPhraseId = phrase.interruptedPhraseId,
                addressBookPermission = permissionManager?.checkReadContactsPermission(),
            )
        } else {
            ClientState(addressBookPermission = permissionManager?.checkReadContactsPermission())
        }
    }

    fun onPhraseCreated(requestId: Int, phraseId: String) {
        currentPhrase.updateAndGetCompat { phrase ->
            if (phrase?.requestId == requestId) {
                phrase.copy(phraseId = phraseId)
            } else {
                phrase
            }
        }
    }

    fun onFinishUserRequest(requestId: Int) {
        currentPhrase.updateAndGetCompat { phrase ->
            if (phrase?.requestId == requestId) {
                null
            } else {
                phrase
            }
        }
    }

    private data class Phrase(
        val requestId: Int,
        val phraseId: String?,
        val interruptedPhraseId: String?
    )
}