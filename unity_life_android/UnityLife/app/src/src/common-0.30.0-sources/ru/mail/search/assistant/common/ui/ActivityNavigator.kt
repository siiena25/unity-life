package ru.mail.search.assistant.common.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.provider.Telephony
import androidx.fragment.app.FragmentActivity
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.Tag
import ru.mail.search.assistant.common.util.getLaunchIntentForPackage

class ActivityNavigator(
    private val activity: FragmentActivity,
    private val logger: Logger?
) {

    fun back() {
        activity.onBackPressed()
    }

    fun systemApplicationSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    fun browser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        runCatching {
            startActivity(browserIntent)
        }.onFailure { error ->
            logger?.e(Tag.ACTIVITY_NAVIGATOR, error, "Error while opening browser")
        }
    }

    fun share(payload: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, payload)
            type = "text/plain"
        }
        startActivity(sendIntent)
    }

    fun sendSms(phoneNumber: String, text: String) {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("address", phoneNumber)
            .putExtra("sms_body", text)

        val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(activity)
        if (defaultSmsPackageName != null) intent.setPackage(defaultSmsPackageName)

        runCatching {
            startActivity(intent)
        }.onFailure { error ->
            logger?.e(Tag.ACTIVITY_NAVIGATOR, error, "Error while initiating sms app")
        }
    }

    fun call(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        runCatching {
            startActivity(callIntent)
        }.onFailure { error ->
            logger?.e(Tag.ACTIVITY_NAVIGATOR, error, "Error while initiating a call")
        }
    }

    fun application(packageName: String): Boolean {
        if (isAppInstalled(activity, packageName)) {
            val launchIntent = activity.getLaunchIntentForPackage(packageName)
            launchIntent?.let {
                startActivity(it)
                return true
            }
        }
        return false
    }

    fun showGeoSettings() {
        showSettings(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    }

    fun showBluetoothSettings() {
        showSettings(Settings.ACTION_BLUETOOTH_SETTINGS)
    }

    fun showWifiSettings() {
        showSettings(Settings.ACTION_WIFI_SETTINGS)
    }

    private fun startActivity(intent: Intent) {
        activity.startActivity(intent)
    }

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showSettings(settings: String) {
        val intent = Intent(settings)
        if (activity.packageManager.resolveActivity(intent, 0) != null) {
            activity.startActivity(intent)
        }
    }
}