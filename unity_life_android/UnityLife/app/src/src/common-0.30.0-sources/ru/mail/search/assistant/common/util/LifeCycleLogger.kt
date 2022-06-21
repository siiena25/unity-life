package ru.mail.search.assistant.common.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class LifeCycleLogger(
    private val componentName: String,
    private val logger: Logger
) : LifecycleObserver {

    private companion object {

        private const val TAG = "ScreenLifecycle"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        logger.i(TAG, "$componentName: create")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        logger.i(TAG, "$componentName: start")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        logger.i(TAG, "$componentName: resume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        logger.i(TAG, "$componentName: pause")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        logger.i(TAG, "$componentName: stop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        logger.i(TAG, "$componentName: destroy")
    }
}