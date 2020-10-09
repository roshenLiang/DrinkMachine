package com.pudutech.mqtt.component.client.netty

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.util.Log
import com.pudutech.mqtt.component.client.bean.SubscribeMessage
import com.pudutech.mqtt.component.client.callback.LoginStateCallback
import com.pudutech.mqtt.component.client.callback.MessageReceiverListener
import com.pudutech.mqtt.component.client.callback.SubscribeStateCallback
import com.pudutech.mqtt.component.client.callback.UnsubscribeStateCallback
import com.pudutech.mqtt.component.client.config.CConfig
import com.pudutech.mqtt.component.client.config.ConnectState
import com.pudutech.mqtt.component.client.config.MqttParamOptions
import com.pudutech.mqtt.component.client.config.Qos
import com.pudutech.mqtt.component.client.interf.IMqttClient
import com.pudutech.mqtt.component.client.utils.*
import com.pudutech.mqtt.component.client.utils.UUID
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.mqtt.*
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.timeout.IdleStateHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * Created by ChenS on 2020/5/12.
 * chenshichao@outlook.com
 */
class NettyMqttClient private constructor() : IMqttClient {

    companion object {
        private const val TAG = "NettyMqttClient"
//        val INSTANCE: NettyMqttClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//            NettyMqttClient()
//        }

        val INSTANCE = NettyMqttClient()
    }

    private lateinit var context: Context
    private var bootstrap: Bootstrap? = null
    private var channel: Channel? = null

    @Volatile
    private var isClosed = true

    @Volatile
    private var isReconnecting = false
    private var addrs: Vector<String>? = null
    private var hasUserName: Boolean = false
    private var hasPassword: Boolean = false
    private var userName: String? = null
    private var password: String? = null
    var executor: ExecutorServiceFactory? = null
    private var connectState: ConnectState = ConnectState.UNCONNECTED

    private var isNetworkAvailable = false
    private lateinit var certificateManager: CertificateManager

    private var allowAutoReconnect = true// 是否允许自动重连
    var loginStateCallback: LoginStateCallback? = null
    var subscribeStateCallback: SubscribeStateCallback? = null
    var unsubscribeStateCallback: UnsubscribeStateCallback? = null
    var messageReceiverListener: MessageReceiverListener? = null
    var currentSubTopics: ArrayList<String>? = null
    var currentUnsubTopics: ArrayList<String>? = null

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun init(
        context: Context,
        paramsOptions: MqttParamOptions?
    ): IMqttClient {
        Log.d(TAG, "init() paramsOptions = $paramsOptions")
        this.context = context

        paramsOptions?.let {
            this.hasUserName = it.isHasUserName
            this.hasPassword = it.isHasPassword
            this.userName = it.userName
            this.password = it.password
            this.addrs = it.addrs
            this.loginStateCallback = it.loginStateCallback
            this.messageReceiverListener = it.messageReceiverListener
        }
        executor = ExecutorServiceFactory()
        executor?.initBossLoopGroup()// 初始化重连线程组
        certificateManager = CertificateManager.getInstance(context)
        isNetworkAvailable = SystemTool.isNetworkAvailable(context)
        GlobalScope.launch {
            delay(2000)
            registerNetworkCallback()
        }

        return this
    }

    override fun connect() {
        Log.d(TAG, "connect()")
        isClosed = false
        this.reconnect(true)
    }

