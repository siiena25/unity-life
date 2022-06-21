package ru.mail.search.assistant.common.util

/**
 * Create [SecureString] from [String].
 */
fun String.secure(): SecureString {
    return SecureString(this)
}

fun String.secureIfNotEmpty(): SecureString? {
    return takeIf { it.isNotEmpty() }?.secure()
}

/**
 * Used to indicate sensitive data and overrides [toString] method for safe logging.
 */
class SecureString(val raw: String) : CharSequence by raw {

    override fun toString(): String = ""

    override fun equals(other: Any?): Boolean {
        return other is SecureString && raw == other.raw || other is String && raw == other
    }

    override fun hashCode(): Int = raw.hashCode()
}