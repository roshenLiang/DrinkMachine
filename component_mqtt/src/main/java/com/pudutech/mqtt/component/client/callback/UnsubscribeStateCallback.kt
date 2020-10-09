package com.pudutech.mqtt.component.client.callback

import com.pudutech.mqtt.component.client.config.UnsubscribeState

/**
 * Created by ChenS on 2020/5/15.
 * chenshichao@outlook.com
 */
interface UnsubscribeStateCallback {

    fun callbackUnsubscribeState(state: UnsubscribeState, topicList: List<String>? = null)
}