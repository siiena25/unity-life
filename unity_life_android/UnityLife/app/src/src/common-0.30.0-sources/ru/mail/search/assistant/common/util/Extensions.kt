package ru.mail.search.assistant.common.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.InterruptedIOException
import java.net.SocketException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

inline fun <reified T : Any> Context.requireSystemService(): T {
    return checkNotNull(ContextCompat.getSystemService(this, T::class.java))
}

fun SharedPreferences.Editor.putDouble(key: String, value: Double) {
    putLong(key, value.toRawBits())
}

fun SharedPreferences.getDouble(key: String, default: Double): Double {
    return Double.fromBits(getLong(key, default.toRawBits()))
}

fun SharedPreferences.getStringOrNull(key: String): String? {
    return getString(key, null)
}

fun SharedPreferences.getStringSafe(key: String, defValue: String): String {
    return getString(key, defValue) ?: defValue
}

fun SharedPreferences.Editor.remove(vararg key: String) {
    key.forEach { remove(it) }
}

fun Bundle.popString(key: String): String? {
    val value = getString(key)
    remove(key)
    return value
}

fun Context.getLaunchIntentForPackage(packageName: String): Intent? {
    return packageManager.getLaunchIntentForPackage(packageName)
}

fun Throwable.isCausedByPoorNetworkConnection(): Boolean {
    return this is UnknownHostException ||
            this is InterruptedIOException ||
            this is SSLException ||
            this is SocketException
}

fun ViewGroup.inflate(resource: Int): View =
    LayoutInflater.from(this.context).inflate(
        resource,
        this,
        false
    )

inline fun View.updatePadding(
    @Px left: Int = paddingLeft,
    @Px top: Int = paddingTop,
    @Px right: Int = paddingRight,
    @Px bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

fun Fragment.setupBackStackOnBackPressedCallback() {
    val backPressedCallback = setupOnBackPressedCallback { childFragmentManager.popBackStack() }
    BackStackBasedCallback(this, backPressedCallback).register()
}

inline fun Fragment.setupOnBackPressedCallback(
    crossinline onBackPressed: (OnBackPressedCallback) -> Unit
): OnBackPressedCallback {
    val callback = createOnBackPressedCallback(onBackPressed)
    requireActivity()
        .onBackPressedDispatcher
        .addCallback(viewLifecycleOwner, callback)
    return callback
}

@Suppress("unused")
inline fun Fragment.createOnBackPressedCallback(
    crossinline onBackPressed: (OnBackPressedCallback) -> Unit
): OnBackPressedCallback {
    return object : OnBackPressedCallback(false) {

        override fun handleOnBackPressed() {
            onBackPressed(this)
        }
    }
}

fun Context.clearAllCookies(callback: () -> Unit) {
    val cookieManager = CookieManager.getInstance()
    cookieManager.removeAllCookies { callback() }
}

fun Fragment.setupLifecycleLogging(name: String, logger: Logger?) {
    logger?.let { lifecycle.addObserver(LifeCycleLogger(name, logger)) }
}