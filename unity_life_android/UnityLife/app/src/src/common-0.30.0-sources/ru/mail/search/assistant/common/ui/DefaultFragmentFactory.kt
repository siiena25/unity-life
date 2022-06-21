package ru.mail.search.assistant.common.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class DefaultFragmentFactory(
    private val instantiator: FragmentInstantiation
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return instantiator.instantiate(className)
            ?: super.instantiate(classLoader, className)
    }
}