package com.uroica.drinkmachine.greement

//import com.uroica.machine.bean.rxbus.Bus_LooperBean
//import com.uroica.machine.bean.rxbus.Bus_ShipmentBean
//import com.uroica.machine.bean.rxbus.Bus_TempBean
import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.uroica.drinkmachine.bean.rxbus.*
import com.uroica.drinkmachine.constant.SharePConstant
import com.uroica.drinkmachine.util.ChangeTool
import com.uroica.drinkmachine.util.SharedPreferenceUtil
import com.uroica.drinkmachine.util.crc.CRC16Util
import com.uroica.drinkmachine.util.serialport.OnSerialPortDataListener
import com.uroica.drinkmachine.util.serialport.SerialPortManager
import me.goldze.mvvmhabit.bus.RxBus
import java.util.*

/*
 */
class AgreementManager private constructor() {
    // 当前轮询的主板
    var curMainBoardIndex: Int = 1


//    //主板数量
//    var maxMachine: Int = 1

    //延迟发送
    var delaySend: Long =0
    var mHandler: Handler = Handler();

    var tempReceiveData: String = "";
//    var tempSendData: String = "";
//    var isReceiveRunShipment :Boolean =true;//判断是否接受发送出货 防止机器漏收现象

    var isReceiveTemp: Boolean = true;//判断是否接受 防止机器漏收现象
    var isReceiveRunShipment: Boolean = true;//判断是否接受 防止机器漏收现象
    var isReceiveACK: Boolean = true;//判断是否接受 防止机器漏收现象
    var isReceiveWB: Boolean = true;//判断是否接受 防止机器漏收现象

    var againRunSetTemp: ByteArray = byteArrayOf(6)
    var againRunDrinkShipment:ByteArray= byteArrayOf(9)
    var againRunACK: ByteArray = byteArrayOf(4)
    var againRunWB: ByteArray = byteArrayOf(4)

    //串口管理类
    private var mSerialPortManager: SerialPortManager? = null

    /*
            单例模式
         */
    companion object {
        //https://www.jianshu.com/p/5797b3d0ebd0
        val instance: AgreementManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AgreementManager()
        }
    }

