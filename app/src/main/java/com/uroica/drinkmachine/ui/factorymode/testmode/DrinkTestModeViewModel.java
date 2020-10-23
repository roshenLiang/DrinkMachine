package com.uroica.drinkmachine.ui.factorymode.testmode;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.uroica.heartmachine.bean.rxbus.Bus_ACKBean;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.rxbus.Bus_LooperDrinkBean;
import com.uroica.drinkmachine.constant.Constant;
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
    private int curMainBoard = 1;
    private String TAG = "HeatTestModeViewModel";
    private List<Integer> shipmentLists;
    private int shipmentIndex = 0;
    public int temp_mode = 1;//0x00，常温；0x01，制冷；0x02，加热
    private Bus_LooperDrinkBean looperDrinkBean;

    public ObservableField<String> curTemp = new ObservableField<>("");//溫度
    public ObservableField<String> curControlStatus = new ObservableField<>("");//控制板状态
    public ObservableField<String> curChannel = new ObservableField<>("");//当前货道电机号
    public ObservableField<String> channleD = new ObservableField<>("");//货道电机
    public ObservableField<String> shipmentResult = new ObservableField<>("");//出货结果
    public ObservableField<String> vibrationState = new ObservableField<>("");//振动报警


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

    public int getCurMainBoard() {
        return curMainBoard;
    }

    public void setCurMainBoard(int curMainBoard) {
        this.curMainBoard = curMainBoard;
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


    //单货道出货
    public BindingCommand go2OnlyShipmentOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if(looperDrinkBean==null){
                ToastUtils.showShort("通讯存在问题");
                return;
            }
            if (looperDrinkBean.getControl_state() == 1 || looperDrinkBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            //判空处理
            if (channelId.get().equals("")) {
                ToastUtils.showShort("请输入货道号！");
                return;
            }
            if (Integer.valueOf(channelId.get()) > Constant.TOTAL_CHANNEL || Integer.valueOf(channelId.get()) < 0) {
                ToastUtils.showShort("请输入正确的货道号：1-70号货道！");
                return;
            }
            ToastUtils.showShort("发送‘出货’命令");
            LogUtils.file("测试模式", "发送‘单货道出货’命令");
            uc.hintKeyBoardEvent.call();
            shipmentLists = new ArrayList<>();
            shipmentIndex = 0;
            for (int i = 0; i < 1; i++) {
                shipmentLists.add(Integer.valueOf(channelId.get()));
            }
            AgreementManager.Companion.getInstance().runDrinkShipment(curMainBoard, halfCirInt, shipmentLists.get(shipmentIndex));
        }
    });

    //整层道出货
    public BindingCommand go2FloorShipmentOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if(looperDrinkBean==null){
                ToastUtils.showShort("通讯存在问题");
                return;
            }
            if (looperDrinkBean.getControl_state() == 1 || looperDrinkBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            //判空处理
            if (floorId.get().equals("")) {
                ToastUtils.showShort("请输入货道层数");
                return;
            }
            if (Integer.valueOf(floorId.get()) > 7 || Integer.valueOf(floorId.get()) < 0) {
                ToastUtils.showShort("请输入正确的货道层数 提示：1-7");
                return;
            }
            ToastUtils.showShort("发送‘整层出货’命令");
            LogUtils.file("测试模式", "发送‘整层出货’命令");
            uc.hintKeyBoardEvent.call();
            shipmentLists = new ArrayList<>();
            shipmentIndex = 0;
            for (int i = 10 * (Integer.valueOf(floorId.get()) - 1); i < 10 * Integer.valueOf(floorId.get()); i++) {
                if (i + 1 < Constant.TOTAL_CHANNEL + 1) {
                    Log.i("层数", "i=" + (i + 1));
                    shipmentLists.add(i + 1);
                }
            }
            AgreementManager.Companion.getInstance().runDrinkShipment(curMainBoard, halfCirInt, shipmentLists.get(shipmentIndex));
        }
    });

    //全部出货
    public BindingCommand go2AllShipmentOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if(looperDrinkBean==null){
                ToastUtils.showShort("通讯存在问题");
                return;
            }
            if (looperDrinkBean.getControl_state() == 1 || looperDrinkBean.getControl_state() == 2) {
                ToastUtils.showShort("控制板状态正忙");
                return;
            }
            ToastUtils.showShort("发送‘全部出货’命令");
            LogUtils.file("测试模式", "发送‘全部出货’命令");
            uc.hintKeyBoardEvent.call();
            shipmentLists = new ArrayList<>();
            shipmentIndex = 0;
            for (int i = 1; i < Constant.TOTAL_CHANNEL + 1; i++) {
                shipmentLists.add(i);
            }
            AgreementManager.Companion.getInstance().runDrinkShipment(curMainBoard, halfCirInt, shipmentLists.get(shipmentIndex));
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
            ToastUtils.showShort("发送‘设置温度’命令");
            LogUtils.file("测试模式", "发送‘设置温度’命令");
            uc.tempEvent.call();
