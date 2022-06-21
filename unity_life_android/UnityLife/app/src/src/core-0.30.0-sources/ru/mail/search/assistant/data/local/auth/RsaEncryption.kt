package ru.mail.search.assistant.data.local.auth

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import java.math.BigInteger
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

internal class RsaEncryption(private val context: Context) {

    companion object {

        private const val ALGORITHM_KEY = "RSA"
        private const val ALGORITHM_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val KEY_LIFETIME_YEARS = 5
    }

    private val sslProvider: String by lazy { getPlatformSslProvider() }

    fun encrypt(data: List<ByteArray>, key: Key): List<String> {
        val cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION, sslProvider)
        return data.map { dataItem -> encrypt(cipher, key, dataItem) }
    }

    fun decrypt(data: List<String>, key: Key): List<ByteArray> {
        val cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION, sslProvider)
        return data.map { dataItem -> decrypt(cipher, key, dataItem) }
    }

    fun generateKey(alias: String, keyStore: String, random: SecureRandom): KeyPair {
        return KeyPairGenerator.getInstance(ALGORITHM_KEY, keyStore)
            .apply { initialize(getAlgorithmParameterSpec(alias), random) }
            .genKeyPair()
    }

    private fun encrypt(cipher: Cipher, key: Key, data: ByteArray): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return encode(cipher.doFinal(data))
    }

    private fun decrypt(cipher: Cipher, key: Key, data: String): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(decode(data))
    }

    private fun getAlgorithmParameterSpec(alias: String): AlgorithmParameterSpec {
        val startDate = GregorianCalendar
            .getInstance(Locale.getDefault())
            .time
        val endDate = GregorianCalendar
            .getInstance(Locale.getDefault())
            .apply { add(Calendar.YEAR, KEY_LIFETIME_YEARS) }
            .time

        return KeyPairGeneratorSpec
            .Builder(context)
            .setAlias(alias)
            .setStartDate(startDate)
            .setEndDate(endDate)
            .setSerialNumber(BigInteger.ONE)
            .setSubject(X500Principal("CN = Secured Preference Store, O = Devliving Online"))
            .build()
    }

    private fun getPlatformSslProvider(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            "AndroidOpenSSL"
        } else {
            "AndroidKeyStoreBCWorkaround"
        }
    }

    private fun encode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    private fun decode(data: String): ByteArray {
        return Base64.decode(data, Base64.NO_WRAP)
    }
}