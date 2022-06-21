package ru.mail.search.assistant.services.music

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.google.android.exoplayer2.upstream.cache.Cache
import java.util.concurrent.atomic.AtomicInteger

/**
 * ExoPlayer cache holds lock on a directory, which used for cache. New instance can't be created
 * until previous instance is released, which may lead a problems in some cases.
 */
object ExoPlayerCacheHolder {

    private var refCounter = AtomicInteger(0)

    @Volatile
    private var cache: Cache? = null

    @MainThread
    @Synchronized
    fun getCache(cacheFactory: () -> Cache?): Cache? {
        if (refCounter.incrementAndGet() == 1) {
            cache = cacheFactory()
        }
        return cache
    }

    @WorkerThread
    @Synchronized
    fun release() {
        if (refCounter.decrementAndGet() == 0) {
            cache?.release()
            cache = null
        }
    }
}