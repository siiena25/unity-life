package ru.mail.search.assistant.common.ui

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Created by Grigory Fedorov on 16.10.18.
 */
class PermissionManager(
    private val context: Context,
    private val sharedPreferences: SharedPreferences?
) {

    companion object {
        private const val USER_ASKED_RECORD_PERMISSION =
            "ru.mail.search.assistant.common.ui.USER_ASKED_RECORD_PERMISSION"

        const val CALLS_CONTACTS_PERMISSION_REQUEST_CODE = 14_444
        const val SMS_CONTACTS_PERMISSION_REQUEST_CODE = 14_445
    }

    val recordAudioPermission: String get() = Manifest.permission.RECORD_AUDIO
    val fineLocationPermission: String get() = Manifest.permission.ACCESS_FINE_LOCATION
    val readContactsPermission: String get() = Manifest.permission.READ_CONTACTS
    val resultGranted: Int get() = PackageManager.PERMISSION_GRANTED

    fun setRecordPermissionAsked() {
        sharedPreferences?.edit()?.putBoolean(USER_ASKED_RECORD_PERMISSION, true)?.apply()
    }

    fun checkRecordAudioPermission(): Boolean {
        return checkPermission(recordAudioPermission)
    }

    fun checkFineLocationPermission(): Boolean {
        return checkPermission(fineLocationPermission)
    }

    fun checkReadContactsPermission(): Boolean {
        return checkPermission(readContactsPermission)
    }

    fun getPermissionCheckResult(permission: String): Int {
        return ContextCompat.checkSelfPermission(context, permission)
    }

    fun isUserDeniedRecordPermission(): Boolean {
        return isRecordPermissionAsked() && !checkRecordAudioPermission()
    }

    private fun checkPermission(permission: String): Boolean {
        return getPermissionCheckResult(permission) == resultGranted
    }

    private fun isRecordPermissionAsked(): Boolean {
        return sharedPreferences?.getBoolean(USER_ASKED_RECORD_PERMISSION, false) ?: false
    }
}
