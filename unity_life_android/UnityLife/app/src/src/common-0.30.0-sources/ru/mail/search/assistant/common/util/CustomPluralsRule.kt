package ru.mail.search.assistant.common.util

import kotlin.math.abs

internal class CustomPluralsRule {

    @Suppress("MagicNumber")
    fun select(countValue: Int): Quantity {
        val count = abs(countValue)
        val countString = count.toString()
        if (countString.length > 1 && countString.endsWith("11")) return Quantity.OTHER
        return if (count % 10 == 1) {
            Quantity.ONE
        } else {
            Quantity.OTHER
        }
    }

    sealed class Quantity {
        object ONE : Quantity()
        object OTHER : Quantity()
    }
}
