package ru.mail.search.assistant.services.deviceinfo

import java.util.*

internal class CapabilitiesConverter(private val capabilities: IntArray) {

    private companion object {

        private const val EMPTY_CAPABILITIES = "0"
        private const val RADIX_HEX = 16
    }

    fun toHex(): String {
        val bitSet = BitSet()
        capabilities.forEach { number -> bitSet.set(number) }
        return if (bitSet.isEmpty) {
            EMPTY_CAPABILITIES
        } else {
            bitSetToHex(bitSet)
        }
    }

    private fun bitSetToHex(bitSet: BitSet): String {
        return buildString {
            bitSet.toLongArray().reversed().forEach { value ->
                val binary = value.toString(RADIX_HEX)
                if (isNotEmpty()) {
                    repeat(RADIX_HEX - binary.length) {
                        append('0')
                    }
                }
                append(binary)
            }
        }
    }
}