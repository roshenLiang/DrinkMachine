package com.pudutech.mqtt.component.client.netty;

import android.util.Log;

/**
 * Created by ChenS on 2020/4/17.
 * chenshichao@outlook.com
 */
class NetworkManager {

    public enum NetworkType {
        Wifi,
        Cellular,
        Other
    }

    private static final String TAG = NetworkManager.class.getSimpleName();

    private NetworkManager() {

    }

    private static class SingletonHolder {
        private static final NetworkManager INSTANCE = new NetworkManager();
    }

    static NetworkManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private NetworkType networkType;

    void onNetworkAvailable() {
        Log.d(TAG, "onNetworkAvailable()");
        NettyMqttClient.Companion.getINSTANCE().setNetworkAvailable(true);
    }

    void onNetworkUnavailable() {
        Log.d(TAG, "onNetworkUnavailable()");
        NettyMqttClient.Companion.getINSTANCE().setNetworkAvailable(false);
    }

    void updateNetworkType(NetworkType networkType) {
        if(this.networkType != networkType) {
            Log.d(TAG, String.format("NetworkType was updated. last networkType[%s], current networkType[%s]", this.networkType, networkType));
            this.networkType = networkType;
        }
    }
}
