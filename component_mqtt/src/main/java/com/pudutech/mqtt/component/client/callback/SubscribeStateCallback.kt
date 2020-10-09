package com.pudutech.mqtt.component.client.callback

import com.pudutech.mqtt.component.client.config.SubscribeState

/**
 * Created by ChenS on 2020/5/15.
 * chenshichao@outlook.com
 */
interface SubscribeStateCallback {

    fun callbackSubscribeState(state: SubscribeState, topicList: List<String>? = null)
}