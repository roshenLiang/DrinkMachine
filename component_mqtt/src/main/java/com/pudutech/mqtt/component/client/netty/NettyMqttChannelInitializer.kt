package com.pudutech.mqtt.component.client.netty

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.mqtt.MqttDecoder
import io.netty.handler.codec.mqtt.MqttEncoder

/**
 * Created by ChenS on 2020/5/12.
 * chenshichao@outlook.com
 */
class NettyMqttChannelInitializer(private val mqttClient: NettyMqttClient) : ChannelInitializer<Channel>() {

    override fun initChannel(ch: Channel) {
        val pipeline = ch.pipeline()
        pipeline.addLast("encoder", MqttEncoder.INSTANCE)
            .addLast("decoder", MqttDecoder())
            .addLast(NettyMqttReadHandler::class.simpleName, NettyMqttReadHandler(mqttClient))
    }
}