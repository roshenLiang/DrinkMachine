package com.uroica.drinkmachine.ui.factorymode.testmode;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
//import com.uroica.heartmachine.bean.rxbus.Bus_ACKBean;
import com.uroica.drinkmachine.bean.rxbus.Bus_LooperHeatBean;
import com.uroica.drinkmachine.constant.Constant;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.gen.ShopManagerDBDao;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.ToastUtils;


/**
 *
 */

public class DrinkTestModeViewModel extends BaseViewModel {

    private String TAG = "HeatTestModeViewModel";
    private List<Integer> shipmentLists;
    private int shipmentIndex = 0;
    public int temp_mode = 1;//0x00，常温；0x01，制冷；0x02，加热
    //    private int ship_mode =1;//1:正常购买，2:测试货道电机，3:测试加热装置，4:加热装置点动控制
    private int up_door_state = 2, down_door_state = 2;//上下门 的状态  0:无动作,1:开，2:关
    //订阅者
//    private Disposable mSubscription_LoopBean;
//    private Disposable mSubscription_Ack;
    private Bus_LooperHeatBean looperHeatBean;

    public ObservableField<String> curTemp = new ObservableField<>("");
    public ObservableField<String> curControlStatus = new ObservableField<>("");
    public ObservableField<String> shipmentChannelD = new ObservableField<>("");
    //    public ObservableField<String> channelResult = new ObservableField<>("");
    public ObservableField<String> curMode = new ObservableField<>("");
    public ObservableField<String> heatChannleD = new ObservableField<>("");
    public ObservableField<String> upDoorOpen = new ObservableField<>("");
    public ObservableField<String> upDoorClose = new ObservableField<>("");
    public ObservableField<String> downDoorOpen = new ObservableField<>("");
    public ObservableField<String> downDoorClose = new ObservableField<>("");
    public ObservableField<String> copmressorS = new ObservableField<>("");
    public ObservableField<String> copmressorA = new ObservableField<>("");
    public ObservableField<String> microwaveS = new ObservableField<>("");
    public ObservableField<String> microwaveA = new ObservableField<>("");

    public ObservableField<String> upDoorStatus = new ObservableField<>("上门开");
    public ObservableField<String> downDoorStatus = new ObservableField<>("下门开");


    //单货道出货 货道号
    public ObservableField<String> channelId = new ObservableField<>("");
    //层数
    public ObservableField<String> floorId = new ObservableField<>("");
    //设置温度
    public ObservableField<String> temp = new ObservableField<>("");
    //设置心跳
    public ObservableField<String> heart = new ObservableField<>("");


