package com.pudutech.mqtt.component.client.netty

import android.util.Log
//import com.pudutech.base.Pdlog
import com.pudutech.mqtt.component.client.config.ConnectState
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.mqtt.*
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

/**
 * Created by ChenS on 2020/5/13.
 * chenshichao@outlook.com
 */
class HeartbeatHandler(private var mqttClient: NettyMqttClient) : ChannelInboundHandlerAdapter() {

    private var heartbeatMessage: MqttMessage = MqttMessageFactory.newMessage(
        MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0),
        null,
        null
    )

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        super.userEventTriggered(ctx, evt)
        if(evt !is IdleStateEvent) {
            return
        }

        when(evt.state()) {
            IdleState.READER_IDLE -> {
                // 规定时间内没收到服务端消息，进行重连操作
                Log.w("HeartbeatHandler", "读超时，触发重连")
                mqttClient.callbackConnectState(ConnectState.CONNECT_FAILED)
                mqttClient.reconnect(false)
            }

            IdleState.WRITER_IDLE -> {
                // 规定时间内没向服务端发送消息，发送一个心跳包
                Log.w("HeartbeatHandler", "写超时，发送心跳包")
                if(heartbeatTask == null) {
                    heartbeatTask = HeartbeatTask(ctx.channel())
                }

                // 发送心跳包
                mqttClient.executor?.execWorkTask(heartbeatTask)
            }
        }
    }

    private var heartbeatTask: HeartbeatTask? = null
    private inner class HeartbeatTask(private val channel: Channel) : Runnable {
        override fun run() {
            if(!channel.isActive) {
                return
            }

            channel.writeAndFlush(heartbeatMessage)
        }
    }
}