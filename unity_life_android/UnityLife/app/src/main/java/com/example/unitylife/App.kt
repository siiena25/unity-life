package com.example.unitylife

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import com.example.unitylife.config.DeviceInfo
import com.example.unitylife.di.AppComponent
import com.example.unitylife.di.AppModule
import com.example.unitylife.di.DaggerAppComponent
import java.util.*

private lateinit var INSTANCE: Application
private lateinit var dependencyProvider: AppComponent

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        init()
    }

    private fun init() {
        dependencyProvider = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        initDeviceMetadata()
    }

    fun getAppComponent(): AppComponent {
        return dependencyProvider
    }

    private fun initDeviceMetadata() {
        val osType = "Android"
        val osVersion = Build.VERSION.SDK_INT.toString()
        val appVersion: String? = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown-01"
        }
        val manufacturer = Build.MANUFACTURER
        val modelDevice = Build.MODEL
        DeviceInfo.getInstance()
            .setAppVersion(appVersion)
            .setOsType(osType)
            .setManufacturer(manufacturer)
            .setDeviceModel(modelDevice)
            .setOsVersion(osVersion).deviceId = generateDeviceId()
    }

    private fun generateDeviceId(): String {
        val deviceID = UUID.randomUUID()
        return deviceID.toString()
    }

    companion object {
        @JvmStatic
        fun getInstance(): Application {
            return INSTANCE
        }
    }
}