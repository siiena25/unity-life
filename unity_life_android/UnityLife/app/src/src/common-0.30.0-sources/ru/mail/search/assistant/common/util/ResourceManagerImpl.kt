package ru.mail.search.assistant.common.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

class ResourceManagerImpl(val context: Context) : ResourceManager {

    private val pluralsRule = CustomPluralsRule()

    override fun getString(@StringRes id: Int): String = context.getString(id)

    @Suppress("SpreadOperator")
    override fun getString(id: Int, vararg formatArg: Any): String {
        return context.getString(id, *formatArg)
    }

    override fun getQuantityString(id: Int, quantity: Int): String =
        context.resources.getQuantityString(id, quantity)

    override fun getQuantityString(id: Int, quantity: Int, formatArgs: Array<Any>): String =
        context.resources.getQuantityString(id, quantity, *formatArgs)

    override fun getDrawable(id: Int): Drawable? {
        return ContextCompat.getDrawable(context, id)
    }

    override fun getCustomPlurals(count: Int, one: Int, other: Int): String {
        return when (pluralsRule.select(count)) {
            CustomPluralsRule.Quantity.ONE -> context.getString(one, count)
            CustomPluralsRule.Quantity.OTHER -> context.getString(other, count)
        }
    }
}
