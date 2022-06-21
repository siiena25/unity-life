package ru.mail.search.assistant.services.deviceinfo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.remote.dto.external.Intents

internal class PackageManagerCheckerImpl(
    private val context: Context,
    private val logger: Logger?
) : PackageManagerChecker {

    companion object {
        private const val TAG = "PackageManagerChecker"
    }

    private val applicationList = mutableSetOf<String>()

    init {
        tryCachedAllApplication()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        val br = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_PACKAGE_REMOVED -> applicationList.remove(
                        getPackageFromIntent(
                            intent
                        )
                    )
                }
                if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
                    applicationList.add(getPackageFromIntent(intent))
                }
            }
        }
        context.registerReceiver(br, intentFilter)
    }

    override fun getLaunchIntentForPackage(packageName: String): Intent? {
        return context.packageManager.getLaunchIntentForPackage(packageName)
    }

    override fun getPackageNameFromIntent(intent: Intent): String? {
        return context.packageManager
            .queryIntentActivities(intent, 0)
            .firstOrNull()
            ?.activityInfo
            ?.applicationInfo
            ?.packageName
    }

    override fun checkApp(appId: String, skipCache: Boolean): Boolean {
        if (applicationList.isEmpty()) {
            tryCachedAllApplication()
        }
        return when (getFromPM(appId)) {
            PackageManagerCheckStatus.Contains -> true
            PackageManagerCheckStatus.NotContains -> false
            PackageManagerCheckStatus.Error -> {
                return getFromGlobalList(appId)
            }
        }
    }

    override fun checkIntent(intent: Intent): Boolean {
        return context.packageManager.queryIntentActivities(intent, 0).size > 0
    }

    override fun checkServerIntent(intent: Intents, skipCache: Boolean): Boolean {
        return when (intent.type) {
            "explicit" -> intent.params?.appPackage?.let { packageName ->
                checkApp(packageName, skipCache)
            } ?: false
            "implicit" ->
                intent.params?.action?.let { action ->
                    checkIntent(
                        actionToIntent(
                            action,
                            intent.params.category,
                            intent.params.type,
                            intent.params.data
                        )
                    )
                } ?: false
            else -> false
        }
    }

    override fun actionToIntent(
        action: String,
        category: String?,
        type: String?,
        data: String?
    ): Intent {
        return Intent(action).apply {
            category?.let { addCategory(it) }
            type?.let { setType(it) }
            data?.let { setData(Uri.parse(it)) }
        }
    }

    override fun appIdToIntent(appId: String): Intent? {
        return context.packageManager.getLaunchIntentForPackage(appId)
    }

    private fun getFromGlobalList(appId: String): Boolean = applicationList.contains(appId)

    private fun getFromPM(appId: String): PackageManagerCheckStatus {
        val result = runCatching {
            context.packageManager.getPackageInfo(appId, PackageManager.GET_ACTIVITIES)
        }
        return if (result.isFailure) {
            result.exceptionOrNull()?.let {
                if (it is PackageManager.NameNotFoundException) {
                    PackageManagerCheckStatus.NotContains
                } else {
                    logger?.e(TAG, it, "package manager throw error when check app")
                    PackageManagerCheckStatus.Error
                }
            } ?: PackageManagerCheckStatus.Error
        } else {
            PackageManagerCheckStatus.Contains
        }
    }

    private fun tryCachedAllApplication() {
        runCatching {
            applicationList.addAll(
                context.packageManager.getInstalledApplications(0).map { it.packageName })
        }.onFailure {
            logger?.e(TAG, it, "package manager throw error when cached all app")
        }
    }

    private fun getPackageFromIntent(intent: Intent): String {
        return intent.dataString?.split(":")?.getOrNull(1).orEmpty()
    }

    private sealed class PackageManagerCheckStatus {
        object Contains : PackageManagerCheckStatus()
        object NotContains : PackageManagerCheckStatus()
        object Error : PackageManagerCheckStatus()
    }
}
