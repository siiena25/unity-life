package com.example.unitylife.network.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthResponse(
    @field:Json(name = "access")
    val token: String? = null,

    @field:Json(name = "is_registration")
    val isRegistration: Boolean
) : Parcelable