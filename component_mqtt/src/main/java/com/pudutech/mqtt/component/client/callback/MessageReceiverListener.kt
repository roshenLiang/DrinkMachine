package com.pudutech.mqtt.component.client.callback

/**
 * Created by ChenS on 2020/5/15.
 * chenshichao@outlook.com
 */
interface MessageReceiverListener {

    fun onReceiverMessage(topic: String, msg: String)
}