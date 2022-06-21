package ru.mail.search.assistant.common.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveDataEvent<T> : MutableLiveData<T>() {

    private val isPending = AtomicBoolean()

    private val map = mutableMapOf<Observer<in T>, Observer<T>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, createSingleEventObserver(observer))
    }

    override fun observeForever(observer: Observer<in T>) {
        createSingleEventObserver(observer).let {
            map[observer] = it
            super.observeForever(it)
        }
    }

    override fun removeObserver(observer: Observer<in T>) {
        map[observer]?.let {
            map.remove(observer)
            super.removeObserver(it)
        } ?: super.removeObserver(observer)
    }

    override fun setValue(value: T?) {
        isPending.set(true)
        super.setValue(value)
    }

    private fun createSingleEventObserver(observer: Observer<in T>): Observer<T> {
        return Observer { t ->
            if (isPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }
}
