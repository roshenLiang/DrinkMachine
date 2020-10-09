package com.pudutech.mqtt.component.client.config

/**
 * Created by ChenS on 2020/5/12.
 * chenshichao@outlook.com
 */
object CConfig {

    const val MQTT_CONNECT_TIMEOUT = 10 * 1000// Mqtt连接超时时间，单位：毫秒
    const val MQTT_RECONNECT_COUNT = 5// 一个周期的重连次数
    const val MQTT_RECONNECT_INTERVAL_TIME = 5 * 1000// 重连间隔时间
    const val MQTT_HEARTBEAT_INTERVAL_TIME = 18 * 1000// 心跳间隔时间
}