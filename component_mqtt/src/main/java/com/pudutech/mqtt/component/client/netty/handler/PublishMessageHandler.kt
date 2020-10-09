package com.pudutech.mqtt.component.client.netty.handler

import android.util.Log
import com.pudutech.mqtt.component.client.netty.MessageConverter
import com.pudutech.mqtt.component.client.netty.NettyMqttClient
import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttPublishMessage

/**
 * Created by ChenS on 2020/5/15.
 * chenshichao@outlook.com
 */
class PublishMessageHandler : AbstractMessageHandler<MqttPublishMessage>() {

    override fun action(channel: Channel, message: MqttPublishMessage, mqttClient: NettyMqttClient) {
        Log.d(TAG, "收到服务端发送过来的消息：channel = $channel, message = $message")
        if(message.decoderResult().isFailure) {
            Log.w(TAG, "服务端发送过来的消息解析失败")
            return
        }

        val payload = message.payload()
        if(payload == null) {
            Log.w(TAG, "payload is null.")
            return
        }

        val msg = MessageConverter.byteBufToString(payload)
        if(msg.isNullOrEmpty()) {
            Log.w(TAG, "msg is null or empty.")
            return
        }

        val topic = message.variableHeader().topicName()
        mqttClient.messageReceiverListener?.onReceiverMessage(topic, msg)
    }
}