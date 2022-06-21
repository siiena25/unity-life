package ru.mail.search.assistant

import android.content.Context
import android.content.SharedPreferences
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.data.locating.LocationProvider
import ru.mail.search.assistant.common.data.remote.NetworkConfig
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.http.common.HttpClient
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.DefaultConfig
import ru.mail.search.assistant.data.DeveloperConfig
import ru.mail.search.assistant.data.DialogModeProvider
import ru.mail.search.assistant.data.MailPhraseParamsProvider
import ru.mail.search.assistant.data.local.auth.AssistantCipherAdapter
import ru.mail.search.assistant.data.local.messages.MessagesStorage
import ru.mail.search.assistant.kws.KeywordSpotter
import ru.mail.search.assistant.services.deviceinfo.AdvertisingIdProvider
import ru.mail.search.assistant.services.deviceinfo.CapabilitiesProvider
import ru.mail.search.assistant.services.deviceinfo.FeatureProvider
import ru.mail.search.assistant.services.deviceinfo.SuggestedCapabilitiesProvider
import ru.mail.search.assistant.services.notification.DefaultPlayerNotificationResourcesProvider
import ru.mail.search.assistant.services.notification.PlayerNotificationManager
import ru.mail.search.assistant.services.notification.PlayerNotificationResourcesProvider
import ru.mail.search.assistant.util.UnstableAssistantApi
import ru.mail.search.assistant.voicemanager.AudioConfig
import java.util.concurrent.atomic.AtomicReference


object Assistant {

    private val currentCore = AtomicReference<AssistantCore>(null)

    fun createCore(
        appProperties: AppProperties,
        networkConfig: NetworkConfig,
        sessionProvider: SessionCredentialsProvider,
        locationProvider: LocationProvider? = null,
        analytics: Analytics? = null,
        cipher: AssistantCipherAdapter? = null,
        preferences: SharedPreferences? = null,
        playerNotificationManager: PlayerNotificationManager? = null,
        notificationResourcesProvider: PlayerNotificationResourcesProvider =
            DefaultPlayerNotificationResourcesProvider(),
        splitExperimentParamProvider: SplitExperimentParamProvider? = null,
        keywordSpotter: KeywordSpotter? = null,
        httpClient: HttpClient? = null,
        developerConfig: DeveloperConfig = DefaultConfig(),
        audioConfig: AudioConfig = AudioConfig(),
        logger: Logger? = null,
        advertisingIdProvider: AdvertisingIdProvider? = null,
        @UnstableAssistantApi
        messagesStorage: MessagesStorage? = null,
        mailPhraseParamsProvider: MailPhraseParamsProvider? = null,
    ): AssistantCore {
        val core = AssistantCore(
            appProperties,
            networkConfig,
            sessionProvider,
            locationProvider,
            analytics,
            cipher,
            preferences,
            playerNotificationManager,
            notificationResourcesProvider,
            splitExperimentParamProvider,
            keywordSpotter,
            httpClient,
            developerConfig,
            audioConfig,
            logger,
            messagesStorage,
            advertisingIdProvider,
            ::onCoreReleased,
            mailPhraseParamsProvider,
        )
        currentCore.getAndSet(core)?.release()
        return core
    }

    fun release() {
        currentCore.getAndSet(null)?.release()
    }

    fun getCurrentCore(): AssistantCore? {
        return currentCore.get()
    }

    private fun onCoreReleased(core: AssistantCore) {
        currentCore.compareAndSet(core, null)
    }

    class AppProperties(
        val context: Context,
        val capabilitiesProvider: CapabilitiesProvider = SuggestedCapabilitiesProvider(),
        val featureProvider: FeatureProvider? = null,
        val dialogModeProvider: DialogModeProvider? = null
    )

    val SUGGESTED_CAPABILITIES: Map<String, Boolean> = mapOf(
        Capability.SUGGESTS to true,
        Capability.CARD to true,
        Capability.NEWS_CARD to true,
        Capability.VK_MUSIC_LINKS to true,
        Capability.CINEMA_CARD to true,
        Capability.SET_ALARM to true,
        Capability.SET_TIMER to true,
        Capability.IMAGES to true,
        Capability.SLIDER to true,
        Capability.SKILL_LIST to true,
        Capability.MAIL_CARD to true,
        Capability.AUTH_CARD to true,
        Capability.OPEN_APP to true,
        Capability.ORGANIZATION_CARD to true,
        Capability.SEARCH_RESULT to true,
        Capability.RECIPES to true,
        Capability.PODCASTS_NEW to true,
        Capability.PLAYER_AUTOPLAY_FLAG to true
    )

