package ru.mail.search.assistant.data

import android.content.Context
import androidx.annotation.WorkerThread
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.mail.search.assistant.data.local.LocalSettingsDataSource
import ru.mail.search.assistant.services.music.MusicMediaSourceCacheProvider

class AssistantSettings(
    private val context: Context,
    private val settings: LocalSettingsDataSource,
    private val messagesRepository: MessagesRepository,
    private val musicMediaSourceCacheProvider: MusicMediaSourceCacheProvider,
    private val settingsRepository: SettingsRepository
) {

    private val kwsAvailability = ConflatedBroadcastChannel(settings.isKwsAvailable)

    fun setKwsAvailability(value: Boolean) {
        settingsRepository.setKwsAvailability(value)
        kwsAvailability.offer(value)
    }

    fun observeKwsAvailability(): Flow<Boolean> {
        return kwsAvailability.asFlow()
    }

    fun setPlayerMaxCacheSizeMb(sizeMb: Int) {
        settings.maxPlayerCacheSizeMb = sizeMb
    }

    @WorkerThread
    fun removeAllMessagesExceptLastN(keptMessagesCount: Int) {
        messagesRepository.removeAllMessagesExceptLastN(keptMessagesCount)
    }

    /**
     * Deletes all dialog messages and clears music player cache
     */
    @WorkerThread
    fun clearAllData() {
        removeAllMessages()
        clearPlayerCache()
    }

    /**
     * Clears all Glide data
     *
     * If application that calls this method uses Glide, it's data also will be cleared
     */
    @WorkerThread
    fun clearGlideData() {
        Glide.get(context).clearDiskCache()
    }

    @WorkerThread
    fun removeAllMessages() {
        messagesRepository.clearDialogData()
    }

    @WorkerThread
    fun clearPlayerCache() {
        SimpleCache.delete(musicMediaSourceCacheProvider.cacheDir, null)
    }
}