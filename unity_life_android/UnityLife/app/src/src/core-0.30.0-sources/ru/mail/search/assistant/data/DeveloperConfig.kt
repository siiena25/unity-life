package ru.mail.search.assistant.data

import ru.mail.search.assistant.entities.ApiHost

interface DeveloperConfig {

    companion object {
        const val DEFAULT_SKILL_SERVER = "default"
    }

    fun getApiHost(): ApiHost
    fun getSkillServerType(host: ApiHost): String
}