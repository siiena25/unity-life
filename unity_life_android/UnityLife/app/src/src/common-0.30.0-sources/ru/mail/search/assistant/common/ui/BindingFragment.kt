package ru.mail.search.assistant.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

open class BindingFragment<T : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T,
) : Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return bindingInflater(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}