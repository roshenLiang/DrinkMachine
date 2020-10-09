package com.uroica.drinkmachine.constant
 
class SharePConstant{
    companion object {
        //串口选择设备的index
        const val  PARAM_SERIALPORT_DEVICEINDEX = "serialport_deviceindex"
        const val  PARAM_SERIALPORT_BAUDRATEINDEX = "serialport_baudrateindex"
        const val  PARAM_SERIALPORT_DEVICE = "serialport_device"
        const val  PARAM_SERIALPORT_BAUDRATE= "serialport_baudrate"
        //机器的主副柜数量
        const val  PARAM_MACHINE_CABINET_NUM = "machine_cabinet_num"
        //设备类型  //1饮料机2格子柜3桶装水  index -1
        const val  PARAM_MACHINE_TYPE_INDEX = "machine_type_index"
        const val  TEMP_MODE = "temp_mode"
        const val  TEMP_NUM = "temp_num"
        const val  HEART_TIME = "heart_time"
        //日誌
        const val  LOG_SWITCH = "log_switch"
        const val  FAULT_DOOR = "fault_door"
        const val  FAULT_SHIPMENT = "fault_shipment"
    }
}