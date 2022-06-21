package ru.mail.search.assistant.data.local.auth

interface Cipher {

    fun encrypt(data: List<String>, alias: String): List<String>

    fun decrypt(data: List<String>, alias: String): List<String>

    fun deleteEntry(alias: String)
}