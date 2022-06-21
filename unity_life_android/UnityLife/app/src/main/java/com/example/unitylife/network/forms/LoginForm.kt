package com.example.unitylife.network.forms

import com.squareup.moshi.Json

data class LoginForm(
        @field:Json(name = "email")
        var email: String? = null,

        @field:Json(name = "password")
        var password: String? = null,

        @field:Json(name = "push_token")
        var pushToken: String? = null
)
