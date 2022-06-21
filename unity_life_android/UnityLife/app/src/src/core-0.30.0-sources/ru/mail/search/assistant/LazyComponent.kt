package ru.mail.search.assistant

/**
 * Component with lazy initialization
 */
internal abstract class LazyComponent<T> {

    @Volatile
    private var component: T? = null
    @Volatile
    private var isReleased = false

    /**
     * Require initialized instance of the component
     */
    fun require(): T {
        return component ?: synchronized(this) {
            if (isReleased) throw RuntimeException("Component already released")
            component ?: run {
                val newInstance = create()
                component = newInstance
                newInstance
            }
        }
    }

    abstract fun create(): T

    /**
     * Close created component
     */
    fun close() {
        synchronized(this) {
            isReleased = true
            component?.let {
                onClose(it)
                component = null
            }
        }
    }

    open fun onClose(component: T) {}
}