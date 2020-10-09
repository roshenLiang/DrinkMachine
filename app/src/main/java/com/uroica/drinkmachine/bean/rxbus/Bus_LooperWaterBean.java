//package com.uroica.heartmachine.bean.rxbus;
//
//import com.uroica.heartmachine.util.ChangeTool;
//
///**
// * 水机轮询协议
// */
//public class Bus_LooperWaterBean {
//    //主板
//    private int mainBoard;
//
//    //命令字
//    private int command;
//
//    //设备类型 1弹簧机2格子柜3桶装水
//    private int device_type;
//
//    //控制板状态 0空闲,1忙,2结束
//    private int control_state;
//
//    //当前货道电机号01-02
//    private int current_Channel;
//
//    //执行结果 0,出货成功；1,出货失败。
//    private int shipment_result;
//
//    //振动情况 0正常，1振动报警
//    private int vibration_state;
//
//    //货道1故障
//    private int channelStatusFault1;
//
//    //货道2故障
//    private int channelStatusFault2;
//
//    //协议命令
//    private String data;
//
//    public Bus_LooperWaterBean() {
//    }
//
//    public Bus_LooperWaterBean(String data) {
//        if (data.length() < 22) return;
//        this.data = data;
//        try {
//            this.mainBoard = Integer.valueOf(data.substring(0, 2)) - 80;
//            this.command = Integer.valueOf(data.substring(2, 4));
//            this.device_type = Integer.valueOf(data.substring(4, 6));
//            this.control_state = Integer.valueOf(data.substring(6, 8));
//            //这里分控制板1 控制板2
////            String controlBit = ChangeTool.byteToBit((byte) Integer.parseInt(String.valueOf(control_stateAll), 16));
////            control_state2 = Integer.valueOf(controlBit.substring(0, 4), 2);
////            control_state1 = Integer.valueOf(controlBit.substring(4, 8), 2);
//
//            this.current_Channel = Integer.parseInt(data.substring(8, 10), 16);
//            this.shipment_result = Integer.valueOf(data.substring(10, 12));
////            这里分控制板1 控制板2
////            String shipmentBit = ChangeTool.byteToBit((byte) Integer.parseInt(String.valueOf(shipment_resultAll), 16));
////            shipment_result2 = Integer.valueOf(shipmentBit.substring(0, 4), 2);
////            shipment_result1 = Integer.valueOf(shipmentBit.substring(4, 8), 2);
//
//            this.vibration_state = Integer.valueOf(data.substring(12, 14));
//            this.channelStatusFault1 = Integer.parseInt(data.substring(14, 16), 16);
//            this.channelStatusFault2 = Integer.parseInt(data.substring(16,18), 16);
//        } catch (Exception e) {
//
//        }
//    }
//
//    public int getMainBoard() {
//        return mainBoard;
//    }
//
//    public void setMainBoard(int mainBoard) {
//        this.mainBoard = mainBoard;
//    }
//
//    public int getCommand() {
//        return command;
//    }
//
//    public void setCommand(int command) {
//        this.command = command;
//    }
//
//    public int getDevice_type() {
//        return device_type;
//    }
//
//    public void setDevice_type(int device_type) {
//        this.device_type = device_type;
//    }
//
//    public int getControl_state() {
//        return control_state;
//    }
//    public String getControl_stateString() {
//        if (control_state == 0) {
//            return "控制板状态：空闲";
//        } else if (control_state == 1) {
//            return "控制板状态：忙";
//        } else {
//            return "控制板状态：结束";
//        }
//    }
//
//    public void setControl_state(int control_state) {
//        this.control_state = control_state;
//    }
//
//
//    public int getCurrent_Channel() {
//        return current_Channel;
//    }
//
//    public void setCurrent_Channel(int current_Channel) {
//        this.current_Channel = current_Channel;
//    }
//
//    public int getShipment_result() {
//        return shipment_result;
//    }
//
//    public void setShipment_result(int shipment_result) {
//        this.shipment_result = shipment_result;
//    }
//
//    public int getVibration_state() {
//        return vibration_state;
//    }
//
//    public void setVibration_state(int vibration_state) {
//        this.vibration_state = vibration_state;
//    }
//
//    public int getChannelStatusFault1() {
//        return channelStatusFault1;
//    }
//
//    public void setChannelStatusFault1(int channelStatusFault1) {
//        this.channelStatusFault1 = channelStatusFault1;
//    }
//
//    public int getChannelStatusFault2() {
//        return channelStatusFault2;
//    }
//
//    public void setChannelStatusFault2(int channelStatusFault2) {
//        this.channelStatusFault2 = channelStatusFault2;
//    }
//
//    public String getData() {
//        return data;
//    }
//
//    public void setData(String data) {
//        this.data = data;
//    }
//}
