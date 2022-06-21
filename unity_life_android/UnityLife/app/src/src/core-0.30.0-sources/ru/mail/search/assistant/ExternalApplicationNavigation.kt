package ru.mail.search.assistant

import android.content.Intent

interface ExternalApplicationNavigation {
    fun startBrowser(url: String)
    fun startActivity(intent: Intent)
}