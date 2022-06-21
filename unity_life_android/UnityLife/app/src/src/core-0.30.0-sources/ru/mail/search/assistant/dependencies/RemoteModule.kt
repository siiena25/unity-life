package ru.mail.search.assistant.dependencies

import ru.mail.search.assistant.api.phrase.PhraseApi
import ru.mail.search.assistant.api.statistics.devicestat.DeviceStatDataSource
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatDataSource
import ru.mail.search.assistant.api.statistics.playerstatus.PlayerStatusDataSource
import ru.mail.search.assistant.api.statistics.rtlog.RtLogRemoteDataSource
import ru.mail.search.assistant.common.data.TimeZoneProvider
import ru.mail.search.assistant.common.data.remote.NetworkConfig
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClientFactory
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.http.common.HttpClient
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.MailPhraseParamsProvider
import ru.mail.search.assistant.data.news.NewsSourcesRemoteDataSource
import ru.mail.search.assistant.data.remote.RemoteDataSource
import ru.mail.search.assistant.data.shuffle.data.ShuffleRemoteDataSource
import ru.mail.search.assistant.services.deviceinfo.AdvertisingIdProvider
import ru.mail.search.assistant.services.deviceinfo.DeviceInfoProvider
import ru.mail.search.assistant.services.music.callback.limit.BackgroundMusicDataSource

internal class RemoteModule(
    networkConfig: NetworkConfig,
    analytics: Analytics?,
    val timeZoneProvider: TimeZoneProvider,
    val deviceInfoProvider: DeviceInfoProvider,
    sessionProvider: SessionCredentialsProvider,
    val advertisingIdProvider: AdvertisingIdProvider?,
    httpClient: HttpClient?,
    logger: Logger?,
    val mailPhraseParamsProvider: MailPhraseParamsProvider?,
) {

    val assistantHttpClient =
        AssistantHttpClientFactory(networkConfig, httpClient, sessionProvider, analytics).create()
    val dataSource = RemoteDataSource(assistantHttpClient)
    val phraseApi = PhraseApi(assistantHttpClient, analytics, logger)

    val deviceStatDataSource = DeviceStatDataSource(assistantHttpClient)
    val playerDeviceStatDataSource = PlayerDeviceStatDataSource(deviceStatDataSource)
    val playerStatusDataSource = PlayerStatusDataSource(assistantHttpClient)
    val rtLogRemoteDataSource = RtLogRemoteDataSource(sessionProvider, assistantHttpClient)
    val shuffleRemoteDataSource = ShuffleRemoteDataSource(sessionProvider, assistantHttpClient)
    val newsSourceDataSource = NewsSourcesRemoteDataSource(sessionProvider, assistantHttpClient)
    val backgroundMusicDataSource = BackgroundMusicDataSource(assistantHttpClient, sessionProvider)
}