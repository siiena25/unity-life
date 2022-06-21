package ru.mail.search.assistant.data.local.auth

import android.content.Context
import android.os.Build
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.SecureRandom

class SimpleCipher(context: Context) : Cipher {

    companion object {

        private const val KEYSTORE_ANDROID = "AndroidKeyStore"
        private const val ALGORITHM_RANDOM = "SHA1PRNG"
        private const val RANDOM_SEED_BYTES_NUMBER = 128
        private const val CHARSET_NAME = "UTF-8"
    }

    private val charset = Charset.forName(CHARSET_NAME)

    private val aesEncryption: AesEncryption by lazy { AesEncryption() }
    private val rsaEncryption: RsaEncryption by lazy { RsaEncryption(context) }

    override fun encrypt(data: List<String>, alias: String): List<String> {
        try {
            val entry = getKeyStore().getEntry(alias, null)
            val byteData = data.map(::stringToBytes)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (entry) {
                    null -> {
                        val key =
                            aesEncryption.generateKey(alias, KEYSTORE_ANDROID, getSecureRandom())
                        aesEncryption.encrypt(byteData, key)
                    }
                    is KeyStore.SecretKeyEntry ->
                        aesEncryption.encrypt(byteData, entry.secretKey)
                    is KeyStore.PrivateKeyEntry ->
                        rsaEncryption.encrypt(byteData, entry.certificate.publicKey)
                    else -> throw CipherException("Unsupported key entry")
                }
            } else {
                when (entry) {
                    null -> {
                        val key =
                            rsaEncryption.generateKey(alias, KEYSTORE_ANDROID, getSecureRandom())
                        rsaEncryption.encrypt(byteData, key.public)
                    }
                    is KeyStore.PrivateKeyEntry ->
                        rsaEncryption.encrypt(byteData, entry.certificate.publicKey)
                    else -> throw CipherException("Unsupported key entry")
                }
            }
        } catch (error: Exception) {
            if (error is CipherException) {
                throw error
            } else {
                throw CipherException("Failed encryption", error)
            }
        }
    }

    override fun decrypt(data: List<String>, alias: String): List<String> {
        try {
            val entry = getKeyStore().getEntry(alias, null)
            val decryptedData = when (entry) {
                is KeyStore.PrivateKeyEntry ->
                    rsaEncryption.decrypt(data, entry.privateKey)
                is KeyStore.SecretKeyEntry -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    aesEncryption.decrypt(data, entry.secretKey)
                } else {
                    throw CipherException("Unsupported key entry")
                }
                else -> throw CipherException("Unsupported key entry")
            }
            return decryptedData.map(::bytesToString)
        } catch (error: Exception) {
            if (error is CipherException) {
                throw error
            } else {
                throw CipherException("Failed decryption", error)
            }
        }
    }

    override fun deleteEntry(alias: String) {
        try {
            getKeyStore().deleteEntry(alias)
        } catch (error: Exception) {
            if (error is CipherException) {
                throw error
            } else {
                throw CipherException("Failed entry deletion", error)
            }
        }
    }

    private fun stringToBytes(data: String): ByteArray {
        return data.toByteArray(charset)
    }

    private fun bytesToString(data: ByteArray): String {
        return String(data, charset)
    }

    private fun getSecureRandom(): SecureRandom {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SecureRandom.getInstanceStrong()
        } else {
            SecureRandom.getInstance(ALGORITHM_RANDOM)
                .apply { setSeed(generateSeed(RANDOM_SEED_BYTES_NUMBER)) }
        }
    }

    private fun getKeyStore(): KeyStore {
        val keystore = KeyStore.getInstance(KEYSTORE_ANDROID)
        keystore.load(null)
        return keystore
    }
}