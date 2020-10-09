package com.pudutech.mqtt.component.client.bean

import com.pudutech.mqtt.component.client.config.Qos

/**
 * Created by ChenS on 2020/5/14.
 * chenshichao@outlook.com
 */
data class SubscribeMessage(var topic: String?, var qos: Qos) {

    override fun toString(): String {
        return "SubscribeMessage(topic='$topic', qos=$qos)"
    }
}