    public DrinkTestModeViewModel(@NonNull Application application) {
        super(application);
        clearStatus();
    }

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //发送测试 隐藏键盘
        public SingleLiveEvent hintKeyBoardEvent = new SingleLiveEvent<>();
        public SingleLiveEvent tempEvent = new SingleLiveEvent<>();
        public SingleLiveEvent heartEvent = new SingleLiveEvent<>();
        public SingleLiveEvent volumeAddEvent = new SingleLiveEvent<>();
        public SingleLiveEvent volumePlusEvent = new SingleLiveEvent<>();
    }

    int halfCirInt = 0;
    public BindingCommand setHalfCirOnClickCommand = new BindingCommand(new BindingConsumer<Boolean>() {
        @Override
        public void call(Boolean aBoolean) {
            if (aBoolean) {
                halfCirInt = 1;
            } else {
                halfCirInt = 0;
            }
        }
    });


    //测试加热装置
    public BindingCommand testHeartOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (looperHeatBean.getControl_state() == 1 || looperHeatBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            singleDoorAction = false;
            singleShipmentAction = true;
            ToastUtils.showShort("发送‘测试加热装置’命令");

            LogUtils.file("测试模式", "发送‘测试加热装置’命令");
            AgreementManager.Companion.getInstance().runHeatShipment(3, 1, halfCirInt, 0, 0, 0);
        }
    });

    boolean singleDoorAction = false;// false就是更新 ture不更新
    boolean singleShipmentAction = false;// false就是更新 ture不更新
    //上门
    public BindingCommand upDoorOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (looperHeatBean.getControl_state() == 1 || looperHeatBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            singleShipmentAction = true;
            singleDoorAction = true;
            if (up_door_state != 1) {
                up_door_state = 1;
                upDoorStatus.set("上门关");
                ToastUtils.showShort("发送‘上门开’命令");
                LogUtils.file("测试模式", "发送‘上门开’命令");
            } else if (up_door_state != 2) {
                up_door_state = 2;
                upDoorStatus.set("上门开");
                ToastUtils.showShort("发送‘上门关’命令");
                LogUtils.file("测试模式", "发送‘上门关’命令");
            }

            AgreementManager.Companion.getInstance().runHeatShipment(4, 1, halfCirInt, 1, up_door_state, 0);
        }
    });
    //下门
    public BindingCommand downDoorOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (looperHeatBean.getControl_state() == 1 || looperHeatBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            singleShipmentAction = true;
            singleDoorAction = true;
            if (down_door_state != 1) {
                down_door_state = 1;
                downDoorStatus.set("下门关");
                ToastUtils.showShort("发送‘下门开’命令");
                LogUtils.file("测试模式", "发送‘下门开’命令");
            } else if (down_door_state != 2) {
                down_door_state = 2;
                downDoorStatus.set("下门开");
                ToastUtils.showShort("发送‘下门关’命令");
                LogUtils.file("测试模式", "发送‘下门关’命令");
            }
            AgreementManager.Companion.getInstance().runHeatShipment(4, 1, halfCirInt, 1, 0, down_door_state);
        }
    });
    //单货道出货
    public BindingCommand go2OnlyShipmentOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (looperHeatBean.getControl_state() == 1 || looperHeatBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            //判空处理
            if (channelId.get().equals("")) {
                ToastUtils.showShort("请输入货道号！");
                return;
            }
            if (Integer.valueOf(channelId.get()) > Constant.TOTAL_CHANNEL || Integer.valueOf(channelId.get()) < 0) {
                ToastUtils.showShort("请输入正确的货道号：1-12号货道！");
                return;
            }
            singleShipmentAction = false;
            singleDoorAction = true;
            ToastUtils.showShort("发送‘出货’命令");
            LogUtils.file("测试模式", "发送‘单货道出货’命令");
            uc.hintKeyBoardEvent.call();
            shipmentLists = new ArrayList<>();
            shipmentIndex = 0;
            for (int i = 0; i < 1; i++) {
                shipmentLists.add(Integer.valueOf(channelId.get()));
            }
            AgreementManager.Companion.getInstance().runHeatShipment(2, shipmentLists.get(shipmentIndex), halfCirInt, 1, 0, 0);
        }
    });

    //整层道出货
    public BindingCommand go2FloorShipmentOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (looperHeatBean.getControl_state() == 1 || looperHeatBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            //判空处理
            if (floorId.get().equals("")) {
                ToastUtils.showShort("请输入货道层数");
                return;
            }
            if (Integer.valueOf(floorId.get()) > 3 || Integer.valueOf(floorId.get()) < 0) {
                ToastUtils.showShort("请输入正确的货道层数 提示：1-3");
                return;
            }
            singleShipmentAction = false;
            singleDoorAction = true;
            ToastUtils.showShort("发送‘整层出货’命令");
            LogUtils.file("测试模式", "发送‘整层出货’命令");
            uc.hintKeyBoardEvent.call();
            shipmentLists = new ArrayList<>();
            shipmentIndex = 0;
            for (int i = 4 * (Integer.valueOf(floorId.get()) - 1); i < 4 * Integer.valueOf(floorId.get()); i++) {
                if (i + 1 < Constant.TOTAL_CHANNEL + 1) {
                    Log.i("层数", "i=" + (i + 1));
                    shipmentLists.add(i + 1);
                }
            }
            AgreementManager.Companion.getInstance().runHeatShipment(2, shipmentLists.get(shipmentIndex), halfCirInt, 1, 0, 0);
        }
    });

    //全部出货
    public BindingCommand go2AllShipmentOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (looperHeatBean.getControl_state() == 1 || looperHeatBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            singleDoorAction = true;
            singleShipmentAction = false;
            ToastUtils.showShort("发送‘全部出货’命令");
            LogUtils.file("测试模式", "发送‘全部出货’命令");
            uc.hintKeyBoardEvent.call();
            shipmentLists = new ArrayList<>();
            shipmentIndex = 0;
            for (int i = 1; i < Constant.TOTAL_CHANNEL + 1; i++) {
                shipmentLists.add(i);
            }
            AgreementManager.Companion.getInstance().runHeatShipment(2, shipmentLists.get(shipmentIndex), halfCirInt, 1, 0, 0);
        }
    });
    public BindingCommand go2CancleShipmentOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ToastUtils.showShort("发送‘停止出货’命令");
            LogUtils.file("测试模式", "发送‘停止出货’命令");
            uc.hintKeyBoardEvent.call();
            shipmentLists = new ArrayList<>();
            shipmentIndex = 0;
        }
    });

    public BindingCommand<String> tempModeOnCheckedChangedCommand = new BindingCommand<>(new BindingConsumer<String>() {
        @Override
        public void call(String str) {
            if (str.equals("常温")) {
                temp_mode = 0;
            } else if (str.equals("制冷")) {
                temp_mode = 1;
            } else if (str.equals("加热")) {
                temp_mode = 2;
            }
        }
    });

    //设置温度
    public BindingCommand setTempOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            //判空处理
            if (temp.get().equals("")) {
                ToastUtils.showShort("请输入温度");
                return;
            }
            singleDoorAction = true;
            singleShipmentAction = true;
            ToastUtils.showShort("发送‘设置温度’命令");
            LogUtils.file("测试模式", "发送‘设置温度’命令");
            uc.tempEvent.call();
