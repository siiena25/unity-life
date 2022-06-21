package ru.mail.search.assistant.common.util

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * Created by Grigory Fedorov on 03.10.18.
 */
interface ResourceManager {
    fun getString(@StringRes id: Int): String
    fun getString(@StringRes id: Int, vararg formatArg: Any): String
    fun getQuantityString(id: Int, quantity: Int): String
    fun getQuantityString(@PluralsRes id: Int, quantity: Int, formatArgs: Array<Any>): String
    fun getDrawable(@DrawableRes id: Int): Drawable?
    fun getCustomPlurals(count: Int, one: Int, other: Int): String
}
