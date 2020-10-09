package com.pudutech.mqtt.component.client.utils

/**
 * Created by ChenS on 2020/5/13.
 * chenshichao@outlook.com
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if(i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if(i2 != null) {
                i2
            }else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}