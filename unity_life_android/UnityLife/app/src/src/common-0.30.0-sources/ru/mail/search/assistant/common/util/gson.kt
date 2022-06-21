package ru.mail.search.assistant.common.util

import com.google.gson.*
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import java.math.BigDecimal
import java.util.*

private const val MILLIS_IN_SECOND = 1_000
private const val TIMESTAMP_MAX_SCALE = 3

@Deprecated(
    "Use JsonParser static methods instead",
    replaceWith = ReplaceWith("JsonParser.parseString(json).toObject()")
)
fun JsonParser.parseAsObject(json: String): JsonObject? {
    return this.parse(json).toObject()
}

fun JsonObject.getObject(name: String): JsonObject? {
    return get(name) as? JsonObject
}

fun JsonObject.requireObject(name: String): JsonObject {
    return getObject(name) ?: throw ResultParsingException("Missing $name object")
}

fun JsonObject.getString(name: String): String? {
    return (get(name) as? JsonPrimitive)?.asString
}

fun JsonObject.requireString(name: String): String = requireNotNull(getString(name))

fun JsonObject.getBoolean(name: String, default: Boolean): Boolean {
    return (get(name) as? JsonPrimitive)?.asBoolean ?: default
}

fun JsonObject.getInt(name: String, default: Int): Int {
    return runCatching { (get(name) as? JsonPrimitive)?.asInt }.getOrNull() ?: default
}

fun JsonObject.getInt(name: String): Int? {
    return runCatching { (get(name) as? JsonPrimitive)?.asInt }.getOrNull()
}

fun JsonObject.getFloat(name: String): Float? {
    return runCatching { (get(name) as? JsonPrimitive)?.asFloat }.getOrNull()
}

fun JsonObject.getFloat(name: String, default: Float): Float {
    return runCatching { (get(name) as? JsonPrimitive)?.asFloat }.getOrNull() ?: default
}

fun JsonObject.getLong(name: String, default: Long): Long {
    return runCatching { (get(name) as? JsonPrimitive)?.asLong }.getOrNull() ?: default
}

fun JsonObject.getLong(name: String): Long? {
    return runCatching { (get(name) as? JsonPrimitive)?.asLong }.getOrNull()
}

fun JsonObject.getDouble(name: String): Double? {
    return runCatching { (get(name) as? JsonPrimitive)?.asDouble }.getOrNull()
}

fun JsonObject.parseDate(name: String): Date? {
    val timeS = getDouble(name) ?: return null
    val timeMs = (timeS * MILLIS_IN_SECOND).toLong()
    return Date(timeMs)
}

fun JsonObject.addTimestamp(name: String, timestampMs: Long) {
    val seconds = TimeUtils.millisToSeconds(timestampMs)
    val number = BigDecimal(seconds).let { number ->
        if (number.scale() > TIMESTAMP_MAX_SCALE) {
            number.setScale(TIMESTAMP_MAX_SCALE, BigDecimal.ROUND_HALF_UP)
        } else {
            number
        }
    }
    add(name, JsonPrimitive(number))
}

fun JsonObject.getArray(name: String): JsonArray? {
    return get(name) as? JsonArray
}

fun JsonElement.toObject(): JsonObject? {
    return this as? JsonObject
}

fun JsonElement.toArray(): JsonArray? {
    return this as? JsonArray
}

fun JsonElement.toFloat(): Float? {
    return runCatching { (this as? JsonPrimitive)?.asFloat }.getOrNull()
}

fun JsonElement.getString(): String? {
    return (this as? JsonPrimitive)?.asString
}

inline fun <reified T> Gson.fromJson(data: String): T {
    return fromJson(data, T::class.java)
}
