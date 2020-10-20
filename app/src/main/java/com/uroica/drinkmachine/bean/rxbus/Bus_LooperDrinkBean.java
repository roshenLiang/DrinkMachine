package com.uroica.drinkmachine.bean.rxbus;

import com.uroica.drinkmachine.util.ChangeTool;

/**
 * 轮询协议
 */
public class Bus_LooperDrinkBean {
    //主板地址 1234
    private int mainBoard;
    //命令字
    private int command;
    //设备类型 1弹簧机2格子柜3桶装水
    private int device_type;
    //控制板状态 0空闲,1忙,2结束
    private int control_state;
    //当前货道电机号
    private int current_Channel;
    //执行结果 Bit1:Bit0（货道电机）: 0= 无故障  1=过流 2=断线 3=超时
    //Bit2,0:出货成功  1:出货失败。
    private int result;

    private int result_channel;
    private int result_shipment;
    //振动情况 0正常，1振动报警
    private int vibration_state;
    //实时温度 数值：-127---127; 单位：℃
    private int real_Temp;

    //协议命令
    private String data;

    public Bus_LooperDrinkBean() {
    }

    public Bus_LooperDrinkBean(String data) {
        if(data.length()<20)return;
        this.data = data;
        try {
            this.mainBoard = Integer.valueOf(data.substring(0, 2)) - 80;
            this.command = Integer.valueOf(data.substring(2, 4));
            this.device_type = Integer.valueOf(data.substring(4, 6));
            this.control_state = Integer.valueOf(data.substring(6, 8));
            this.current_Channel = Integer.parseInt(data.substring(8, 10), 16);
            this.result = Integer.valueOf(data.substring(10, 12));
            String ss = ChangeTool.byteToBit((byte) result);
            this.result_channel = Integer.parseInt(ss.substring(6, 8),2);
            this.result_shipment = Integer.valueOf(ss.substring(5, 6));
            this.vibration_state = Integer.valueOf(data.substring(12, 14));
            this.real_Temp = Integer.valueOf(ChangeTool.HexString2String(data.substring(14, 16)));
        }catch (Exception e){

        }
//        Log.i("roshen", "mainBoard=" + mainBoard);
//        Log.i("roshen", "command=" + command);
//        Log.i("roshen", "device_type=" + device_type);
//        Log.i("roshen", "control_state=" + control_state);
//        Log.i("roshen", "current_Channel=" + current_Channel);
//        Log.i("roshen", "result=" + result);
//        Log.i("roshen", "vibration_state=" + vibration_state);
//        Log.i("roshen", "real_Temp=" + real_Temp);
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

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getVibration_state() {
        return vibration_state;
    }

    public String getVibration_state2String() {
        if (vibration_state == 0) {
            return "振动状态：正常";
        } else {
            return "振动状态：振动报警";

        }
    }

    public void setVibration_state(int vibration_state) {
        this.vibration_state = vibration_state;
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

    public int getResult_channel() {
        return result_channel;
    }

    public String getResult_channel2String() {
        if (result_channel == 0) {
            return "货道电机：无故障";
        } else if (result_channel == 1) {
            return "货道电机：过流";
        } else if (result_channel == 2) {
            return "货道电机：断线";
        } else {
            return "货道电机：超时";
        }
    }

    public void setResult_channel(int result_channel) {
        this.result_channel = result_channel;
    }

    public int getResult_shipment() {
        return result_shipment;
    }

    public String getResult_shipment2String() {
        if (control_state == 2)
            if (result_shipment == 0) {
                return "出货状态：出货成功";
            } else {
                return "出货状态：出货失败";
            }
        else{
            return "出货状态：- -";
        }
    }

    public void setResult_shipment(int result_shipment) {
        this.result_shipment = result_shipment;
    }
}
