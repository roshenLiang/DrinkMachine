package com.pudutech.mqtt.component.client.interf

import android.content.Context
import com.pudutech.mqtt.component.client.bean.SubscribeMessage
import com.pudutech.mqtt.component.client.callback.LoginStateCallback
import com.pudutech.mqtt.component.client.callback.MessageReceiverListener
import com.pudutech.mqtt.component.client.callback.SubscribeStateCallback
import com.pudutech.mqtt.component.client.callback.UnsubscribeStateCallback
import com.pudutech.mqtt.component.client.config.MqttParamOptions
import com.pudutech.mqtt.component.client.config.Qos
import java.util.*

/**
 * Created by ChenS on 2020/5/12.
 * chenshichao@outlook.com
 */
interface IMqttClient {

    fun init(context: Context, paramsOptions: MqttParamOptions?): IMqttClient

    fun connect()

    fun reconnect(isFirstConnect: Boolean)

    fun close()

    fun release()

    fun subscribe(vararg subscribeMessages: SubscribeMessage?, subscribeStateCallback: SubscribeStateCallback?)

    fun unsubscribe(vararg topics: String?, unsubscribeStateCallback: UnsubscribeStateCallback?)

    fun publishMsg(topic: String?, payload: String?)

    fun publishMsg(topic: String?, qos: Qos, payload: String?)
}