//            uc.hintKeyBoardEvent.call();
            AgreementManager.Companion.getInstance().setTemp(1, temp_mode, temp.get());
        }
    });

    int mwClear = -1;
    //微波炉故障一键
    public BindingCommand clearOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            singleDoorAction = true;
            singleShipmentAction = true;
            mwClear = 0;
            down_door_state = 2;
            downDoorOnClickCommand.execute();
//            down_door_state=2;
//            downDoorOnClickCommand.execute();
//            //发送下门开 再发送下门关，再发清除
//            LogUtils.file("测试模式", "发送‘微波炉故障清除’命令");
//            AgreementManager.Companion.getInstance().sendMicrowaveClear();
        }
    });

    //設置心跳
    public BindingCommand setHeartOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (heart.get().equals("")) {
                ToastUtils.showShort("请输入心跳");
                return;
            }
            if (Integer.valueOf(heart.get()) < 1) {
                ToastUtils.showShort("请输入正确的心跳");
                return;
            }
            ToastUtils.showShort("设置成功！");
            uc.heartEvent.call();
        }
    });
    //設置音量+
    public BindingCommand setVolumeAddOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.volumeAddEvent.call();
            uc.heartEvent.call();
        }
    });
    //設置音量-
    public BindingCommand setVolumePlusOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.volumePlusEvent.call();
            uc.heartEvent.call();
        }
    });


    public void setFaultDoor(String data) {
        Bus_LooperHeatBean faultBean = new Bus_LooperHeatBean(data);
        upDoorOpen.set(faultBean.getUp_door_open_status2String());
        upDoorClose.set(faultBean.getUp_door_close_status2String());
        downDoorOpen.set(faultBean.getDown_door_open_status2String());
        downDoorClose.set(faultBean.getDown_door_close_status2String());
    }

    public void setFaultShipment(String data) {
        Bus_LooperHeatBean faultBean = new Bus_LooperHeatBean(data);
        heatChannleD.set(faultBean.getChannelDStatus2String());
    }

    public void clearStatus() {
//        looperHeatBean = new Bus_LooperHeatBean("0003020100000C0000000000");
//        Log.i(TAG, "RxBus 接收到轮询数据" + looperHeatBean.getData());
//        if (looperHeatBean.getReal_Temp() == -40) {
//            curTemp.set("温度：探头断线");
//        } else if (looperHeatBean.getReal_Temp() == 90) {
//            curTemp.set("温度：探头短路");
//        } else {
//            curTemp.set("温度：" + looperHeatBean.getReal_Temp() + " °C");
//        }
//        curControlStatus.set(looperHeatBean.getControl_state2String());
//        shipmentChannelD.set("货道号：" + looperHeatBean.getCurrent_Channel());
////                        channelResult.set(looperBean.getShipment_result2String());
//        curMode.set(looperHeatBean.getCur_mode2String());
//        if (looperHeatBean.getControl_state() != 0) {
//            heatChannleD.set(looperHeatBean.getChannelDStatus2String());
//            upDoorOpen.set(looperHeatBean.getUp_door_open_status2String());
//            upDoorClose.set(looperHeatBean.getUp_door_close_status2String());
//            downDoorOpen.set(looperHeatBean.getDown_door_open_status2String());
//            downDoorClose.set(looperHeatBean.getDown_door_close_status2String());
//            //更新数据库的故障
//            if (looperHeatBean.getCur_mode() == 2) {
//                updateChannelD(looperHeatBean);
//            }
//        }
//        copmressorS.set(looperHeatBean.getCopmressorS2String());
//        String ca = String.valueOf(looperHeatBean.getCopmressorA() / 10f);
//        copmressorA.set("压缩机当前电流值：" + ca + " A");
//        microwaveS.set(looperHeatBean.getMicrowaveS2String());
//        String ma = String.valueOf(looperHeatBean.getMicrowaveA() / 10f);
//        microwaveA.set("微波炉当前电流值：" + ma + " A");

        curTemp.set("温度：- -");
        curControlStatus.set("控制板状态：- -");
        shipmentChannelD.set("货道号：- -");
//        channelResult.set("出货结果：- -");
        curMode.set("当前模式：- -");
        heatChannleD.set("货道电机：- -");
        upDoorOpen.set("上门开门限位：- -");
        upDoorClose.set("上门关门限位：- -");
        downDoorOpen.set("下门开门限位：- -");
        downDoorClose.set("下门关门限位：- -");
        copmressorS.set("压缩机运行状态：- -");
        copmressorA.set("压缩机当前电流值：- -");
        microwaveS.set("微波炉运行状态：- -");
        microwaveA.set("微波炉当前电流值：- -");
    }


    @Override
    public void onResume() {
        super.onResume();
        AgreementManager.Companion.getInstance().setReceivedListener(new AgreementManager.onReceivedListener() {
            @Override
            public void OnListener(@NotNull String data) {
                if (data.substring(2, 4).equals("03")) {
                    //接受到的数据
                    looperHeatBean = new Bus_LooperHeatBean(data);
                    Log.i(TAG, "RxBus 接收到轮询数据" + looperHeatBean.getData());
                    if (looperHeatBean.getReal_Temp() == -40) {//216
                        curTemp.set("温度：探头断线");
                    } else if (looperHeatBean.getReal_Temp() == 90) {
                        curTemp.set("温度：探头短路");
                    } else {
                        curTemp.set("温度：" + looperHeatBean.getReal_Temp() + " °C");
                    }
                    curControlStatus.set(looperHeatBean.getControl_state2String());
                    shipmentChannelD.set("货道号：" + looperHeatBean.getCurrent_Channel());
//                        channelResult.set(looperBean.getShipment_result2String());
                    curMode.set(looperHeatBean.getCur_mode2String());
                    if (looperHeatBean.getControl_state() != 0) {
                        if (!singleDoorAction) {
                            SharedPreferenceUtil.saveData(SharePConstant.FAULT_DOOR, data);
                            upDoorOpen.set(looperHeatBean.getUp_door_open_status2String());
                            upDoorClose.set(looperHeatBean.getUp_door_close_status2String());
                            downDoorOpen.set(looperHeatBean.getDown_door_open_status2String());
                            downDoorClose.set(looperHeatBean.getDown_door_close_status2String());
//                            //更新数据库的故障
//                            if (looperHeatBean.getCur_mode() == 2) {
//                                updateChannelD(looperHeatBean);
//                            }
                        }
                        if (!singleShipmentAction) {
                            heatChannleD.set(looperHeatBean.getChannelDStatus2String());
                            //更新数据库的故障
                            if (looperHeatBean.getCur_mode() == 2) {
                                updateChannelD(looperHeatBean);
                            }
                        }


                    }
                    copmressorS.set(looperHeatBean.getCopmressorS2String());
                    String ca = String.valueOf(looperHeatBean.getCopmressorA() / 10f);
                    copmressorA.set("压缩机当前电流值：" + ca + " A");
                    microwaveS.set(looperHeatBean.getMicrowaveS2String());
                    String ma = String.valueOf(looperHeatBean.getMicrowaveA() / 10f);
                    microwaveA.set("微波炉当前电流值：" + ma + " A");
                    if (looperHeatBean.getControl_state() == 2) {
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
                        AgreementManager.Companion.getInstance().sendACK(1);
//                            }
//                        }, 500);
                    }
                } else if (data.substring(2, 4).equals("04")) {
                    //收到温度设置
                } else if (data.substring(2, 4).equals("05")) {
                    //收到启动电机
                } else if (data.substring(2, 4).equals("06")) {
                    //收到ACK 看看还需要继续出货不
                    //收到ACK后再操作
                    shipmentIndex++;
                    if (shipmentLists != null && shipmentIndex < shipmentLists.size()) {
                        //延遲400ms
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
                        AgreementManager.Companion.getInstance().runHeatShipment(2, shipmentLists.get(shipmentIndex), halfCirInt, 1, 0, 0);
//                            }
//                        }, 500);
                    } else {
                        AgreementManager.Companion.getInstance().startLoopCheckCabinet();
                        if (mwClear == 0) {
                            down_door_state = 1;
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    downDoorOnClickCommand.execute();
                                    mwClear = 1;
                                }
                            }, 500);
                        } else if (mwClear == 1) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtils.file("测试模式", "发送‘微波炉故障清除’命令");
                                    ToastUtils.showShort("发送‘微波炉故障清除’命令");
                                    AgreementManager.Companion.getInstance().sendMicrowaveClear();
                                    mwClear = -1;
                                }
                            }, 500);

                        }
                    }
                } else if (data.substring(2, 4).equals("07")) {
                    //收到微波设置
                }

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        AgreementManager.Companion.getInstance().setReceivedListener(null);
    }

    //    @Override
