package com.example.unitylife.network

import com.example.unitylife.App
import com.example.unitylife.config.DeviceInfo
import utils.SharedPreferencesStorage

private const val APP_VERSION = "App-Version"
private const val OS_VERSION = "Os-Version"
private const val OS_TYPE = "Os-Type"
private const val DEVICE_ID = "Device-ID"
private const val MANUFACTURER = "Manufacturer"
private const val DEVICE_MODEL = "Model_Device"

class Headers {
    private val map = hashMapOf<String, String>()

    init {
        map["Content-Type"] = "application/json; charset=UTF-8"
        map["Authorization"] = "Bearer "
        map[APP_VERSION] = DeviceInfo.getInstance().appVersion
        map[OS_VERSION] = DeviceInfo.getInstance().osVersion
        map[OS_TYPE] = DeviceInfo.getInstance().osType
        map[DEVICE_ID] = DeviceInfo.getInstance().deviceId
        map[MANUFACTURER] = DeviceInfo.getInstance().manufacturer
        map[DEVICE_MODEL] = DeviceInfo.getInstance().deviceModel
    }

    companion object {
        private val instance = Headers()

        @JvmStatic
        fun getInstance(): Headers {
            return instance
        }
    }
}