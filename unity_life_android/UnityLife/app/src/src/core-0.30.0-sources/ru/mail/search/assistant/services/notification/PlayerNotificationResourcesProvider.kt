package ru.mail.search.assistant.services.notification

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.exoplayer2.ui.R

interface PlayerNotificationResourcesProvider {

    @get:DrawableRes
    val smallIconRes: Int

    @get:DrawableRes
    val playerCoverPlaceholderRes: Int

    @get:DrawableRes
    val controlPreviousIconRes: Int
        get() = R.drawable.exo_controls_previous

    @get:StringRes
    val controlPreviousLabelRes: Int

    @get:DrawableRes
    val controlNextIconRes: Int
        get() = R.drawable.exo_controls_next

    @get:StringRes
    val controlNextLabelRes: Int

    @get:DrawableRes
    val controlPlayIconRes: Int
        get() = R.drawable.exo_controls_play

    @get:StringRes
    val controlPlayLabelRes: Int

    @get:DrawableRes
    val controlPauseIconRes: Int
        get() = R.drawable.exo_controls_pause

    @get:StringRes
    val controlPauseLabelRes: Int
}