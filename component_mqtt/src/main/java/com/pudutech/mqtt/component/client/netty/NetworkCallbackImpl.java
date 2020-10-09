package com.pudutech.mqtt.component.client.netty;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

/**
 * Created by ChenS on 2020/4/17.
 * chenshichao@outlook.com
 */
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

    @Override
    public void onAvailable(@NonNull Network network) {
        NetworkManager.getInstance().onNetworkAvailable();
    }

    @Override
    public void onLost(@NonNull Network network) {
        NetworkManager.getInstance().onNetworkUnavailable();
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                NetworkManager.getInstance().updateNetworkType(NetworkManager.NetworkType.Wifi);
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                NetworkManager.getInstance().updateNetworkType(NetworkManager.NetworkType.Cellular);
            } else {
                NetworkManager.getInstance().updateNetworkType(NetworkManager.NetworkType.Other);
            }
        }
    }
}
