package ru.mail.search.assistant.common.util.delegate

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : Any> Fragment.lifecycled(
    state: Lifecycle.State = Lifecycle.State.INITIALIZED,
    noinline producer: () -> T
): Lifecycled<T> {
    return Lifecycled(state, producer, fragment = this)
}

/**
 * @param state describes first valid state of lifecycle to obtain value.
 * Don't use [Lifecycle.State.CREATED] if you need to take value during creation,
 * for fragment's view lifecycle it is [Fragment.onViewCreated] block as example,
 * in this case use [Lifecycle.State.INITIALIZED].
 * */
class Lifecycled<T : Any>(
    private val state: Lifecycle.State,
    private val producer: () -> T,
    private val fragment: Fragment,
) : ReadOnlyProperty<Any?, T> {

    companion object {
        private fun Lifecycle.State.oppositeEvent(): Lifecycle.Event {
            return when (this) {
                Lifecycle.State.INITIALIZED, Lifecycle.State.CREATED -> Lifecycle.Event.ON_DESTROY
                Lifecycle.State.STARTED -> Lifecycle.Event.ON_STOP
                Lifecycle.State.RESUMED -> Lifecycle.Event.ON_PAUSE
                else -> error("Unsupported state")
            }
        }
    }

    val value: T get() = _value ?: obtainValue()
    val isInitialized get() = _value != null

    private var _value: T? = null

    private val oppositeEvent: Lifecycle.Event = state.oppositeEvent()
    private val eventObserver = LifecycleEventObserver(::onStateChanged)

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) {
            it.lifecycle.addObserver(eventObserver)
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    private fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == oppositeEvent) {
            source.lifecycle.removeObserver(eventObserver)
            _value = null
        }
    }

    private fun obtainValue(): T {
        val isValidState = fragment.viewLifecycleOwner.lifecycle.currentState.isAtLeast(state)
        check(isValidState)
        return producer.invoke()
            .also { _value = it }
    }
}