    override fun reconnect(isFirstConnect: Boolean) {
        Log.d(
            TAG,
            "reconnect() isFirstConnect = $isFirstConnect, threadId = ${Thread.currentThread().id}"
        )
        if (connectState == ConnectState.CONNECTING || connectState == ConnectState.CONNECTED) {
            Log.w(TAG, "No need to reconnect(), current connectState is connecting or connected.")
            return
        }

        if (!allowAutoReconnect) {
            Log.d(TAG, "reconnect() failure, Not allow auto reconnect.")
            callbackConnectState(ConnectState.CONNECT_FAILED)
            return
        }

        if (!isNetworkAvailable) {
            Log.d(TAG, "reconnect() failure, Network is unavailable.")
            callbackConnectState(ConnectState.CONNECT_FAILED)
            return
        }

        if (!certificateManager.hasCertificateFile()) {
            Log.d(
                TAG,
                "reconnect() failure, Hasn't certificate file, please check assets folder."
            )
            callbackConnectState(ConnectState.CONNECT_FAILED)
            return
        }

        if (!isFirstConnect) {
            try {
                Thread.sleep(CConfig.MQTT_RECONNECT_INTERVAL_TIME.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
                callbackConnectState(ConnectState.CONNECT_FAILED)
            }
        }

        if (!isClosed && !isReconnecting) {
            synchronized(this) {
                if (!isClosed && !isReconnecting) {
                    if (connectState == ConnectState.CONNECTING || connectState == ConnectState.CONNECTED) {
                        return@synchronized
                    }
                    isReconnecting = true
                    executor?.execBossTask(ReconnectTask())
                }
            }
        }
    }

    override fun close() {
        Log.d(TAG, "The netty mqtt client is stopping.")
        if (isClosed) {
            return
        }

        try {
            sendMsg(
                MqttMessage(
                    MqttFixedHeader(
                        MqttMessageType.DISCONNECT,
                        false,
                        MqttQoS.AT_MOST_ONCE,
                        false,
                        0x02
                    )
                )
            )
            closeChannel()
            closeBootstrap()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isClosed = true
            callbackConnectState(ConnectState.UNCONNECTED)
            Log.d(TAG, "The netty mqtt client is stopped.")
        }
    }

    override fun release() {
        unregisterNetworkCallback()
        executor?.destroy()
        close()
    }

    /**
     * 订阅主题
     */
    override fun subscribe(
        vararg subscribeMessages: SubscribeMessage?,
        subscribeStateCallback: SubscribeStateCallback?
    ) {
        Log.d(TAG, "subscribe() subscribeMessages = $subscribeMessages")
        this.subscribeStateCallback = subscribeStateCallback
        if (subscribeMessages.isNullOrEmpty()) {
            Log.w(TAG, "subscribe() failure, reason: subscribeMessage is null or empty.")
            return
        }

        if (currentSubTopics == null) {
            currentSubTopics = arrayListOf()
        }
        currentSubTopics?.clear()
        var subscriptionList: LinkedList<MqttTopicSubscription>? = null
        for (subscribeMessage in subscribeMessages) {
            if (subscribeMessage?.topic.isNullOrEmpty()) {
                Log.w(TAG, "subscribe topic failure, reason: Topic is null or empty.")
                continue
            }
            val topic = subscribeMessage?.topic!!
            val qos = subscribeMessage.qos
            val subscription = MqttTopicSubscription(topic, MqttQoS.valueOf(qos.level))
            if (subscriptionList.isNullOrEmpty()) {
                subscriptionList = LinkedList()
            }
            subscriptionList.add(subscription)
            currentSubTopics?.add(topic)
        }

        if (subscriptionList.isNullOrEmpty()) {
            Log.w(TAG, "subscribe() failure, reason: subscriptionList is null or empty.")
            return
        }

        val fixedHeader =
            MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0)
        val variableHeader =
            MqttMessageIdVariableHeader.from(MessageIdGenerate.getInstance().messageId)
        val payload = MqttSubscribePayload(subscriptionList)
        sendMsg(MqttSubscribeMessage(fixedHeader, variableHeader, payload))
    }

