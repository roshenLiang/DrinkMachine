package com.pudutech.mqtt.component.client.netty

import io.netty.buffer.ByteBuf


/**
 * Created by ChenS on 2020/5/15.
 * chenshichao@outlook.com
 */
object MessageConverter {

    fun byteBufToString(buf: ByteBuf?): String? {
        if(buf == null) {
            return null
        }

        return if (buf.hasArray()) {
            String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes())
        } else {
            val bytes = ByteArray(buf.readableBytes())
            buf.getBytes(buf.readerIndex(), bytes)
            String(bytes, 0, buf.readableBytes())
        }
    }
}