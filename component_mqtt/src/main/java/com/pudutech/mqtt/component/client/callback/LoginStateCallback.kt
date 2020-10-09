package com.pudutech.mqtt.component.client.callback

import com.pudutech.mqtt.component.client.config.LoginState

/**
 * Created by ChenS on 2020/5/14.
 * chenshichao@outlook.com
 */
interface LoginStateCallback {

    fun callbackLoginState(state: LoginState, errMsg: String? = null)
}