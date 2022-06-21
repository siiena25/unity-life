package ru.mail.search.assistant.core

import ru.mail.search.assistant.data.SkillListRepository
import ru.mail.search.assistant.api.suggests.AssistantSkill

class MarusiaFacilities(private val skillListRepository: SkillListRepository) {

    suspend fun loadSkillList(): List<AssistantSkill> {
        return skillListRepository.loadSkillList()
    }
}