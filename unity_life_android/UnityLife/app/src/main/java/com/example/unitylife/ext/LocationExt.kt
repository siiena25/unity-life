package com.example.unitylife.ext

import com.example.unitylife.network.models.LocationModel

fun LocationModel.buildAddress(): String {
    val builder = StringBuilder()
    if (street != null && street!!.isNotBlank()) {
        builder.append(street)
    } else if (title != null && title!!.isNotBlank()) {
        builder.append(title)
    } else {
        if (country != null && country!!.isNotBlank()) {
            builder.append(country)
                .append(" ")
        }
        if (city != null && city!!.isNotBlank()) {
            builder.append(city)
        }
    }

    return builder.toString().trim()
}