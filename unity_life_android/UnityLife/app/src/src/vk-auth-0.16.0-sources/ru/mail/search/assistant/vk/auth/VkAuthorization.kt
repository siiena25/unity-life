package ru.mail.search.assistant.vk.auth

data class VkAuthorization(
    val userId: Long,
    val accessToken: String
)