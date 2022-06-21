package ru.mail.search.assistant.dependencies

import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.core.RtLog
import ru.mail.search.assistant.data.rtlog.RtLogDeviceTOSEvent
import ru.mail.search.assistant.data.rtlog.RtLogDeviceChunksExtraDataEvent
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.data.rtlog.RtLogDeviceStatEvent
import ru.mail.search.assistant.api.statistics.rtlog.RtLogRepository
import ru.mail.search.assistant.services.deviceinfo.FeatureProvider

internal class RtLogModule(
    remoteModule: RemoteModule,
    featureProvider: FeatureProvider?,
    poolDispatcher: PoolDispatcher,
    logger: Logger?
) {

    private val rtLogRepository = RtLogRepository(remoteModule.rtLogRemoteDataSource)
    val devicePhraseExtraDataEvent =
        RtLogDevicePhraseExtraDataEvent(rtLogRepository, poolDispatcher, logger)
    val deviceChunksExtraDataEvent =
        RtLogDeviceChunksExtraDataEvent(
            rtLogRepository,
            featureProvider,
            poolDispatcher,
            logger
        )
    private val deviceStatEvent = RtLogDeviceStatEvent(rtLogRepository)
    private val deviceTOSEvent = RtLogDeviceTOSEvent(rtLogRepository)
    val rtLog = RtLog(deviceStatEvent, deviceTOSEvent)
}