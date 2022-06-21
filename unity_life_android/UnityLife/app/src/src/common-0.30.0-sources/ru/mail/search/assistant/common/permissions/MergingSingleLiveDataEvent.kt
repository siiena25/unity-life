package ru.mail.search.assistant.common.permissions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

internal abstract class MergingSingleLiveDataEvent<T> : MutableLiveData<List<T>>() {

    private var isPending = AtomicBoolean()

    override fun observe(
        owner: LifecycleOwner,
        observer: Observer<in List<T>>
    ) {
        super.observe(
            owner
        ) { value ->
            if (isPending.compareAndSet(true, false)) {
                observer.onChanged(value)
            }
        }
    }

    override fun setValue(value: List<T>) {
        val newValue = if (isPending.get()) {
            getValue()
                ?.let { currentResults -> mergeResults(currentResults, value) }
                ?: value
        } else {
            value
        }
        isPending.set(true)
        super.setValue(newValue)
    }

    protected abstract fun mergeResults(
        oldItems: List<T>,
        newItems: List<T>
    ): List<T>
}