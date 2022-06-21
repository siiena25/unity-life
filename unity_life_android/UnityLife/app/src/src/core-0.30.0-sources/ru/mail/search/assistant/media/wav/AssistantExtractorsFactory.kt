package ru.mail.search.assistant.media.wav

import com.google.android.exoplayer2.extractor.Extractor
import com.google.android.exoplayer2.extractor.ExtractorsFactory

internal class AssistantExtractorsFactory : ExtractorsFactory {

    private var constantBitrateSeekingEnabled: Boolean = false

    /**
     * Convenience method to set whether approximate seeking using constant bitrate assumptions should
     * be enabled for all extractors that support it. If set to true, the flags required to enable
     * this functionality will be OR'd with those passed to the setters when creating extractor
     * instances. If set to false then the flags passed to the setters will be used without
     * modification.
     *
     * @param constantBitrateSeekingEnabled Whether approximate seeking using a constant bitrate
     * assumption should be enabled for all extractors that support it.
     * @return The factory, for convenience.
     */
    @Synchronized
    fun setConstantBitrateSeekingEnabled(constantBitrateSeekingEnabled: Boolean): AssistantExtractorsFactory {
        this.constantBitrateSeekingEnabled = constantBitrateSeekingEnabled
        return this
    }

    @Synchronized
    override fun createExtractors(): Array<Extractor> {
        return arrayOf(WavExtractor())
    }
}