    object Capability {

        const val SUGGESTS = "suggests"
        const val CARD = "card"
        const val NEWS_CARD = "news_card"
        const val VK_MUSIC_LINKS = "vk_music_link"
        const val CINEMA_CARD = "cinema_card"
        const val SET_ALARM = "set_alarm"
        const val SET_TIMER = "set_timer"
        const val CANCEL_ALARM = "cancel_alarm"
        const val CANCEL_TIMER = "cancel_timer"
        const val IMAGES = "images"
        const val RECO_STREAM = "reco_stream"
        const val SLIDER = "sliders"
        const val SHOPPING_LIST = "shopping_list"
        const val TRANSLATION_CARD = "translation_card"
        const val INVITE_CARD = "invite_card"
        const val SKILL_LIST = "lists"
        const val MAIL_CARD = "mail_card"
        const val AUTH_CARD = "authorize_card"
        const val OPEN_APP = "open_app"
        const val ORGANIZATION_CARD = "organization_card"
        const val CAPSULE_SETUP_WIZARD = "setup_wizard"
        const val SET_REMINDER = "set_reminder"
        const val SEARCH_RESULT = "search_result"
        const val PUSH_DISABLED = "push_disabled"
        const val RECIPES = "recipes"
        const val VK_AUDIOS_REFRESH = "vk_audios_refresh"
        const val PODCASTS_NEW = "podcasts_new"
        const val SILENT_MODE = "silent_mode"
        const val REMINDER_CARDS = "reminder_cards"
        const val VK_AUTH = "vk_auth"
        const val TEXT_SO_FAR = "text_so_far"
        const val MAIL_HELPER_ARTICLE = "mail_helper_article"
        const val MAIL_HELPER_OPTIONS = "mail_helper_options"
        const val SESSION_SECRET = "session_secret"
        const val MIXER = "mixer"
        const val MAIL_HELPER_CAROUSEL = "mail_helper_carousel"
        const val ENTER_FLOWMODE = "enter_flowmode"
        const val BLUETOOTH_PAIR = "bluetooth_pair"
        const val SETTINGS_GENERAL = "settings_v1_general"
        const val SETTINGS_ALARM = "settings_v1_alarm"
        const val PERIODIC_ALARM = "periodic_alarm"
        const val VK_CALLS = "vk_calls"
        const val SETTINGS_VK_CALLS = "settings_v1_vk_calls"
        const val ANSWER_BY_URL = "answer_by_url"
        const val OPEN_SMARTHOME = "open_smarthome"
        const val UPDATE_MAIL_TOKEN = "update_mail_token"
        const val TTS_BASE_URL = "update_mail_token"
        const val NIGHT_MODE = "night_mode"
        const val SETTINGS_NIGHT_MODE = "settings_v1_night_mode"
        const val OPEN_APP_V2 = "open_app_v2"
        const val CHECK_VK_SUBSCRIPTION = "check_vk_subscription"
        const val WELCOME_SCREEN = "welcome_screen"
        const val LAZY_PLAYLIST = "lazy_playlist"
        const val PLAYER = "player"
        const val PLAYER_AUTOPLAY_FLAG = "player_autoplay_flag"
        const val ONLY_VK_PLAYER = "only_vk_player"
        const val SETTINGS_REMINDER_SYNC = "settings_v1_reminder_sync"
        const val OPUS_DECODER = "opus_decoder"
        const val MP3_DECODER = "mp3_decoder"
        const val OK_AUTH = "ok_auth"
        const val SHORTCUT_COLUMNS = "shortcut_columns"
        const val RECEIVE_APP_ANSWER = "receive_app_answer"
        const val WIDGETS_SCREEN = "widgets_screen"
        const val MAIL_CARD_V2 = "mail_card_v2"
        const val ENABLE_TOS_SCREEN = "enable_tos_screen"
        const val MINIAPP_CARD = "miniapp_card"
        const val RADIO_ACC = "radio_acc"
        const val UNIVERSAL_LAYOUT = "universal_layout"
        const val SETTINGS_V1_ALARM_MUSIC = "settings_v1_alarm_music"
        const val HTML_CARD = "html_card"
        const val PLAYLIST = "playlist"
        const val INIT_PURCHASE_V2 = "init_purchase_v2"
        const val UPGRADE_VK_TOKEN_SCOPES = "upgrade_vk_token_scopes"
        const val TELEPHONY_CALLS = "telephony_calls"
    }
}