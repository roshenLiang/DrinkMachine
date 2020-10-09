package com.pudutech.mqtt.component.client.netty.handler

import android.util.Log
import com.pudutech.mqtt.component.client.config.LoginState
import com.pudutech.mqtt.component.client.netty.NettyMqttClient
import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttConnAckMessage
import io.netty.handler.codec.mqtt.MqttConnectReturnCode

/**
 * Created by ChenS on 2020/5/13.
 * chenshichao@outlook.com
 */
class ConnAckMessageHandler : AbstractMessageHandler<MqttConnAckMessage>() {

    override fun action(channel: Channel, message: MqttConnAckMessage, mqttClient: NettyMqttClient) {
        Log.d(TAG, "收到连接确认消息：channel = $channel, message = $message")
        if(message.decoderResult().isFailure) {
            Log.w(TAG, "连接确认消息解析失败")
            mqttClient.loginStateCallback?.callbackLoginState(LoginState.LoginFailure)
            return
        }

        val variableHeader = message.variableHeader()
        var errMsg: String? = null
        when(variableHeader.connectReturnCode()) {
            MqttConnectReturnCode.CONNECTION_ACCEPTED -> {
                Log.d(TAG, "登录成功")
                mqttClient.addHeartbeatHandler()
                mqttClient.loginStateCallback?.callbackLoginState(LoginState.LoginSuccessful)
                return
            }

            MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD -> {
                Log.w(TAG, "登录失败：用户名或密码错误")
                errMsg = "登录失败：用户名或密码错误"
                mqttClient.setAllowAutoReconnect(false)
            }

            MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED -> {
                Log.w(TAG, "登录失败：ClientId不允许连接")
                errMsg = "登录失败：ClientId不允许连接"
                mqttClient.setAllowAutoReconnect(false)
            }

            MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE -> {
                Log.w(TAG, "登录失败：服务不可用")
                errMsg = "登录失败：服务不可用"
            }

            MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED -> {
                Log.w(TAG, "登录失败：未授权登录")
                errMsg = "登录失败：未授权登录"
                mqttClient.setAllowAutoReconnect(false)
            }

            else -> {
                Log.w(TAG, "登录失败：未知错误")
                errMsg = "登录失败：未知错误"
            }
        }

        mqttClient.loginStateCallback?.callbackLoginState(LoginState.LoginFailure, errMsg)
        channel.close()
    }
}