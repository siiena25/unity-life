package ru.mail.search.assistant.common.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class NavigationLifecycleOwner(private val fragment: Fragment) : LifecycleOwner {

    private val fragmentManager: FragmentManager = fragment.parentFragmentManager
    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        fragmentManager.registerFragmentLifecycleCallbacks(FragmentLifecycleCallback(), false)
        lifecycleRegistry.currentState = getCurrentState(fragment)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private fun getCurrentState(fragment: Fragment): Lifecycle.State {
        val state = fragment.lifecycle.currentState
        return if (state.isAtLeast(Lifecycle.State.STARTED) && fragment.isStateSaved) {
            Lifecycle.State.CREATED
        } else {
            state
        }
    }

    private inner class FragmentLifecycleCallback : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            if (f === fragment) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            }
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            if (f === fragment) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            }
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (f === fragment) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            if (f === fragment) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            }
        }

        override fun onFragmentSaveInstanceState(
            fm: FragmentManager,
            f: Fragment,
            outState: Bundle
        ) {
            if (f === fragment) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            }
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            if (f === fragment) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            }
        }

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            if (f === fragment) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fragmentManager.unregisterFragmentLifecycleCallbacks(this)
            }
        }
    }
}