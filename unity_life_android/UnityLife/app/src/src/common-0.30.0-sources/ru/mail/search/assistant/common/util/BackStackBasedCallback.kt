package ru.mail.search.assistant.common.util

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * [backPressedCallback] wrapper, which is enabled if the back stack is not empty.
 */
class BackStackBasedCallback(
    private val fragment: Fragment,
    private val backPressedCallback: OnBackPressedCallback
) {

    private val parentFragmentManager = fragment.parentFragmentManager
    private val childFragmentManager = fragment.childFragmentManager
    private val backStackChangedListener = BackStackChangedListener()
    private val lifecycleCallback = FragmentLifecycleCallback()

    fun register() {
        childFragmentManager.addOnBackStackChangedListener(backStackChangedListener)
        parentFragmentManager.registerFragmentLifecycleCallbacks(lifecycleCallback, false)
        refresh()
    }

    private fun unregister() {
        parentFragmentManager.unregisterFragmentLifecycleCallbacks(lifecycleCallback)
        childFragmentManager.addOnBackStackChangedListener(backStackChangedListener)
    }

    private fun refresh() {
        backPressedCallback.isEnabled = isNotEmptyBackStack()
    }

    private fun isNotEmptyBackStack(): Boolean {
        return childFragmentManager.backStackEntryCount > 0
    }

    private inner class BackStackChangedListener : FragmentManager.OnBackStackChangedListener {

        override fun onBackStackChanged() {
            refresh()
        }
    }

    private inner class FragmentLifecycleCallback : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            if (f === fragment) {
                unregister()
            }
        }
    }
}