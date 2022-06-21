package ru.mail.search.assistant.common.util

import android.security.NetworkSecurityPolicy

object NetworkUtils {

    val isClearTextTrafficPermitted by lazy {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted
        } else {
            true
        }
    }
}