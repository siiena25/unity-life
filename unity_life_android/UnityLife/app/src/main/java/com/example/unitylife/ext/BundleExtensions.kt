package com.example.unitylife.ext

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

fun <T> Bundle.put(key: String, value: T) {
    when (value) {
        is Boolean -> putBoolean(key, value)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Float -> putFloat(key, value)
        is Double -> putDouble(key, value)
        is Parcelable -> putParcelable(key, value)
        is Serializable -> putSerializable(key, value)
        is Bundle -> putBundle(key, value)
        else -> throw IllegalStateException("Type of property $key is not supported")
    }
}

fun Bundle.put(key: String, value: ArrayList<out Parcelable>) {
    putParcelableArrayList(key, value)
}

fun Bundle.put(key: String, value: Array<out Parcelable>) {
    putParcelableArray(key, value)
}