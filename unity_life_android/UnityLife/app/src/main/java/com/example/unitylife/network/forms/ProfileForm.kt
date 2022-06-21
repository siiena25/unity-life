package com.example.unitylife.network.forms

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileForm(
        @field:Json(name = "avartar_link")
        var imagePath: String? = null,

        @field:Json(name = "name")
        var fullName: String? = null,

        @field:Json(name = "city")
        var city: String? = null,

        @field:Json(name = "gender")
        var gender: String? = null,

        @Transient
        var birthday: Long? = null,

        @field:Json(name = "birth_date")
        var birthdayISO8601: String? = null,

        @field:Json(name = "email")
        var email: String? = null
) : Parcelable
