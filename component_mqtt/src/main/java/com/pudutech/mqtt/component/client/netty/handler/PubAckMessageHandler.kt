package com.pudutech.mqtt.component.client.netty.handler

import android.util.Log
import com.pudutech.mqtt.component.client.netty.NettyMqttClient
import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttPubAckMessage

/**
 * Created by ChenS on 2020/5/15.
 * chenshichao@outlook.com
 */
class PubAckMessageHandler : AbstractMessageHandler<MqttPubAckMessage>() {

    override fun action(channel: Channel, message: MqttPubAckMessage, mqttClient: NettyMqttClient) {
        Log.d(TAG, "收到消息发送响应：channel = $channel, message = $message")
        if(message.decoderResult().isFailure) {
            Log.w(TAG, "消息发送响应解析失败")
            return
        }
    }
}