package com.pudutech.mqtt.component.client.netty.handler

import android.util.Log
import com.pudutech.mqtt.component.client.config.SubscribeState
import com.pudutech.mqtt.component.client.netty.NettyMqttClient
import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttSubAckMessage

/**
 * Created by ChenS on 2020/5/14.
 * chenshichao@outlook.com
 */
@Suppress("UNCHECKED_CAST")
class SubAckMessageHandler : AbstractMessageHandler<MqttSubAckMessage>() {

    override fun action(channel: Channel, message: MqttSubAckMessage, mqttClient: NettyMqttClient) {
        Log.d(TAG, "收到订阅主题确认消息：channel = $channel, message = $message")
        if(message.decoderResult().isFailure) {
            Log.w(TAG, "主题订阅失败：消息解码异常")
            mqttClient.subscribeStateCallback?.callbackSubscribeState(SubscribeState.SubscribeFailure)
            return
        }

        Log.w(TAG, "主题订阅成功")
        val topicList: ArrayList<String> = mqttClient.currentSubTopics?.clone() as ArrayList<String>
        mqttClient.currentSubTopics?.clear()
        mqttClient.subscribeStateCallback?.callbackSubscribeState(SubscribeState.SubscribeSuccessful, topicList)
    }
}