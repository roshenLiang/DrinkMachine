package com.pudutech.mqtt.component.client.utils

import android.content.Context
import com.google.gson.Gson

/**
 * Created by ChenS on 2020/5/13.
 * chenshichao@outlook.com
 */
class CertificateManager private constructor(private val context: Context) {

    companion object : SingletonHolder<CertificateManager, Context>(::CertificateManager)
    private var params: LinkedHashMap<String, String>? = null
    private val gson = Gson()

    /**
     * 判断是否包含凭证文件
     */
    fun hasCertificateFile(): Boolean {
        try {
            val fileNames = context.assets.list("")
            if (fileNames.isNullOrEmpty()) {
                return false
            }

            for (i in fileNames.indices) {
                if (fileNames[i] == "certificate") {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun getUserName(): String? {
        if(!hasCertificateFile()) {
            return null
        }

        readFileToParams()
        return params?.get("userName")
    }

    fun getPassword(): String? {
        if(!hasCertificateFile()) {
            return null
        }

        readFileToParams()
        return params?.get("password")
    }

    private fun readFileToParams() {
        if(params != null) {
            return
        }

        try {
            val inputStream = context.assets.open("certificate")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            val result = String(buffer)
            params = gson.fromJson<LinkedHashMap<String, String>>(AES.decrypt(result), LinkedHashMap::class.javaObjectType)
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
}