package com.pudutech.mqtt.component.client.netty.handler

import io.netty.handler.codec.mqtt.MqttMessageType

/**
 * Created by ChenS on 2020/5/13.
 * chenshichao@outlook.com
 */
object MessageHandlerFactory {

    private val HANDLERS = HashMap<MqttMessageType, IMessageHandler>()

    init {
        HANDLERS[MqttMessageType.CONNACK] = ConnAckMessageHandler()
        HANDLERS[MqttMessageType.PINGRESP] = HeartbeatMessageHandler()
        HANDLERS[MqttMessageType.SUBACK] = SubAckMessageHandler()
        HANDLERS[MqttMessageType.UNSUBACK] = UnsubAckMessageHandler()
        HANDLERS[MqttMessageType.PUBACK] = PubAckMessageHandler()
        HANDLERS[MqttMessageType.PUBLISH] = PublishMessageHandler()
    }

    fun getMessageHandler(messageType: MqttMessageType): IMessageHandler? {
        return HANDLERS[messageType]
    }
}