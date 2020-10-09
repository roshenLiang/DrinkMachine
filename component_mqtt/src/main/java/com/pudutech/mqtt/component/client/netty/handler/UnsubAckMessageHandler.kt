package com.pudutech.mqtt.component.client.netty.handler

import android.util.Log
import com.pudutech.mqtt.component.client.config.UnsubscribeState
import com.pudutech.mqtt.component.client.netty.NettyMqttClient
import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage

/**
 * Created by ChenS on 2020/5/14.
 * chenshichao@outlook.com
 */
class UnsubAckMessageHandler : AbstractMessageHandler<MqttUnsubAckMessage>() {

    override fun action(channel: Channel, message: MqttUnsubAckMessage, mqttClient: NettyMqttClient) {
        Log.d(TAG, "收到取消订阅主题确认消息：channel = $channel, message = $message")
        if(message.decoderResult().isFailure) {
            Log.w(TAG, "取消主题订阅失败：消息解码异常")
            mqttClient.unsubscribeStateCallback?.callbackUnsubscribeState(UnsubscribeState.UnsubscribeFailure)
            return
        }

        Log.w(TAG, "取消主题订阅成功")
        val topicList: ArrayList<String> = mqttClient.currentUnsubTopics?.clone() as ArrayList<String>
        mqttClient.currentUnsubTopics?.clear()
        mqttClient.unsubscribeStateCallback?.callbackUnsubscribeState(UnsubscribeState.UnsubscribeSuccessful, topicList)
    }
}