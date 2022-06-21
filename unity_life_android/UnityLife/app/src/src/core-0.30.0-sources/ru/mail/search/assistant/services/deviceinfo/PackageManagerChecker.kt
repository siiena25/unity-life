package ru.mail.search.assistant.services.deviceinfo

import android.content.Intent
import ru.mail.search.assistant.data.remote.dto.external.Intents

interface PackageManagerChecker {

    fun getLaunchIntentForPackage(packageName: String): Intent?

    fun getPackageNameFromIntent(intent: Intent): String?

    fun checkApp(appId: String, skipCache: Boolean = false): Boolean

    fun checkIntent(intent: Intent): Boolean

    fun checkServerIntent(intent: Intents, skipCache: Boolean = false): Boolean

    fun actionToIntent(
        action: String,
        category: String? = null,
        type: String? = null,
        data: String? = null
    ): Intent

    fun appIdToIntent(appId: String): Intent?
}
