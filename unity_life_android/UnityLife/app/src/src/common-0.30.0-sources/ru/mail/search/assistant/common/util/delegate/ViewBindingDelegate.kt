package ru.mail.search.assistant.common.util.delegate

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

inline fun <reified VB : ViewBinding> Fragment.viewBinding(): ReadOnlyProperty<Any?, VB> {
    return lifecycled { VB::class.bind(requireView()) }
}

@PublishedApi
internal fun <VB : ViewBinding> KClass<VB>.bind(rootView: View): VB {
    val inflateMethod = java.getBindMethod()
    @Suppress("UNCHECKED_CAST")
    return inflateMethod.invoke(null, rootView) as VB
}

private val bindMethodsCache = mutableMapOf<Class<out ViewBinding>, Method>()

private fun Class<out ViewBinding>.getBindMethod(): Method {
    return bindMethodsCache.getOrPut(this) { getDeclaredMethod("bind", View::class.java) }
}
