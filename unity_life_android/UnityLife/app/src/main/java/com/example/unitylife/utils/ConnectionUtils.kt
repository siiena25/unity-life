package com.llc.aceplace_ru.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.util.Log

class ConnectionUtils {
    companion object {
        private val TAG = ConnectionUtils::class.simpleName

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null
        private var isConnected: Boolean = true

        @JvmStatic
        fun init(context: Context) {
            this.context = context
            registerNetworkCallback()
        }

        @JvmStatic
        fun unregister() {
            context = null
        }

        private fun registerNetworkCallback() {
            val manager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val builder = NetworkRequest.Builder()
            val request = builder.build()
            manager?.registerNetworkCallback(
                request,
                object : ConnectivityManager.NetworkCallback() {

                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        isConnected = true
                        Log.d(TAG, "onAvailable")
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        super.onLosing(network, maxMsToLive)
                        isConnected = false
                        Log.d(TAG, "onLosing")
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        isConnected = false
                        Log.d(TAG, "onLost")
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        isConnected = false
                        Log.d(TAG, "onUnavailable")
                    }
                })
        }

        fun isConnected(): Boolean = isConnected

        fun isConnectedToInternet(): Boolean {
            val connectivity = context?.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager?
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo as Array?
                if (info != null)
                    for (i in info.indices)
                        if (info[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
            }
            return false
        }
    }
}