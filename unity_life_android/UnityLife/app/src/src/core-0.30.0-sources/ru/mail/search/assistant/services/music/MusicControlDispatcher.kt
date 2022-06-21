package ru.mail.search.assistant.services.music

import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.Player
import ru.mail.search.assistant.interactor.PlayerLimitInteractor

internal class MusicControlDispatcher(
    private val limitInteractor: PlayerLimitInteractor
) : DefaultControlDispatcher() {

    override fun dispatchSetPlayWhenReady(player: Player, playWhenReady: Boolean): Boolean {
        return if (playWhenReady && player.playbackState == Player.STATE_READY) {
            if (limitInteractor.isPlayingAvailable()) {
                super.dispatchSetPlayWhenReady(player, playWhenReady)
            } else {
                false
            }
        } else {
            super.dispatchSetPlayWhenReady(player, playWhenReady)
        }
    }
}