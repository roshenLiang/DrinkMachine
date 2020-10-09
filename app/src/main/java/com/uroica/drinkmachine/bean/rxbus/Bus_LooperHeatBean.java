package com.uroica.drinkmachine.bean.rxbus;

import android.util.Log;

import com.uroica.drinkmachine.util.ChangeTool;

/**
 * 热卖机轮询协议
 */
public class Bus_LooperHeatBean {
    //    00 03 00 00 00 00 00 00 00 D8 64 AD
    //只有一个
    private int mainBoard;
    //命令字
    private int command;
    //控制板状态 0空闲,1忙,2结束
    private int control_state;
    //当前货道电机号0-12
    private int current_Channel;
    //执行结果 0,出货成功；1,出货失败。
    private int shipment_result;
    //当前模式 0:空闲1:正常购买，2:测试货道电机，3:测试加热装置，4加热装置点动控制
    private int cur_mode;
    //    加热装置故障状态和货道电机故障状态
    //Bit5:Bit4（货道电机）: 0= 无故障  1=过流 2=断线 3=超时
    //Bit3:上门开门限位故障(0正常1故障)
    //Bit2:上门关门限位故障(0正常1故障)
    //Bit1:下门开门限位故障(0正常1故障)
    //Bit0:下门关门限位故障(0正常1故障)
    private String heatAndChannelStatus;
    private int channelDStatus;//货道电机
    private int up_door_open_status;//上门开门限位故障
    private int up_door_close_status;//上门关门限位故障
    private int down_door_open_status;//下门开门限位故障
    private int down_door_close_status;//下门关门限位故障
    //压缩机运行状态
    // Bit7:状态位，0正常，1故障
    //Bit0~Bit6：当前电流值，单位0.1A
    private int compressorStatus;
    private int copmressorS;
    private int copmressorA;
    //微波炉运行状态
    //Bit7:状态位，0正常，1故障
    //Bit0~Bit6：当前电流值，单位0.1A
    private int microwaveStatus;
    private int microwaveS;
    private int microwaveA;
    //实时温度 数值：-127---127; 单位：℃
    private int real_Temp;


    //协议命令
    private String data;

    public Bus_LooperHeatBean() {
    }

    public Bus_LooperHeatBean(String data) {
        if (data.length() < 24) return;
        this.data = data;
        try {
//            0003 0201 0000 0C00 0000 0000
            this.mainBoard = Integer.valueOf(data.substring(0, 2));
            this.command = Integer.valueOf(data.substring(2, 4));
            this.control_state = Integer.valueOf(data.substring(4, 6));
            Log.i("roshen", "control_state=" + control_state);
            if (control_state != 0) {
                this.heatAndChannelStatus = data.substring(12, 14);
                Log.i("roshen", "heatAndChannelStatus=" + heatAndChannelStatus);
                String HAC = ChangeTool.byteToBit((byte) Integer.parseInt(String.valueOf(heatAndChannelStatus), 16));
                Log.i("roshen", "HAC=" + HAC);
                channelDStatus = Integer.parseInt(HAC.substring(2, 4), 2);
                up_door_open_status = Integer.valueOf(HAC.substring(4, 5));
                up_door_close_status = Integer.valueOf(HAC.substring(5, 6));
                down_door_open_status = Integer.valueOf(HAC.substring(6, 7));
                down_door_close_status = Integer.valueOf(HAC.substring(7, 8));
                Log.i("roshen", "up_door_open_status=" + up_door_open_status);
                Log.i("roshen", "up_door_close_status=" + up_door_close_status);
                Log.i("roshen", "down_door_open_status=" + down_door_open_status);
                Log.i("roshen", "down_door_close_status=" + down_door_close_status);
            }
            this.current_Channel = Integer.parseInt(data.substring(6, 8), 16);
            this.shipment_result = Integer.valueOf(data.substring(8, 10));
            this.cur_mode = Integer.valueOf(data.substring(10, 12));
            this.compressorStatus = Integer.parseInt(data.substring(14, 16), 16);
            String CS = ChangeTool.byteToBit((byte) compressorStatus);
            copmressorS = Integer.valueOf(CS.substring(0, 1));
            copmressorA = Integer.valueOf(CS.substring(1, 8), 2);
            //        ----
            this.microwaveStatus = Integer.parseInt(data.substring(16, 18), 16);
            String MS = ChangeTool.byteToBit((byte) microwaveStatus);
            microwaveS = Integer.valueOf(MS.substring(0, 1));
            microwaveA = Integer.valueOf(MS.substring(1, 8), 2);
//            this.real_Temp = Integer.parseInt(data.substring(18, 20), 16);
            this.real_Temp = Integer.valueOf(ChangeTool.HexString2String(data.substring(18, 20)));
        } catch (Exception e) {

        }


//        Log.i("roshen", "mainBoard=" + mainBoard);
//        Log.i("roshen", "command=" + command);
//        Log.i("roshen", "control_state=" + control_state);
//        Log.i("roshen", "current_Channel=" + current_Channel);
//        Log.i("roshen", "shipment_result=" + shipment_result);
//        Log.i("roshen", "cur_mode=" + cur_mode);
//        Log.i("roshen", "heatAndChannelStatus=" + heatAndChannelStatus);
//        Log.i("roshen", "channelDStatus=" + channelDStatus);
//        Log.i("roshen", "up_door_open_status=" + up_door_open_status);
//        Log.i("roshen", "up_door_close_status=" + up_door_close_status);
//        Log.i("roshen", "down_door_open_status=" + down_door_open_status);
//        Log.i("roshen", "down_door_close_status=" + down_door_close_status);
    }

