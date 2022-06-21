package ru.mail.search.assistant.entities.audio

data class AudioSource(
    val mediaType: String?,
    val uid: String?,
    val sourceType: String?,
    val skillName: String?,
    val source: String?
) {

    val isVk = sourceType == "vk"
    val isRadio = mediaType == "radio"
}