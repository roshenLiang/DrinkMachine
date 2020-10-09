package com.pudutech.mqtt.component.client

import com.pudutech.mqtt.component.client.interf.IMqttClient
import com.pudutech.mqtt.component.client.netty.NettyMqttClient

/**
 * Created by ChenS on 2020/5/12.
 * chenshichao@outlook.com
 */
object MqttClientFactory {

    fun getMqttClient(): IMqttClient {
        return NettyMqttClient.INSTANCE
    }
}