package com.pudutech.mqtt.component.client.netty.handler

import com.pudutech.mqtt.component.client.netty.NettyMqttClient
import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttMessage

/**
 * Created by ChenS on 2020/5/13.
 * chenshichao@outlook.com
 */
interface IMessageHandler {

    fun execute(channel: Channel, message: MqttMessage, mqttClient: NettyMqttClient)
}