//    public void registerRxBus() {
//        super.registerRxBus();
//        mSubscription_Ack=RxBus.getDefault().toObservable(Bus_ACKBean.class).subscribe(new Consumer<Bus_ACKBean>() {
//            @Override
//            public void accept(Bus_ACKBean bus_ackBean) throws Exception {
//                //接受到ACK 看是否继续操作
//                //收到ACK后再操作
//                shipmentIndex++;
//                if (shipmentLists != null && shipmentIndex < shipmentLists.size()) {
//                    AgreementManager.Companion.getInstance().runHeatShipment(2, shipmentLists.get(shipmentIndex), 1, 0, 0);
//                } else {
//                    AgreementManager.Companion.getInstance().startLoopCheckCabinet();
//                    if (mwClear == 0) {
//                        down_door_state = 1;
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                downDoorOnClickCommand.execute();
//                                mwClear = 1;
//                            }
//                        }, 500);
//                    } else if (mwClear == 1) {
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                LogUtils.file("测试模式", "发送‘微波炉故障清除’命令");
//                                ToastUtils.showShort("发送‘微波炉故障清除’命令");
//                                AgreementManager.Companion.getInstance().sendMicrowaveClear();
//                                mwClear = -1;
//                            }
//                        }, 500);
//
//                    }
//                }
//
//            }
//        });
//        mSubscription_LoopBean = RxBus.getDefault().toObservable(Bus_LooperHeatBean.class)
//                .subscribe(new Consumer<Bus_LooperHeatBean>() {
//                    @Override
//                    public void accept(Bus_LooperHeatBean looperBean) throws Exception {
//                        //接受到的数据
//                        looperHeatBean = looperBean;
//                        LogUtils.file("RxBus 接收到轮询数据", looperBean.getData());
//                        Log.i(TAG, "RxBus 接收到轮询数据" + looperBean.getData());
//                        if (looperBean.getReal_Temp() == -40) {
//                            curTemp.set("温度：探头断线");
//                        } else if (looperBean.getReal_Temp() == 90) {
//                            curTemp.set("温度：探头短路");
//                        } else {
//                            curTemp.set("温度：" + looperBean.getReal_Temp() + " °C");
//                        }
//                        curControlStatus.set(looperBean.getControl_state2String());
//                        shipmentChannelD.set("货道号：" + looperBean.getCurrent_Channel());
////                        channelResult.set(looperBean.getShipment_result2String());
//                        curMode.set(looperBean.getCur_mode2String());
//                        if (looperBean.getControl_state() != 0) {
//                            heatChannleD.set(looperBean.getChannelDStatus2String());
//                            upDoorOpen.set(looperBean.getUp_door_open_status2String());
//                            upDoorClose.set(looperBean.getUp_door_close_status2String());
//                            downDoorOpen.set(looperBean.getDown_door_open_status2String());
//                            downDoorClose.set(looperBean.getDown_door_close_status2String());
//                            //更新数据库的故障
//                            if (looperHeatBean.getCur_mode() == 2) {
//                                updateChannelD(looperBean);
//                            }
//                        }
//                        copmressorS.set(looperBean.getCopmressorS2String());
//                        String ca = String.valueOf(looperBean.getCopmressorA() / 10f);
//                        copmressorA.set("压缩机当前电流值：" + ca + " A");
//                        microwaveS.set(looperBean.getMicrowaveS2String());
//                        String ma = String.valueOf(looperBean.getMicrowaveA() / 10f);
//                        microwaveA.set("微波炉当前电流值：" + ma + " A");
//                        if (looperBean.getControl_state() == 2) {
//                            AgreementManager.Companion.getInstance().sendACK(1);
//                        }
//                    }
//                });
//        //将订阅者加入管理站
//        RxSubscriptions.add(mSubscription_LoopBean);
//        RxSubscriptions.add(mSubscription_Ack);
//    }

    private void updateChannelD(Bus_LooperHeatBean looperBean) {
        //更新
        List list = DaoUtilsStore.getInstance().getShopManagerDBUtils().queryByQueryBuilder(ShopManagerDBDao.Properties.ChannleID.eq(looperBean.getCurrent_Channel()));
        if (list.size() > 0) {
            ShopManagerDB s = (ShopManagerDB) list.get(0);
            if (looperBean.getChannelDStatus() == 0 && s.getChannelFault().equals("1")) {
                Log.i("故障", "检测到正常 ,查询到数据库不正常");
                //检测到正常 ,查询到数据库不正常
                s.setChannelFault("0");
                DaoUtilsStore.getInstance().getShopManagerDBUtils().update(s);
                //添加
                String data;
                Gson gson = new Gson();
                if (s.getCombination().equals("1")) {
                    data = SharedPreferenceUtil.getStrData("comb1");
                } else {
                    data = SharedPreferenceUtil.getStrData("comb2");
                }
                Type listType = new TypeToken<LinkedList<String>>() {
                }.getType();
                LinkedList<String> ll = gson.fromJson(data, listType);
                for (int i = 0; i < Integer.valueOf(s.getStockNum()); i++) {
                    ll.add(s.getChannleID());
                }
                //baoc
                Gson gson2 = new Gson();
                String data2 = gson2.toJson(ll);
                if (s.getCombination().equals("1")) {
                    SharedPreferenceUtil.saveData("comb1", data2);
                } else {
                    SharedPreferenceUtil.saveData("comb2", data2);
                }
            } else if (looperBean.getChannelDStatus() == 1 || looperBean.getChannelDStatus() == 2) {
                if (s.getChannelFault().equals("0")) {
                    Log.i("故障", "检测到不正常 ,查询到数据库正常");
                    //检测到不正常 ,查询到数据库正常
                    s.setChannelFault("1");
                    DaoUtilsStore.getInstance().getShopManagerDBUtils().update(s);
                    //移除
                    String data;
                    Gson gson = new Gson();
                    if (s.getCombination().equals("1")) {
                        data = SharedPreferenceUtil.getStrData("comb1");
                    } else {
                        data = SharedPreferenceUtil.getStrData("comb2");
                    }
                    Type listType = new TypeToken<LinkedList<String>>() {
                    }.getType();
                    LinkedList<String> ll = gson.fromJson(data, listType);
                    for (int i = 0; i < Integer.valueOf(s.getStockNum()); i++) {
                        ll.remove(s.getChannleID());
                    }
                    //baoc
                    Gson gson2 = new Gson();
                    String data2 = gson2.toJson(ll);
                    if (s.getCombination().equals("1")) {
                        SharedPreferenceUtil.saveData("comb1", data2);
                    } else {
                        SharedPreferenceUtil.saveData("comb2", data2);
                    }
                }
            }
        }

    }


    @Override
    public void removeRxBus() {
        super.removeRxBus();
        //将订阅者从管理站中移除
//        RxSubscriptions.remove(mSubscription_LoopBean);
//        RxSubscriptions.remove(mSubscription_Ack);
    }
}
