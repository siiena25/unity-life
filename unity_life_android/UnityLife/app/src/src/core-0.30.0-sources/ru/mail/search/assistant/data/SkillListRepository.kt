package ru.mail.search.assistant.data

import ru.mail.search.assistant.SkillServerParamProvider
import ru.mail.search.assistant.api.suggests.AssistantSkill
import ru.mail.search.assistant.api.suggests.SkillListDataSource
import ru.mail.search.assistant.services.deviceinfo.DeviceInfoProvider

class SkillListRepository(
    private val skillListDataSource: SkillListDataSource,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val settingsRepository: SettingsRepository,
    private val skillServerParamProvider: SkillServerParamProvider
) {

    suspend fun loadSkillList(): List<AssistantSkill> {
        val capabilities = deviceInfoProvider.capabilities
        val childMode = settingsRepository.getChildModeEnabled()
        val selectedSkillServer = skillServerParamProvider.getSkillServerIfNotDefault()
        val dialogMode = deviceInfoProvider.dialogMode
        return skillListDataSource.getSkillList(
            capabilities = capabilities,
            childMode = childMode,
            selectedSkillServer = selectedSkillServer,
            dialogMode = dialogMode
        )
    }
}