//            uc.hintKeyBoardEvent.call();
            AgreementManager.Companion.getInstance().setTemp(1, temp_mode, temp.get());
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


    public void clearStatus() {
        curTemp.set("温度：- -");
        curControlStatus.set("控制板状态：- -");
        curChannel.set("货道号：- -");
        channleD.set("货道电机：- -");
        shipmentResult.set("出货结果：- -");
        vibrationState.set("振动情况：- -");
    }


    @Override
    public void onResume() {
        super.onResume();
        AgreementManager.Companion.getInstance().setReceivedListener(new AgreementManager.onReceivedListener() {
            @Override
            public void OnListener(@NotNull String data) {
                Log.i(TAG, "RxBus 接收到轮询数据" + data);
                if (data.substring(2, 4).equals("03")) {
                    looperDrinkBean = new Bus_LooperDrinkBean(data);
                    if (looperDrinkBean.getMainBoard() == curMainBoard) {
                        //接受到的数据
                        if (looperDrinkBean.getReal_Temp() == -40) {//216
                            curTemp.set("温度：探头断线");
                        } else if (looperDrinkBean.getReal_Temp() == 90) {
                            curTemp.set("温度：探头短路");
                        } else {
                            curTemp.set("温度：" + looperDrinkBean.getReal_Temp() + " °C");
                        }
                        curControlStatus.set(looperDrinkBean.getControl_state2String());
                        curChannel.set("货道号：" + looperDrinkBean.getCurrent_Channel());
                        channleD.set(looperDrinkBean.getResult_channel2String());
                        shipmentResult.set(looperDrinkBean.getResult_shipment2String());
                        vibrationState.set(looperDrinkBean.getVibration_state2String());
                        if (looperDrinkBean.getControl_state() != 0) {
                            //更新数据库的故障
                            updateChannelD(looperDrinkBean);
                        }
                        if (looperDrinkBean.getControl_state() == 2) {
                            AgreementManager.Companion.getInstance().sendACK(1);
                        }
                    }
                }
                else if (data.substring(2, 4).equals("06")) {
                    //收到ACK 看看还需要继续出货不
                    //收到ACK后再操作
                    shipmentIndex++;
                    if (shipmentLists != null && shipmentIndex < shipmentLists.size()) {
                        AgreementManager.Companion.getInstance().runDrinkShipment(curMainBoard, halfCirInt, shipmentLists.get(shipmentIndex));
                    } else {
                        AgreementManager.Companion.getInstance().sstartLoopCheckCabinet();
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        AgreementManager.Companion.getInstance().setReceivedListener(null);
    }


    private void updateChannelD(Bus_LooperDrinkBean looperBean) {
//        //更新
        List list = DaoUtilsStore.getInstance().getShopManagerDBUtils().queryByQueryBuilder(ShopManagerDBDao.Properties.ChannleID.eq(looperBean.getCurrent_Channel()));
        if (list.size() > 0) {
            ShopManagerDB s = (ShopManagerDB) list.get(0);
            if (looperBean.getResult_channel() == 0 && s.getChannelFault().equals("1")) {
                Log.i("故障", "检测到正常 ,查询到数据库不正常");
                //检测到正常 ,查询到数据库不正常
                s.setChannelFault("0");
                DaoUtilsStore.getInstance().getShopManagerDBUtils().update(s);
            } else if (looperBean.getResult_channel() == 1 || looperBean.getResult_channel() == 2) {
                if (s.getChannelFault().equals("0")) {
                    Log.i("故障", "检测到不正常 ,查询到数据库正常");
                    //检测到不正常 ,查询到数据库正常
                    s.setChannelFault("1");
                    DaoUtilsStore.getInstance().getShopManagerDBUtils().update(s);
                }
            }
        }
    }


    @Override
    public void removeRxBus() {
        super.removeRxBus();
    }
}
