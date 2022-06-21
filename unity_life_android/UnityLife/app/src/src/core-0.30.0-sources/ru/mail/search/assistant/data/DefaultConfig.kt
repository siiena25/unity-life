package ru.mail.search.assistant.data

import ru.mail.search.assistant.entities.ApiHost

class DefaultConfig : DeveloperConfig {

    override fun getApiHost(): ApiHost = ApiHost.PRODUCTION
    override fun getSkillServerType(host: ApiHost): String = DeveloperConfig.DEFAULT_SKILL_SERVER
}