//
//    /*
//           设置机器主副柜数量
//        */
//    fun putMachineBoardNum(type: Int) {
//        maxMachine = type;
//    }


    /**
     *目前 热卖机和饮料机一样
     *
     */
    fun checkCabinet(mainBoard: Int) {
//        Log.i("串口内容","检测loop状态")
        curMainBoardIndex = mainBoard;
        //轮询协议
        var drinkLooperAgreement = byteArrayOf(0x01, 0x03)
        drinkLooperAgreement[0] = mainBoard.toByte();
        var result = CRC16Util.calcCrc16ToBytes(drinkLooperAgreement, true)
        val data: String = ChangeTool.ByteArrToHex(result)
        //记得前提要打开串口
        mSerialPortManager?.sendBytes(result)
    }

    /*
        轮询查询
        根据主板去轮询查询
     */
     var timer: Timer= Timer();
    fun startLoopCheckCabinet() {
        Log.i("串口内容", "开启全部轮询")
        curMainBoardIndex = 1;
        //开启200ms轮询
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                checkCabinet(curMainBoardIndex)
//                curMainBoardIndex++;
//                if (curMainBoardIndex > maxMachine) {
//                    curMainBoardIndex = 1;
//                }
            }
        }, 200, 200)
    }
    fun startLoopCheckCabinet( mainBoard: Int) {
        stopLoopCheckCabinet();
        Log.i("串口内容", "开启单轮询")
        curMainBoardIndex = mainBoard;
        //开启200ms轮询
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                checkCabinet(curMainBoardIndex)
            }
        }, 200, 200)
    }

    fun stopLoopCheckCabinet() {
        timer?.cancel()
        LogUtils.file("串口内容", "停止轮询");
        Log.i("串口内容", "停止轮询")
    }

    fun sstartLoopCheckCabinet() {
        timer?.cancel()
        startLoopCheckCabinet()
        Log.i("串口内容", "停止并 开启轮询")
    }


    /***
     * 设置温度
     * mainBoard 主板
     * mode 温度模式
     * tempNum 温度值
     * 目前 热卖机和饮料机一样
     */
    fun setTemp(mainBoard: Int, mode: Int, tempNum: String) {
        isReceiveTemp=false;
        stopLoopCheckCabinet()
        Log.i("串口内容", "设置温度")
        var drinkTempAgreement = byteArrayOf(0x01, 0x04, 0x00, 0x25)
        drinkTempAgreement[0] = mainBoard.toByte()
        drinkTempAgreement[2] = mode.toByte()
        var b = (tempNum.toInt() and 0xFF).toByte()
        drinkTempAgreement[3] = b
        var result = CRC16Util.calcCrc16ToBytes(drinkTempAgreement, true)
        //记得前提要打开串口
        LogUtils.file("温度设置", "指令=" + ChangeTool.ByteArrToHex(result))
        mHandler.postDelayed(Runnable { mSerialPortManager?.sendBytes(result) }, delaySend)
//        mSerialPortManager?.sendBytes(result)
        againRunSetTemp=result
        startBackCountDown()
    }
    /**
     * 启动电机
     * 单次出货
     *  mainBoard 主板
     * mode 温度模式
     * tempNum 温度值
     *
     *
     */
    fun runHeatShipment(mode: Int, channle: Int, half: Int, heatTime: Int, upDoor: Int, downDoor: Int) {
        isReceiveRunShipment=false;
        stopLoopCheckCabinet();
        Log.i("串口内容", "启动电机" + channle)
        LogUtils.file("启动电机", "runHeatShipment");
        var heatShipmentAgreement = byteArrayOf(0x01, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00)
        heatShipmentAgreement[2] = mode.toByte();
        heatShipmentAgreement[3] = ChangeTool.HexToByte(Integer.toHexString(channle))
        heatShipmentAgreement[4] = half.toByte()
        heatShipmentAgreement[5] = ChangeTool.HexToByte(Integer.toHexString(heatTime))
        val strBuilder = StringBuilder();
        strBuilder.append("0000")
        if (upDoor == 0) {
            strBuilder.append("00")
        } else if (upDoor == 1) {
            strBuilder.append("01")
        } else if (upDoor == 2) {
            strBuilder.append("10")
        }
        if (downDoor == 0) {
            strBuilder.append("00")
        } else if (downDoor == 1) {
            strBuilder.append("01")
        } else if (downDoor == 2) {
            strBuilder.append("10")
        }
        heatShipmentAgreement[6] = ChangeTool.bitToByte(strBuilder.toString())
        var result = CRC16Util.calcCrc16ToBytes(heatShipmentAgreement, true)
        //记得前提要打开串口
//            mSerialPortManager?.sendBytes(result)
        Log.i("roshen", "发送出货指令");
        LogUtils.file("启动电机", "指令=" + ChangeTool.ByteArrToHex(result))
        mHandler.postDelayed(Runnable { mSerialPortManager?.sendBytes(result) }, delaySend)
        againRunDrinkShipment=result
        startBackCountDown()
    }

    /**
     * 发送ACK确认码
     */
    fun sendACK(mainBoard: Int) {
        isReceiveACK=false;
        stopLoopCheckCabinet();
        Log.i("串口内容", "发送ACK确认码")
        var drinkACKAgreement = byteArrayOf(0x01, 0x06)
        drinkACKAgreement[0] = mainBoard.toByte()
        var result = CRC16Util.calcCrc16ToBytes(drinkACKAgreement, true)
        //记得前提要打开串口
//        mSerialPortManager?.sendBytes(result)
        LogUtils.file("ACK确认码", "指令=" + ChangeTool.ByteArrToHex(result))
        mHandler.postDelayed(Runnable { mSerialPortManager?.sendBytes(result) }, delaySend)
        againRunACK=result
        startBackCountDown()
    }

    /**
     * 微波炉故障一键清除
     */
    fun sendMicrowaveClear() {
        isReceiveWB=false;
        stopLoopCheckCabinet();
        Log.i("串口内容", "微波炉故障一键清除")
        var microwaveClear = byteArrayOf(0x01, 0x07)
        var result = CRC16Util.calcCrc16ToBytes(microwaveClear, true)
        LogUtils
            .file("微波炉故障一键清除", "指令=" + ChangeTool.ByteArrToHex(result))
        //记得前提要打开串口
//        mSerialPortManager?.sendBytes(result)
        mHandler.postDelayed(Runnable { mSerialPortManager?.sendBytes(result) }, delaySend)
        againRunWB=result
        startBackCountDown()
    }


    var sendTimer: CountDownTimer=object :  CountDownTimer(2000, 1000){
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            if(!isReceiveRunShipment){
                //重新发送出货命令
                LogUtils.file("启动电机", "检察到未接受出货命令 重新发送"+ChangeTool.ByteArrToHex(againRunDrinkShipment))
                mSerialPortManager?.sendBytes(againRunDrinkShipment)
                startBackCountDown()
            }
            else if(!isReceiveTemp){
                LogUtils.file("设置温度", " 重新发送" + ChangeTool.ByteArrToHex(againRunSetTemp))
                mSerialPortManager?.sendBytes(againRunSetTemp)
                startBackCountDown()
            }
            else if(!isReceiveACK){
                LogUtils.file("ACK", " 重新发送" + ChangeTool.ByteArrToHex(againRunACK))
                mSerialPortManager?.sendBytes(againRunACK)
                startBackCountDown()
            }
            else if(!isReceiveWB){
                LogUtils.file("微波", " 重新发送" + ChangeTool.ByteArrToHex(againRunWB))
                mSerialPortManager?.sendBytes(againRunWB)
                startBackCountDown()
            }
        }
    }
    fun startBackCountDown() {
        finishBackCountDown()
        sendTimer.start()
    }


    fun finishBackCountDown() {
        if (sendTimer != null) {
            sendTimer.cancel()
        }
    }



    fun openSerial(deviceAddress: String, baudRate: String): Boolean {
        return serialportOpen(deviceAddress, baudRate)
    }

    /*
        打开串口
     */
    fun openSerial(mContext: Context): Boolean {
        //安卓板的串口名
        var deviceAddress = ""
        //波特率
        var baudRate = ""
        SharedPreferenceUtil.initSharedPreferenc(mContext)
        deviceAddress = SharedPreferenceUtil.getStrData(SharePConstant.PARAM_SERIALPORT_DEVICE)
        baudRate = SharedPreferenceUtil.getStrData(SharePConstant.PARAM_SERIALPORT_BAUDRATE)
        if ((deviceAddress == "") && (baudRate == "")) {
            deviceAddress = ""
            baudRate = "0"
        }
        return serialportOpen(deviceAddress, baudRate)
    }

    fun subByte(b: ByteArray?, off: Int, length: Int): ByteArray? {
        var b1: ByteArray
        try {
            b1 = ByteArray(length)
            System.arraycopy(b, off, b1, 0, length)
        } catch (e: Exception) {
            return null;
        }
        return b1
    }

    private fun serialportOpen(deviceAddress: String, baudRate: String): Boolean {
        // 打开串口
        mSerialPortManager = SerialPortManager.instance()
        mSerialPortManager?.setOnSerialPortDataListener(object : OnSerialPortDataListener {
            override fun onDataReceived(bytes: ByteArray?) {
                //接收
                var data: String = ChangeTool.ByteArrToHex(bytes)
//                LogUtils.file("接受到串口数据", "未验证数据= $data")
                Log.i("接受到串口数据","data："+data)
                var sb: ByteArray? = subByte(bytes, 0, bytes!!.size - 2) ?: return
                var newB = CRC16Util.calcCrc16ToBytes(sb, true)
                if (newB != null) {
                    if (!tempReceiveData.equals(data) && ChangeTool.ByteArrToHex(newB).equals(data)) {
                        LogUtils.file("接受到串口数据", " 验证后的数据= $data")
                        tempReceiveData = data;
                        data = data.trim().replace(" ", "")
//                        if (data.length < 8) return;
                        when {
                            data.subSequence(2, 4) == "03" -> {
                                //轮询
                                    LogUtils.file("接受到串口数据", "轮询")
                                 //0003010100030C0000D8F2F1
                                   var bs= Bus_LooperHeatBean(data)
                                    LogUtils.file("当前温度", "温度="+bs.real_Temp)
                                    RxBus.getDefault().post(bs)
                            }
                            data.subSequence(2, 4) == "04" -> {
                                isReceiveTemp=true
                                finishBackCountDown()
                                LogUtils.file("接受到串口数据", "溫度设置")
                                //溫度更新
//                                RxBus.getDefault().post(Bus_TempBean(data))
                                sstartLoopCheckCabinet()
                            }
                            data.subSequence(2, 4) == "05" -> {
                                isReceiveRunShipment=true
                                finishBackCountDown()
                                //启动电机
                                LogUtils.file("接受到串口数据", "启动电机")
//                                finishBackCountDown()
//                                isReceiveRunShipment=true;
//                                RxBus.getDefault().post(Bus_ShipmentHeatBean(data))
                                sstartLoopCheckCabinet()
                            }
                            data.subSequence(2, 4) == "06" -> {
                                isReceiveACK=true
                                finishBackCountDown()
//                                sstartLoopCheckCabinet()
                                //收到ACK回复
                                RxBus.getDefault().post(Bus_ACKBean())
                                LogUtils.file("接受到串口数据", "收到ACK回复")
                            }
                            data.subSequence(2, 4) == "07" -> {
                                isReceiveWB=true
                                finishBackCountDown()
                                //收到微波炉故障一键清除
                                LogUtils.file("接受到串口数据", "收到微波炉故障一键清除")
                                sstartLoopCheckCabinet()
                            }
                            //回调
                        }
                        //回调
                        listener?.OnListener(data);
                        LogUtils.file("接受到串口数据", " 验证后的数据= $data")
                    }
                }
            }

            override fun onDataSent(bytes: ByteArray?) {
                val data: String = ChangeTool.ByteArrToHex(bytes)
//                if (!tempSendData.equals(data)) {
//                    tempSendData = data;
//                Log.i("串口", "发送---->数据 --- " + data)
//                }
            }

        });
        var result = mSerialPortManager?.openSerialPort(deviceAddress, baudRate)
        return result!!

    }

    fun closeSerialPort() {
        // 打开串口
        finishBackCountDown()
        tempReceiveData = "";
        mSerialPortManager = SerialPortManager.instance()
        mSerialPortManager?.closeSerialPort();
    }


    /**
     * 定义一个接口
     */
    interface onReceivedListener {
        fun OnListener(data: String)
    }

    /**
     * 定义一个变量储存数据
     */
    private var listener: onReceivedListener? = null

    /**
     * 提供公共的方法,并且初始化接口类型的数据
     */
    fun setReceivedListener(listener: onReceivedListener?) {
        this.listener = listener
    }
}