package com.pudutech.mqtt.component.client.netty

import android.util.Log
import com.pudutech.mqtt.component.client.config.ConnectState
import com.pudutech.mqtt.component.client.netty.handler.MessageHandlerFactory
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.mqtt.MqttMessage

/**
 * Created by ChenS on 2020/5/12.
 * chenshichao@outlook.com
 */
class NettyMqttReadHandler(private val mqttClient: NettyMqttClient) : ChannelInboundHandlerAdapter() {

    companion object {
        private const val TAG = "NettyMqttReadHandler"
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        Log.d(TAG, "channelActive() ctx = $ctx")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        Log.d(TAG, "channelInactive() ctx = $ctx")
        val chl = ctx.channel()
        chl?.apply {
            close()
        }
        ctx.close()

        mqttClient.callbackConnectState(ConnectState.CONNECT_FAILED)
        // 触发重连
        mqttClient.reconnect(false)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        Log.d(TAG, "channelRead() ctx = $ctx, msg = $msg")
        if(msg !is MqttMessage) {
            Log.w(TAG, "channelRead() msg !instanceof MqttMessage, passed.")
            return
        }

        try {
            val fixedHeader = msg.fixedHeader()
            if(fixedHeader == null) {
                Log.w(TAG, "channedRead() fixedHeader is null, passed.")
                return
            }

            val messageType = fixedHeader.messageType()
            val messageHandler = MessageHandlerFactory.getMessageHandler(messageType)
            if(messageHandler == null) {
                Log.w(TAG, "channelRead() messageHandler is null, passed.")
                return
            }
            Log.d(TAG, "Found messageHandler, class = ${messageHandler.javaClass.simpleName}")
            messageHandler.execute(ctx.channel(), msg, mqttClient)
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        Log.d(TAG, "channelReadComplete() ctx = $ctx")
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        Log.d(TAG, "exceptionCaught() ctx = $ctx, cause = $cause")
        val chl = ctx.channel()
        chl?.apply {
            close()
        }
        ctx.close()

        mqttClient.callbackConnectState(ConnectState.CONNECT_FAILED)
        // 触发重连
        mqttClient.reconnect(false)
    }
}