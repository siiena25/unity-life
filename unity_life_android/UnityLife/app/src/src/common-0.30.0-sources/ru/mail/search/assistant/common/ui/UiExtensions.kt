package ru.mail.search.assistant.common.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import ru.mail.search.assistant.common.schedulers.PoolDispatcherFactory

fun Fragment.showToast(text: String) {
    requireActivity().runOnUiThread {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }
}

fun Fragment.showKeyboard() {
    val activity = requireActivity()
    activity.currentFocus?.let {
        activity.getSystemService<InputMethodManager>()
            ?.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.showKeyboard() {
    context.getSystemService<InputMethodManager>()
        ?.showSoftInput(this, 0)
}

fun Fragment.hideKeyboard() {
    val activity = requireActivity()
    activity.currentFocus?.let { focusedView ->
        activity.getSystemService<InputMethodManager>()
            ?.hideSoftInputFromWindow(focusedView.windowToken, 0)
    }
}

fun View.hideKeyboard() {
    context.getSystemService<InputMethodManager>()
        ?.hideSoftInputFromWindow(windowToken, 0)
}

inline fun <reified T : Any> Context.getSystemService(): T? {
    return ContextCompat.getSystemService(this, T::class.java)
}

fun Context.copyToClipboard(text: String, label: String = "label") {
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.setPrimaryClip(
        ClipData.newPlainText(label, text)
    )
}

inline fun <reified T : ViewModel> FragmentActivity.provideViewModel(): T {
    return ViewModelProvider(this).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.provideViewModel(factory: ViewModelProvider.Factory): T {
    return ViewModelProvider(this, factory).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.provideViewModel(): T {
    return ViewModelProvider(this).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.provideViewModel(factory: ViewModelProvider.Factory): T {
    return ViewModelProvider(this, factory).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.provideParentViewModel(): T {
    return ViewModelProvider(requireParentFragment()).get(T::class.java)
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.parentViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> = viewModels(
    ownerProducer = { requireParentFragment() },
    factoryProducer = factoryProducer
)

inline fun <T> Fragment.observe(liveData: LiveData<T>, crossinline block: (T?) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer { value -> block(value) })
}

inline fun <T> Fragment.observeNonNull(liveData: LiveData<T>, crossinline block: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer { value -> value?.let(block) })
}

fun Drawable.setColorFilter(@ColorInt rgb: Int) {
    this.mutate().setColorFilter(rgb, PorterDuff.Mode.OVERLAY)
}

inline fun <T> LifecycleOwner.observe(
    liveData: LiveData<T>,
    crossinline block: (T?) -> Unit
) {
    liveData.observe(this, Observer { value -> block(value) })
}

inline fun <T> LifecycleOwner.observeNonNull(
    liveData: LiveData<T>,
    crossinline block: (T) -> Unit
) {
    liveData.observe(this, Observer { value -> value?.let(block) })
}

fun createPoolDispatcher() = PoolDispatcherFactory().createPoolDispatcher()

fun Context.isLightMode(): Boolean {
    return getNightMode() != Configuration.UI_MODE_NIGHT_YES
}

fun Context.isNightMode(): Boolean {
    return getNightMode() == Configuration.UI_MODE_NIGHT_YES
}

fun Context.getNightMode(): Int {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
}

fun Fragment.isNightMode(): Boolean {
    return requireContext().isNightMode()
}