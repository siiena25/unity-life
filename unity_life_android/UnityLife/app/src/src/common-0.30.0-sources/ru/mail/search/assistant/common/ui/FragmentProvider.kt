package ru.mail.search.assistant.common.ui

import androidx.fragment.app.Fragment

interface FragmentInstantiation {

    fun instantiate(className: String): Fragment?
}
