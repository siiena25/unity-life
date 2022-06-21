package ru.mail.search.assistant.data.local.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.security.Key
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

internal class AesEncryption {

    companion object {

        private const val ALGORITHM_TRANSFORMATION = "AES/CBC/PKCS7Padding"
        private const val IV_SEPARATOR = "]"
    }

    fun encrypt(data: List<ByteArray>, key: Key): List<String> {
        val cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION)
        return data.map { item -> encrypt(cipher, key, item) }
    }

    fun decrypt(data: List<String>, key: Key): List<ByteArray> {
        val cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION)
        return data.map { dataItem -> decrypt(cipher, key, dataItem) }
    }

    @RequiresApi(23)
    fun generateKey(alias: String, keyStore: String, random: SecureRandom): SecretKey {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, keyStore)
            .apply { init(getAlgorithmParameterSpec(alias), random) }
            .generateKey()
    }

    private fun encrypt(cipher: Cipher, key: Key, data: ByteArray): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedData = cipher.doFinal(data)
        return encodeSecureData(SecureData(cipher.iv, encryptedData))
    }

    private fun decrypt(cipher: Cipher, key: Key, data: String): ByteArray {
        val encryptedData = decodeSecureData(data)
        val iv = IvParameterSpec(encryptedData.iv)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        return cipher.doFinal(encryptedData.data)
    }

    @RequiresApi(23)
    private fun getAlgorithmParameterSpec(alias: String): AlgorithmParameterSpec {
        val purpose = KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT
        return KeyGenParameterSpec.Builder(alias, purpose)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setKeySize(256)
            .build()
    }

    private fun encodeSecureData(data: SecureData): String {
        return encode(data.iv) + IV_SEPARATOR + encode(data.data)
    }

    private fun decodeSecureData(data: String): SecureData {
        return data.split(IV_SEPARATOR)
            .takeIf { it.size == 2 }
            ?.let { SecureData(decode(it[0]), decode(it[1])) }
            ?: throw IllegalArgumentException("Can't decode encrypted data")
    }

    private fun encode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    private fun decode(data: String): ByteArray {
        return Base64.decode(data, Base64.NO_WRAP)
    }

    private class SecureData(
        val iv: ByteArray,
        val data: ByteArray
    )
}