package ru.mail.search.assistant.entities

import androidx.annotation.StringRes
import ru.mail.search.assistant.R
import ru.mail.search.assistant.common.R as RCommon

enum class ApiHost(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val urlRes: Int
) {

    PRODUCTION(
        id = "prod",
        titleRes = R.string.assistant_setting_host_production_title,
        urlRes = RCommon.string.myAssistant_url_prod
    ),
    TEST_1(
        id = "dev",
        titleRes = R.string.assistant_setting_host_test1_title,
        urlRes = RCommon.string.myAssistant_url_test1
    ),
    TEST_2(
        id = "dev_2",
        titleRes = R.string.assistant_setting_host_test2_title,
        urlRes = RCommon.string.myAssistant_url_test2
    ),
    STAG_1(
        id = "stag1",
        titleRes = R.string.assistant_setting_host_stag1_title,
        urlRes = RCommon.string.myAssistant_url_stag1
    );

    companion object {

        fun requireById(id: String): ApiHost {
            return values().first { it.id == id }
        }
    }
}