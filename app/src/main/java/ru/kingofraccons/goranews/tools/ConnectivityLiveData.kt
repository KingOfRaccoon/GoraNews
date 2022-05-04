package ru.kingofraccons.goranews.tools

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData

// livedata for get state network
class ConnectivityLiveData(context: Context) : LiveData<NetworkState>() {

    private var connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        notifyNetworkStatus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            connectivityManager.registerDefaultNetworkCallback(
                getConnectivityManagerCallback()
            )
        // for devices below Nougat
        else
            nougatNetworkAvailableRequest()
    }

    override fun onInactive() {
        // When all observers are gone, remove connectivity callback or un register the receiver.
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun nougatNetworkAvailableRequest() {
        val builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        connectivityManager.registerNetworkCallback(
            builder.build(),
            getConnectivityManagerCallback()
        )
    }

    private fun getConnectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                postValue(NetworkState.CONNECTED)
            }

            override fun onLost(network: Network) {
                postValue(NetworkState.DISCONNECTED)
            }
        }
        return networkCallback
    }

    private fun notifyNetworkStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities)
            if (actNw != null) {
                if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
                    postValue(NetworkState.CONNECTED)
                else
                    postValue(NetworkState.DISCONNECTED)
            } else
                postValue(NetworkState.DISCONNECTED)
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    when (type) {
                        ConnectivityManager.TYPE_WIFI -> postValue(NetworkState.CONNECTED)
                        ConnectivityManager.TYPE_MOBILE -> postValue(NetworkState.CONNECTED)
                        ConnectivityManager.TYPE_ETHERNET -> postValue(NetworkState.CONNECTED)
                        else -> postValue(NetworkState.DISCONNECTED)
                    }
                }
            }
        }
    }
}