    /**
     * 取消订阅主题
     */
    override fun unsubscribe(
        vararg topics: String?,
        unsubscribeStateCallback: UnsubscribeStateCallback?
    ) {
        Log.d(TAG, "unsubscribe() topics = $topics")
        this.unsubscribeStateCallback = unsubscribeStateCallback
        if (topics.isNullOrEmpty()) {
            Log.w(TAG, "unsubscribe() failure, reason: topics is null or empty.")
            return
        }

        if (currentUnsubTopics == null) {
            currentUnsubTopics = arrayListOf()
        }

        currentUnsubTopics?.apply {
            clear()
            topics.iterator().forEach { topic ->
                topic?.let {
                    add(it)
                }
            }
        }

        val fixedHeader =
            MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0x02)
        val variableHeader =
            MqttMessageIdVariableHeader.from(MessageIdGenerate.getInstance().messageId)
        val payload = MqttUnsubscribePayload(topics.toList())
        sendMsg(MqttUnsubscribeMessage(fixedHeader, variableHeader, payload))
    }

    /**
     * 发布消息
     */
    override fun publishMsg(topic: String?, payload: String?) {
        this.publishMsg(topic, Qos.AT_MOST_ONCE, payload)
    }

    /**
     * 发布消息
     */
    override fun publishMsg(topic: String?, qos: Qos, payload: String?) {
        Log.d(TAG, "publishMsg() topic = $topic, qos = $qos, payload = $payload")
        if (topic.isNullOrEmpty()) {
            Log.w(TAG, "publishMsg() failure, reason: topic is null or empty.")
            return
        }

        if (payload.isNullOrEmpty()) {
            Log.w(TAG, "publishMsg() failure, reason: payload is null or empty.")
            return
        }

        val publishMessage = MqttMessageFactory.newMessage(
            MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(qos.level), false, 0),
            MqttPublishVariableHeader(topic, MessageIdGenerate.getInstance().messageId),
            Unpooled.buffer().writeBytes(payload.toByteArray())
        )

        sendMsg(publishMessage)
    }

    /**
     * 标识网络是否可用
     */
    fun setNetworkAvailable(available: Boolean) {
        this.isNetworkAvailable = available
        if (isNetworkAvailable) {
            reconnect(false)
        }
    }

    private fun initBootstrap() {
        close()
        Log.d(TAG, "initBootstrap()")
        try {
            isClosed = false
            val loopGroup = NioEventLoopGroup()
            bootstrap = Bootstrap()
            bootstrap?.apply {
                group(loopGroup).channel(NioSocketChannel::class.java)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CConfig.MQTT_CONNECT_TIMEOUT)
                    .handler(LoggingHandler(LogLevel.INFO))
                    .handler(NettyMqttChannelInitializer(this@NettyMqttClient))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun closeChannel() {
        Log.d(TAG, "closeChannel()")
        try {
            channel?.close()
            channel?.eventLoop()?.shutdownGracefully()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            channel = null
        }
    }

    private fun closeBootstrap() {
        Log.d(TAG, "closeBootstrap()")
        try {
            bootstrap?.group()?.shutdownGracefully()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bootstrap = null
        }
    }

    fun callbackConnectState(state: ConnectState) {
        Log.d(TAG, "callbackConnectState() state = $state")
        this.connectState = state
        when (state) {
            ConnectState.UNCONNECTED -> {

            }

            ConnectState.CONNECTING -> {

            }

            ConnectState.CONNECTED -> {
                val fixedHeader =
                    MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 10)
                val mqttVersion = MqttVersion.MQTT_3_1_1
                val variableHeader = MqttConnectVariableHeader(
                    mqttVersion.protocolName(),
                    mqttVersion.protocolLevel().toInt(),
                    hasUserName,
                    hasPassword,
                    false,
                    0,
                    false,
                    false,
                    60
                )
                val payload = MqttConnectPayload(
                    generateClientId(),
                    null,
                    null,
                    userName,
                    password?.toByteArray()
                )
                val mqttConnectMessage = MqttConnectMessage(fixedHeader, variableHeader, payload)
                Log.d(TAG, "mqttConnectMessage = $mqttConnectMessage")
                channel?.writeAndFlush(mqttConnectMessage)
            }

            ConnectState.CONNECT_FAILED -> {

            }
        }
    }

    /**
     * cc4b7333fc26_e00CPJ4g
     */
    private fun generateClientId(): String {
        val mac = SystemTool.getMac(context).replace(":", "").toLowerCase()
        val uuid = UUID.generateShortUUID()
        val clientId = mac.plus("_").plus(uuid)
        Log.d(TAG, "generateClientId() clientId = $clientId")
        return clientId
    }

    private fun sendMsg(mqttMessage: MqttMessage?) {
        Log.d(TAG, "sendMsg() mqttMessage = $mqttMessage")
        if (mqttMessage == null) {
            Log.w(TAG, "sendMsg() failure, reason: MqttMessage is null.")
            return
        }

        if (channel == null) {
            Log.w(TAG, "sendMsg() failure, reason: channel is null.")
            return
        }

        channel?.apply {
            if (!isActive) {
                Log.w(TAG, "sendMsg() failure, reason: channel is inactive.")
                return@apply
            }

            writeAndFlush(mqttMessage)
            Log.d(TAG, "sendMsg successful")
        }
    }

    fun setAllowAutoReconnect(allowAutoReconnect: Boolean) {
        this.allowAutoReconnect = allowAutoReconnect
    }

    fun addHeartbeatHandler() {
        if (channel == null) {
            return
        }

        channel?.apply {
            if (!isActive || pipeline() == null) {
                return@apply
            }

            try {
                // 之前存在的读写超时handler，先移除掉，再重新添加
                if (pipeline().get(IdleStateHandler::class.simpleName) != null) {
                    pipeline().remove(IdleStateHandler::class.simpleName)
                }

                pipeline().addFirst(
                    IdleStateHandler::class.simpleName, IdleStateHandler(
                        CConfig.MQTT_HEARTBEAT_INTERVAL_TIME.toLong() * 3,
                        CConfig.MQTT_HEARTBEAT_INTERVAL_TIME.toLong(),
                        0,
                        TimeUnit.MILLISECONDS
                    )
                )

                if (pipeline().get(HeartbeatHandler::class.simpleName) != null) {
                    pipeline().remove(HeartbeatHandler::class.simpleName)
                }
                pipeline().addAfter(
                    IdleStateHandler::class.simpleName,
                    HeartbeatHandler::class.simpleName,
                    HeartbeatHandler(this@NettyMqttClient)
                )
                Log.d(TAG, "添加心跳管理handler成功")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private inner class ReconnectTask : Runnable {

        override fun run() {
            try {
                // 重连是，释放工作线程组，也就是停止心跳
                executor?.destroyWorkLoopGroup()
                while (!isClosed) {
                    if (!isNetworkAvailable) {
                        Log.d(
                            TAG,
                            "The netty mqtt client connect failure, Network is unavailable."
                        )
                        break
                    }

                    if (reconnect() == ConnectState.CONNECTED) {
                        Log.d(TAG, "The netty mqtt client connected.")
                        callbackConnectState(ConnectState.CONNECTED)
                        return
                    }
                }

                callbackConnectState(ConnectState.CONNECT_FAILED)
            } finally {
                isReconnecting = false
            }
        }

        private fun reconnect(): ConnectState {
            initBootstrap()
            return connectServer()
        }

        private fun connectServer(): ConnectState {
            if (addrs.isNullOrEmpty()) {
                Log.w(TAG, "connectServer() failure, addrs is null or empty.")
                return ConnectState.CONNECT_FAILED
            }

            for (i in addrs?.indices!!) {
                if (isClosed) {
                    Log.w(TAG, "connectServer() failure, The netty mqtt client is closed.")
                    return ConnectState.CONNECT_FAILED
                }

                if (!isNetworkAvailable) {
                    Log.w(TAG, "connectServer() failure, Network is unavailable.")
                    return ConnectState.CONNECT_FAILED
                }

                val addr = addrs!![i].split(" ")
                if (addr.size < 2) {
                    Log.w(TAG, "connectServer() failure, addr wrongful.")
                    continue
                }

                for (j in 0 until CConfig.MQTT_RECONNECT_COUNT) {
                    try {
                        val host = addr[0]
                        val port = Integer.parseInt(addr[1])
                        realConnectServer(host, port)

                        if (channel != null && channel?.isActive!!) {
                            Log.d(TAG, "connectServer() successfully.")
                            return ConnectState.CONNECTED
                        } else {
                            Log.w(
                                TAG,
                                "connectServer() failure, wait ${(j + 1) * CConfig.MQTT_RECONNECT_INTERVAL_TIME} milliseconds to reconnect."
                            )
                            Thread.sleep((j + 1) * CConfig.MQTT_RECONNECT_INTERVAL_TIME.toLong())
                        }
                    } catch (e: InterruptedException) {
                        close()
                        break// 线程被中断，强制关闭
                    }
                }
            }

            // 执行到这里，代表连接失败
            return ConnectState.CONNECT_FAILED
        }

        /**
         * 真正连接服务端的地方
         */
        private fun realConnectServer(host: String?, port: Int) {
            channel = try {
                bootstrap?.connect(host, port)?.sync()?.channel()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "realConnectServer() exception, reason = ${e.message}")
                null
            }
        }
    }

    private fun registerNetworkCallback() {
        if (networkCallback == null) {
            networkCallback = NetworkCallbackImpl()
        }
        val request = NetworkRequest.Builder().build()
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) ?: return
        cm as ConnectivityManager
        cm.registerNetworkCallback(request, networkCallback!!)
    }

    private fun unregisterNetworkCallback() {
        if (networkCallback == null) {
            return
        }
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) ?: return
        cm as ConnectivityManager
        cm.unregisterNetworkCallback(networkCallback!!)
    }
}