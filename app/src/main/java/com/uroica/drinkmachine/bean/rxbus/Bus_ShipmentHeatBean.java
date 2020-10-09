//package com.uroica.heartmachine.bean.rxbus;
//
///**
// * 启动电机协议
// */
//public class Bus_ShipmentHeatBean {
//    //主板地址 1234
//    private int mainBoard;
//    //命令字
//    private int command;
//    //模式1:正常购买，2:测试货道电机，3:测试加热装置，4:加热装置点动控制
//    private int mode;
//    //    命令是否执行
//    //0 表示已执行，
//    //1 表示无效的电机索引
//    //2 表示当前有另一个电机正在运行
//    //3 表示另一台电机的运转结果还未取走
//    private int commandIsOk;
//    //协议命令
//    private String data;
//
//    public Bus_ShipmentHeatBean() {
//    }
//
//    public Bus_ShipmentHeatBean(String data) {
//        if(data.length()<8)return;
//        this.data = data;
//        try {
//            this.mainBoard = Integer.valueOf(data.substring(0, 2)) - 80;
//            this.command = Integer.valueOf(data.substring(2, 4));
//            this.mode = Integer.valueOf(data.substring(4, 6));
//            this.commandIsOk = Integer.valueOf(data.substring(6, 8));
//        }catch (Exception e){
//
//        }
////        Log.i("roshen", "mainBoard=" + mainBoard);
////        Log.i("roshen", "command=" + command);
////        Log.i("roshen", "commandIsOk=" + commandIsOk);
//    }
//
//    public int getMode() {
//        return mode;
//    }
//
//    public void setMode(int mode) {
//        this.mode = mode;
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
//    public int getCommandIsOk() {
//        return commandIsOk;
//    }
//
//    public void setCommandIsOk(int commandIsOk) {
//        this.commandIsOk = commandIsOk;
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
