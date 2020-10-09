package com.pudutech.mqtt.component.client.netty.handler

import com.pudutech.mqtt.component.client.netty.NettyMqttClient
import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttMessage

/**
 * Created by ChenS on 2020/5/13.
 * chenshichao@outlook.com
 */
@Suppress("UNCHECKED_CAST")
abstract class AbstractMessageHandler<T : MqttMessage>() : IMessageHandler {

    var TAG: String? = null

    init {
        TAG = this.javaClass.simpleName
    }

    override fun execute(channel: Channel, message: MqttMessage, mqttClient: NettyMqttClient) {
        message as T
        action(channel, message, mqttClient)
    }

    abstract fun action(channel: Channel, message: T, mqttClient: NettyMqttClient)
}