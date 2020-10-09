package com.uroica.drinkmachine.constant
 
class Constant {

    companion object {
        //热卖机
        const val TOTAL_CHANNEL = 12;
        const val TOTAL_NUM = 7;
        //热卖机
        const val MACHINE_TYPE_HEAT = 0;
        //桶装水
        const val MACHINE_TYPE_WATER = 1;
        //饮料机
        const val MACHINE_TYPE_DRINK = 2;

        //格子柜
        const val MACHINE_TYPE_LATTICE = 3;


                const  val BASE_URL = "https://uroicavending.cn:4430"//正式
//        const  val BASE_URL = "https://payforpark.cn:9983";//旧的
        //MQTT
        const val MQTT_IP = "47.112.160.93";//正式
//        const val MQTT_IP = "193.112.9.99";//旧的测试
        //MQTT
        const val MQTT_PORT = "61613";
        const val MQTT_USER = "admin";
        const val MQTT_PASSWORD = "Cxl26yang22";
        const val MQTT_TOPIC = "/UROICA/SaleBox/";
        const val MQTT_CTRL = "/ctrl";
        const val MQTT_REPLY = "/reply";
        //协议部分
        //注册
        const val MQTT_REGISTER = "FAAF010A";
        const val MQTT_END = "BB";
        //心跳
        const val MQTT_HEART = "FAAF0205"
        //出货应答
        const val MQTT_SHIPMENT = "FAAF000D03";
    }
}