package ru.mail.search.assistant.common.util.analytics

import java.util.concurrent.ConcurrentHashMap

class AnalyticsStore {

    private val map = ConcurrentHashMap<Pair<String, Class<out Any>>, Any>()

    inline fun <reified T : Any> remember(tag: String, value: T) {
        remember(tag, T::class.java, value)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> obtain(tag: String): T? {
        return obtain(tag, T::class.java)
    }

    fun <T : Any> remember(tag: String, clazz: Class<T>, value: T) {
        map[Pair(tag, clazz)] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> obtain(tag: String, clazz: Class<T>): T? {
        return map.remove(Pair(tag, clazz)) as? T
    }
}