package ru.mail.search.assistant.data.local.auth

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Thread-safe wrapper for [Cipher]. All operations executes sequentially on single thread.
 */
class AssistantCipherAdapter(
    private val cipher: Cipher,
    private val dispatcher: CoroutineDispatcher
) {

    companion object {

        private const val CIPHER_ALIAS_USER_DATA = "el_userdata"
    }

    suspend fun encryptUserData(data: List<String>): List<String> {
        return withContext(dispatcher) {
            cipher.encrypt(data, CIPHER_ALIAS_USER_DATA)
        }
    }

    suspend fun encryptUserData(data: String): String {
        return withContext(dispatcher) {
            cipher.encrypt(listOf(data), CIPHER_ALIAS_USER_DATA).first()
        }
    }

    suspend fun decryptUserData(data: List<String>): List<String> {
        return withContext(dispatcher) {
            cipher.decrypt(data, CIPHER_ALIAS_USER_DATA)
        }
    }

    suspend fun decryptUserData(data: String): String {
        return withContext(dispatcher) {
            cipher.decrypt(listOf(data), CIPHER_ALIAS_USER_DATA).first()
        }
    }

    suspend fun deleteUserDataEntry() {
        withContext(dispatcher) {
            cipher.deleteEntry(CIPHER_ALIAS_USER_DATA)
        }
    }
}