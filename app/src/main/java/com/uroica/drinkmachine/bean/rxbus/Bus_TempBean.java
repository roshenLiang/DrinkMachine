//package com.uroica.heartmachine.bean.rxbus;
//
///**
// * 设置温度协议
// */
//public class Bus_TempBean {
//    //主板地址 1234
//    private int mainBoard;
//    //命令字
//    private int command;
//    //温度模式 0x00，常温；0x01，制冷；0x02，加热
//    private int temp_mode;
//    //设置温度值 	有符号数，单位：℃
//    private int temp_num;
//    //协议命令
//    private String data;
//
//    public Bus_TempBean() {
//    }
//
//    public Bus_TempBean(String data) {
//        if(data.length()<8)return;
//        this.data = data;
//        try {
//            this.mainBoard = Integer.valueOf(data.substring(0, 2)) - 80;
//            this.command = Integer.valueOf(data.substring(2, 4));
//            this.temp_mode = Integer.valueOf(data.substring(4, 6));
//            this.temp_num = Integer.parseInt(data.substring(6, 8), 16);
//        }catch (Exception e){
//
//        }
////        Log.i("roshen", "mainBoard=" + mainBoard);
////        Log.i("roshen", "command=" + command);
////        Log.i("roshen", "temp_mode=" + temp_mode);
////        Log.i("roshen", "temp_num=" + temp_num);
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
//    public int getTemp_mode() {
//        return temp_mode;
//    }
//
//    public void setTemp_mode(int temp_mode) {
//        this.temp_mode = temp_mode;
//    }
//
//    public int getTemp_num() {
//        return temp_num;
//    }
//
//    public void setTemp_num(int temp_num) {
//        this.temp_num = temp_num;
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
