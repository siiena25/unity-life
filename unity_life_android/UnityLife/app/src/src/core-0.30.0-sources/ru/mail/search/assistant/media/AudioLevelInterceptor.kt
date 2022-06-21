package ru.mail.search.assistant.media

import android.media.audiofx.Visualizer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.util.AssistantPermissionChecker
import ru.mail.search.assistant.util.analytics.event.AssistantError
import ru.mail.search.assistant.util.analytics.logAssistantError

class AudioLevelInterceptor(
    private val permissionManager: AssistantPermissionChecker,
    private val analytics: Analytics?,
    private val logger: Logger?
) {

    companion object {
        private const val TAG = "AudioLevelInterceptor"
    }

    val audioLevel: LiveData<Float> get() = _audioLevel
    private val _audioLevel = MutableLiveData<Float>()

    private val dataCaptureListener = DataCaptureListener()

    private var visualizer: Visualizer? = null
    private var isActive = false

    fun init(audioSessionId: Int) {
        if (permissionManager.checkRecordAudioPermission()) {
            initVisualizer(audioSessionId)
        }
    }

    fun onRelease() {
        releaseVisualizer()
    }

    private fun initVisualizer(audioSessionId: Int) {
        releaseVisualizer()
        visualizer = runCatching { createVisualizer(audioSessionId) }
            .onFailure { error ->
                analytics?.logAssistantError(
                    AssistantError.Module.COMMON,
                    "Failed to create visualizer: %s",
                    error
                )
                logger?.e(
                    TAG,
                    error,
                    "Failed to initialize Visualizer with session: $audioSessionId"
                )
            }
            .getOrNull()
    }

    private fun createVisualizer(audioSessionId: Int): Visualizer {
        return Visualizer(audioSessionId).apply {
            isActive = true
            captureSize = 1
            setDataCaptureListener(
                dataCaptureListener,
                Visualizer.getMaxCaptureRate(),
                true,
                false
            )
            enabled = true
        }
    }

    private fun releaseVisualizer() {
        visualizer?.apply {
            enabled = false
            setDataCaptureListener(null, Visualizer.getMaxCaptureRate(), false, false)
            release()
        }
        visualizer = null
        isActive = false
        _audioLevel.postValue(0f)
    }

    private fun countAudioLevel(waveForm: ByteArray) {
        val intensity = (waveForm[0] + 128f) / 256
        _audioLevel.postValue(intensity)
    }

    private inner class DataCaptureListener : Visualizer.OnDataCaptureListener {

        override fun onFftDataCapture(
            visualizer: Visualizer?,
            fft: ByteArray?,
            samplingRate: Int
        ) {
            // empty because we don't need it
        }

        override fun onWaveFormDataCapture(
            visualizer: Visualizer?,
            waveform: ByteArray?,
            samplingRate: Int
        ) {
            if (waveform != null && isActive) {
                countAudioLevel(waveform)
            }
        }
    }
}