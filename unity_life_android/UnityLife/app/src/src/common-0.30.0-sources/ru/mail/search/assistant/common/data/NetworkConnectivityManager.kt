package ru.mail.search.assistant.common.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.flow.*
import ru.mail.search.assistant.common.ui.getSystemService
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.Tag
import java.util.concurrent.atomic.AtomicInteger

class NetworkConnectivityManager(
    private val context: Context,
    private val logger: Logger?
) : NetworkConnection {

    private val networkCallback = NetworkCallback()
    private val internetAvailability = MutableStateFlow(true)
    private val counter = AtomicInteger(0)

    override fun hasNetworkAvailability(): Boolean {
        val connectivityManager = getConnectivityManager() ?: return true
        return hasNetworkAvailability(connectivityManager)
    }

    override fun observeNetworkAvailability(): Flow<Boolean> {
        val connectivityManager = getConnectivityManager()
        return internetAvailability.asStateFlow()
            .onStart { connectivityManager?.let(::onStartSubscription) }
            .onCompletion { connectivityManager?.let(::onFinishSubscription) }
            .distinctUntilChanged()
    }

    private fun getConnectivityManager(): ConnectivityManager? {
        val service = context.getSystemService<ConnectivityManager>()
        if (service == null) {
            logger?.e(
                Tag.NETWORK_STATUS,
                NullPointerException("Failed to retrieve ConnectivityManager")
            )
        }
        return service
    }

    private fun hasNetworkAvailability(connectivityManager: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    private fun onStartSubscription(connectivityManager: ConnectivityManager) {
        if (counter.getAndIncrement() == 0) {
            internetAvailability.tryEmit(hasNetworkAvailability(connectivityManager))
            registerBroadcastReceiver(connectivityManager)
        }
    }

    private fun onFinishSubscription(connectivityManager: ConnectivityManager) {
        if (counter.decrementAndGet() == 0) {
            unregisterBroadcastReceiver(connectivityManager)
        }
    }

    private fun registerBroadcastReceiver(connectivityManager: ConnectivityManager) {
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    private fun unregisterBroadcastReceiver(connectivityManager: ConnectivityManager) {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private inner class NetworkCallback : ConnectivityManager.NetworkCallback() {

        private val set: MutableSet<Network> = HashSet()

        override fun onAvailable(network: Network) {
            set.add(network)
            internetAvailability.tryEmit(true)
        }

        override fun onLost(network: Network) {
            set.remove(network)
            internetAvailability.tryEmit(set.isNotEmpty())
        }
    }
}