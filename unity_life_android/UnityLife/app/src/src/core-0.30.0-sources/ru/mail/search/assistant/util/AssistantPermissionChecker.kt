package ru.mail.search.assistant.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Created by Grigory Fedorov on 16.10.18.
 */
class AssistantPermissionChecker(private val context: Context) {

    fun checkRecordAudioPermission(): Boolean {
        return checkPermission(Manifest.permission.RECORD_AUDIO)
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat
            .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