    public int getMainBoard() {
        return mainBoard;
    }

    public void setMainBoard(int mainBoard) {
        this.mainBoard = mainBoard;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getControl_state() {
        return control_state;
    }

    public String getControl_state2String() {
        if (control_state == 0) {
            return "控制板状态：空闲";
        } else if (control_state == 1) {
            return "控制板状态：忙";
        } else {
            return "控制板状态：结束";
        }
    }

    public void setControl_state(int control_state) {
        this.control_state = control_state;
    }

    public int getCurrent_Channel() {
        return current_Channel;
    }

    public void setCurrent_Channel(int current_Channel) {
        this.current_Channel = current_Channel;
    }

    public int getShipment_result() {
        return shipment_result;
    }

    public String getShipment_result2String() {
        if (shipment_result == 0) {
            return "出货结果：成功";
        } else {
            return "出货结果：失败";
        }
    }

    public void setShipment_result(int shipment_result) {
        this.shipment_result = shipment_result;
    }

    public int getCur_mode() {
        return cur_mode;
    }

    public String getCur_mode2String() {
        if (cur_mode == 0) {
            return "当前模式：空闲";
        } else if (cur_mode == 1) {
            return "当前模式：正常购买";
        } else if (cur_mode == 2) {
            return "当前模式：测试货道电机";
        } else if (cur_mode == 3) {
            return "当前模式：测试加热装置";
        } else {
            return "当前模式：加热装置点动控制";
        }
    }

    public void setCur_mode(int cur_mode) {
        this.cur_mode = cur_mode;
    }

    public String getHeatAndChannelStatus() {
        return heatAndChannelStatus;
    }

    public void setHeatAndChannelStatus(String heatAndChannelStatus) {
        this.heatAndChannelStatus = heatAndChannelStatus;
    }

    public int getChannelDStatus() {
        return channelDStatus;
    }

    public String getChannelDStatus2String() {
        if (channelDStatus == 0) {
            return "货道电机：无故障";
        } else if (channelDStatus == 1) {
            return "货道电机：过流";
        } else if (channelDStatus == 2) {
            return "货道电机：断线";
        } else {
            return "货道电机：超时";
        }
    }

    public void setChannelDStatus(int channelDStatus) {
        this.channelDStatus = channelDStatus;
    }

    public int getUp_door_open_status() {
        return up_door_open_status;
    }

    public String getUp_door_open_status2String() {
        if (up_door_open_status == 0) {
            return "上门开门限位：正常";
        } else {
            return "上门开门限位：故障";
        }
    }

    public String getDown_door_open_status2String() {
        if (down_door_open_status == 0) {
            return "下门开门限位：正常";
        } else {
            return "下门开门限位：故障";
        }
    }

    public void setUp_door_open_status(int up_door_open_status) {
        this.up_door_open_status = up_door_open_status;
    }

    public int getUp_door_close_status() {
        return up_door_close_status;
    }

    public String getUp_door_close_status2String() {
        if (up_door_close_status == 0) {
            return "上门关门限位：正常";
        } else {
            return "上门关门限位：故障";
        }
    }

    public String getDown_door_close_status2String() {
        if (down_door_close_status == 0) {
            return "下门关门限位：正常";
        } else {
            return "下门关门限位：故障";
        }
    }

    public void setUp_door_close_status(int up_door_close_status) {
        this.up_door_close_status = up_door_close_status;
    }

    public int getDown_door_open_status() {
        return down_door_open_status;
    }

    public void setDown_door_open_status(int down_door_open_status) {
        this.down_door_open_status = down_door_open_status;
    }

    public int getDown_door_close_status() {
        return down_door_close_status;
    }

    public void setDown_door_close_status(int down_door_close_status) {
        this.down_door_close_status = down_door_close_status;
    }

    public int getCompressorStatus() {
        return compressorStatus;
    }

    public void setCompressorStatus(int compressorStatus) {
        this.compressorStatus = compressorStatus;
    }

    public int getCopmressorS() {
        return copmressorS;
    }

    public String getCopmressorS2String() {
        if (copmressorS == 0) {
            return "压缩机运行状态：正常";
        } else {
            return "压缩机运行状态：故障";
        }
    }

    public void setCopmressorS(int copmressorS) {
        this.copmressorS = copmressorS;
    }

    public int getCopmressorA() {
        return copmressorA;
    }

    public void setCopmressorA(int copmressorA) {
        this.copmressorA = copmressorA;
    }

    public int getMicrowaveStatus() {
        return microwaveStatus;
    }

    public void setMicrowaveStatus(int microwaveStatus) {
        this.microwaveStatus = microwaveStatus;
    }

    public int getMicrowaveS() {
        return microwaveS;
    }

    public String getMicrowaveS2String() {
        if (microwaveS == 0) {
            return "微波炉运行状态：正常";
        } else {
            return "微波炉运行状态：故障";
        }
    }

    public void setMicrowaveS(int microwaveS) {
        this.microwaveS = microwaveS;
    }

    public int getMicrowaveA() {
        return microwaveA;
    }

    public void setMicrowaveA(int microwaveA) {
        this.microwaveA = microwaveA;
    }

    public int getReal_Temp() {
        return real_Temp;
    }

    public void setReal_Temp(int real_Temp) {
        this.real_Temp = real_Temp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
