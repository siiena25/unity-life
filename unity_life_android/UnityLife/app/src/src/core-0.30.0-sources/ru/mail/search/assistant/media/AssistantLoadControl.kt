package ru.mail.search.assistant.media

import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.upstream.DefaultAllocator

class AssistantLoadControl(
    minBuffer: Int = BUFFER_MIN,
    maxBuffer: Int = BUFFER_MAX,
    minPlaybackBuffer: Int = BUFFER_MIN_PLAYBACK,
    bufferAfterRebuffer: Int = BUFFER_AFTER_REBUFFER
) : DefaultLoadControl(
    DefaultAllocator(true, 128 * 1024),
    minBuffer,
    maxBuffer,
    minPlaybackBuffer,
    bufferAfterRebuffer,
    DEFAULT_TARGET_BUFFER_BYTES,
    DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS,
    DEFAULT_BACK_BUFFER_DURATION_MS,
    DEFAULT_RETAIN_BACK_BUFFER_FROM_KEYFRAME
) {

    companion object {
        private const val BUFFER_MIN = 1000
        private const val BUFFER_MAX = 15000
        private const val BUFFER_MIN_PLAYBACK = 300
        private const val BUFFER_AFTER_REBUFFER = 600
    }
}