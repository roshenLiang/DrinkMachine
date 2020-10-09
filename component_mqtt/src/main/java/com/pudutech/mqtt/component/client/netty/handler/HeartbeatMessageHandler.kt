package com.pudutech.mqtt.component.client.netty.handler

import android.util.Log
import com.pudutech.mqtt.component.client.netty.NettyMqttClient
import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttMessage

/**
 * Created by ChenS on 2020/5/13.
 * chenshichao@outlook.com
 */
class HeartbeatMessageHandler : AbstractMessageHandler<MqttMessage>() {

    override fun action(channel: Channel, message: MqttMessage, mqttClient: NettyMqttClient) {
        Log.d(TAG, "收到心跳包响应：channel = $channel, message = $message")
        if(message.decoderResult().isFailure) {
            Log.w(TAG, "心跳包响应消息解析失败")
            return
        }
    }
}