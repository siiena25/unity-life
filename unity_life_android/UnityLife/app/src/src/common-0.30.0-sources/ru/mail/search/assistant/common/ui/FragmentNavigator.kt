package ru.mail.search.assistant.common.ui

import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class FragmentNavigator(
    val fragmentManager: FragmentManager,
    @IdRes private val containerId: Int
) {

    fun tryBackTo(transactionName: String?): Boolean {
        if (fragmentManager.isNotEmpty()) {
            return if (transactionName != null) {
                tryBackToTransaction(transactionName)
            } else {
                backToRoot()
                true
            }
        }
        return false
    }

    fun findByTag(tag: String): Fragment? {
        return fragmentManager.findFragmentByTag(tag)
    }

    fun back(): Boolean {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            return true
        }
        return false
    }

    inline fun <reified T : Fragment> replace(
        arguments: Bundle? = null,
        addToBackStack: Boolean = false,
        clearBackStack: Boolean = false,
        tag: String? = null
    ) {
        replace(
            fragment = T::class.java,
            arguments = arguments,
            addToBackStack = addToBackStack,
            clearBackStack = clearBackStack,
            tag = tag
        )
    }

    fun replace(
        fragment: Class<out Fragment>,
        arguments: Bundle? = null,
        addToBackStack: Boolean = false,
        clearBackStack: Boolean = false,
        tag: String? = null
    ) {
        fragmentManager.changeFragment(addToBackStack, clearBackStack) {
            replace(containerId, fragment, arguments, tag)
        }
    }

    fun replace(
        fragment: Fragment,
        addToBackStack: Boolean = false,
        clearBackStack: Boolean = false,
        tag: String? = null,
        @AnimatorRes @AnimRes enterAnimation: Int = 0,
        @AnimatorRes @AnimRes exitAnimation: Int = 0,
    ) {
        fragmentManager.changeFragment(addToBackStack, clearBackStack) {
            setCustomAnimations(
                enterAnimation,
                exitAnimation,
            )
            replace(containerId, fragment, tag)
        }
    }

    inline fun <reified T : Fragment> add(
        name: String? = null,
        arguments: Bundle? = null,
        addToBackStack: Boolean = true,
        @AnimatorRes @AnimRes enterAnimation: Int = 0,
        @AnimatorRes @AnimRes exitAnimation: Int = 0
    ) {
        add(
            fragment = T::class.java,
            name = name,
            arguments = arguments,
            addToBackStack = addToBackStack,
            enterAnimation = enterAnimation,
            exitAnimation = exitAnimation
        )
    }

    fun add(
        fragment: Class<out Fragment>,
        name: String?,
        arguments: Bundle? = null,
        addToBackStack: Boolean = true,
        @AnimatorRes @AnimRes enterAnimation: Int = 0,
        @AnimatorRes @AnimRes exitAnimation: Int = 0
    ) {
        fragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            setCustomAnimations(
                enterAnimation,
                0,
                0,
                exitAnimation
            )
            add(containerId, fragment, arguments)
            if (addToBackStack) {
                addToBackStack(name)
            }
            commit()
        }
    }

    fun add(
        fragment: Fragment,
        name: String? = null,
        addToBackStack: Boolean = true,
        @AnimatorRes @AnimRes enterAnimation: Int = 0,
        @AnimatorRes @AnimRes exitAnimation: Int = 0,
        arguments: Bundle? = null,
        target: Fragment? = null,
        requestCode: Int = -1,
    ) {
        arguments?.let { fragment.arguments = arguments }
        target?.let { fragment.setTargetFragment(target, requestCode) }
        fragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            setCustomAnimations(
                enterAnimation,
                0,
                0,
                exitAnimation
            )
            add(containerId, fragment)
            if (addToBackStack) {
                addToBackStack(name)
            }
            commit()
        }
    }

    inline fun <reified T : Fragment> addToRoot(
        name: String,
        arguments: Bundle? = null,
        @AnimatorRes @AnimRes enterAnimation: Int = 0,
        @AnimatorRes @AnimRes exitAnimation: Int = 0
    ) {
        addToRoot(
            fragment = T::class.java,
            name = name,
            arguments = arguments,
            enterAnimation = enterAnimation,
            exitAnimation = exitAnimation
        )
    }

    fun addToRoot(
        fragment: Class<out Fragment>,
        name: String,
        arguments: Bundle? = null,
        @AnimatorRes @AnimRes enterAnimation: Int = 0,
        @AnimatorRes @AnimRes exitAnimation: Int = 0
    ) {
        if (fragmentManager.backStackEntryCount == 0) {
            add(fragment, name, arguments, true, enterAnimation, exitAnimation)
        } else {
            replace(fragment, arguments, addToBackStack = true, clearBackStack = true, tag = name)
        }
    }

    fun instantiateFragment(fragment: Class<out Fragment>): Fragment {
        return fragmentManager.fragmentFactory.instantiate(fragment.classLoader!!, fragment.name)
    }

    inline fun <reified T : Fragment> remove() {
        fragmentManager
            .fragments
            .filterIsInstance<T>()
            .firstOrNull()
            ?.let { fragment ->
                fragmentManager.beginTransaction().apply {
                    remove(fragment)
                    commit()
                }
            }
    }

    private fun backToRoot() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun tryBackToTransaction(transactionName: String): Boolean {
        if (fragmentManager.backStackContains(transactionName)) {
            fragmentManager.popBackStack(transactionName, 0)
            return true
        }
        return false
    }

    private fun FragmentManager.isNotEmpty(): Boolean {
        return fragments.isNotEmpty() || backStackEntryCount > 0
    }

    private fun FragmentManager.backStackContains(transactionName: String): Boolean {
        for (idx in backStackEntryCount - 1 downTo 0) {
            if (getBackStackEntryAt(idx).name == transactionName) {
                return true
            }
        }
        return false
    }

    private fun FragmentManager.changeFragment(
        addToBackStack: Boolean,
        clearBackStack: Boolean,
        action: FragmentTransaction.() -> Unit
    ) {
        if (clearBackStack) {
            while (backStackEntryCount > 0) {
                popBackStackImmediate()
            }
        }
        commit {
            action()
            if (addToBackStack) {
                addToBackStack("")
            }
        }
    }

    private inline fun FragmentManager.commit(
        allowStateLoss: Boolean = false,
        body: FragmentTransaction.() -> Unit
    ) {
        val transaction = beginTransaction()
        transaction.body()
        if (allowStateLoss) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }
    }

    private fun FragmentManager.commit(body: FragmentTransaction.() -> Unit) {
        val transaction = beginTransaction()
        transaction.body()
        transaction.commit()
    }
}