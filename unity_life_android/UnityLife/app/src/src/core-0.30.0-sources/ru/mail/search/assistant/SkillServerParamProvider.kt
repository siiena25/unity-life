package ru.mail.search.assistant

import ru.mail.search.assistant.data.DeveloperConfig

class SkillServerParamProvider(
    private val developerConfig: DeveloperConfig
) {

    fun getSkillServerIfNotDefault(): String? {
        return developerConfig.getSkillServerType(developerConfig.getApiHost())
            .takeIf { skillServer -> skillServer != DeveloperConfig.DEFAULT_SKILL_SERVER }
    }
}