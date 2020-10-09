package com.uroica.drinkmachine.ui.sale;
 
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pudutech.mqtt.component.client.MqttClientFactory;
import com.pudutech.mqtt.component.client.bean.SubscribeMessage;
import com.pudutech.mqtt.component.client.callback.LoginStateCallback;
import com.pudutech.mqtt.component.client.callback.MessageReceiverListener;
import com.pudutech.mqtt.component.client.callback.SubscribeStateCallback;
import com.pudutech.mqtt.component.client.callback.UnsubscribeStateCallback;
import com.pudutech.mqtt.component.client.config.LoginState;
import com.pudutech.mqtt.component.client.config.MqttParamOptions;
import com.pudutech.mqtt.component.client.config.Qos;
import com.pudutech.mqtt.component.client.config.SubscribeState;
import com.pudutech.mqtt.component.client.config.UnsubscribeState;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.db.SaleRecordDB;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.bean.rxbus.Bus_ACKBean;
import com.uroica.drinkmachine.bean.rxbus.Bus_LooperHeatBean;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.databinding.ActivitySalespageBinding;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.gen.SaleRecordDBDao;
import com.uroica.drinkmachine.gen.ShopManagerDBDao;
import com.uroica.drinkmachine.gen.ShopModelDBDao;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.ui.fragment.ad.AdFragment;
import com.uroica.drinkmachine.ui.fragment.pay.PayFragment;
import com.uroica.drinkmachine.ui.fragment.shop.ShopFragment;
import com.uroica.drinkmachine.ui.login.LoginActivity;
import com.uroica.drinkmachine.util.ChangeTool;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import me.goldze.mvvmhabit.base.BaseActivity;

import static com.uroica.drinkmachine.constant.Constant.*;
/*
    目前这个为售货页面
    MQTT 串口的轮询指令
 */

public class SalesPageActivity extends BaseActivity<ActivitySalespageBinding, SalesPageViewModel> implements LoginStateCallback, MessageReceiverListener {
    private String deviceID;
    Timer timer;
    private List<Fragment> mFragments;

    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 1500;// 规定有效时间
    long[] mHits = new long[COUNTS];
    private int fragmentIndex = 0;//区分是小程序还是机器
    //销售记录
    private String orderID;  //订单号
//    private int from;// 1就是机器 2就是小程序
//    private long saleTime;//销售的时间
//    private ShopModelDB record_shop;//销售的商品信息
    private boolean isAck = false;//控制進入一次
    private Bus_LooperHeatBean bus_looperHeatBean;
    //与服务器轮询 故障
    private String CONTROL_STATE="00"; //00空闲 01忙
    private String FAULT_STRING = "00000000";
    private int heart_time;
    private boolean serialOpenResult=false;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_salespage;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        BarUtils.setStatusBarVisibility(this, false);
        deviceID = DeviceUtils.getAndroidID();
        deviceID = ChangeTool.codeAddOne(deviceID, 20).toUpperCase();
        binding.tvDeviceid.setText("设备号：" + deviceID);
        binding.tvDeviceid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MyApplication.instance.setMachineFault(true);
//                binding.tvFault.setVisibility(View.VISIBLE);
                continuousClick(COUNTS, DURATION);
            }
        });
        orderID = "";
        binding.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MyApplication.instance.setMachineFault(false);
//                binding.tvFault.setVisibility(View.GONE);
//                ToastUtils.showShort(MQTT_SHIPMENT + "UROICA_3040506070809" + "00" + MQTT_END);
//                sendData(MQTT_SHIPMENT + "UROICA_3040506070809" + "00" + MQTT_END);
            }
        });
        mFragments = new ArrayList<>();
        mFragments.add(new ShopFragment());
        mFragments.add(new PayFragment());
        //默认选中第一个
        commitAllowingStateLoss(0);
//        serialOpenResult = AgreementManager.Companion.getInstance().openSerial(this);
//        if (!serialOpenResult) {
//                    binding.llFault.setVisibility(View.VISIBLE);
//                    binding.tvFault.setText("串口打开失败！请设置串口参数");
//        }
//        connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        serialOpenResult = AgreementManager.Companion.getInstance().openSerial(this);
        if (!serialOpenResult) {
            binding.llFault.setVisibility(View.VISIBLE);
            binding.tvFault.setText("串口打开失败！请设置串口参数");
        }
        connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AgreementManager.Companion.getInstance().stopLoopCheckCabinet();
        AgreementManager.Companion.getInstance().closeSerialPort();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        unSubscribe();
        close();
    }

    public void commitAllowingStateLoss(int position) {
        fragmentIndex = position;
        hideAllFragment();
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = mFragments.get(position);
        transaction.add(R.id.fl, currentFragment, position + "");
        transaction.commitAllowingStateLoss();
    }

    public void commitAllowingStateLoss(int position, ShopModelDB dataBean, boolean fromMachine) {
        fragmentIndex = position;
        hideAllFragment();
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = mFragments.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ShopModelDB", dataBean);//这里的values就是我们要传的值
        bundle.putBoolean("fromMachine", fromMachine);
        currentFragment.setArguments(bundle);
        transaction.add(R.id.fl, currentFragment, position + "");
        transaction.commitAllowingStateLoss();
    }




    private  String tempStringLog="";
    public void sendData(String data) {
        if(!tempStringLog.equals(data)){
            if (data.startsWith("FAAF01")){
                LogUtils.file("云端数据","注册包 发送= "+data);
            }else if(data.startsWith("FAAF000D")){
                LogUtils.file("云端数据","收货确认 发送= "+data);
            }else if(data.startsWith("FAAF0205")){
                LogUtils.file("云端数据","心跳包 发送= "+data);
            }else{
                LogUtils.file("云端数据","发送= "+data);
            }


            tempStringLog=data;
        }
        MqttClientFactory.INSTANCE.getMqttClient().publishMsg(MQTT_TOPIC + deviceID + MQTT_REPLY, data);
    }


    @Override
    public void callbackLoginState(@NotNull LoginState state, @Nullable String errMsg) {
        if (state.equals(LoginState.LoginSuccessful)) {
            Log.d("roshen", "登录成功");
            subscribe();
            startMqtt();
        } else if (state.equals(LoginState.LoginFailure)) {
            Log.w("roshen", "登录失败：$errMsg");
        }
    }


    @Override
    public void onReceiverMessage(@NotNull String topic, @NotNull String msg) {
        Log.i("roshen", "接收到服务器信息:" + msg);
        LogUtils.file("云端数据","接收到的数据= "+msg);
        if (msg.startsWith("FAAF00")) {
            registrationBusiness(msg);
        }
        if (msg.startsWith("FAAF03")) {
            shipmentBusiness(msg);
        }
    }
    void updateAd(){
       FragmentManager fragmentManager= getSupportFragmentManager();
        AdFragment adFragment = (AdFragment) fragmentManager.findFragmentById(R.id.fm_adfragment);
        adFragment.updateAd();
    }

    void shipmentBusiness(@NotNull String msg) {
        long saleTime = System.currentTimeMillis();
        String  orderIDTemp = msg.substring(12,32);
        String pid = msg.substring(32, 42);
        int from= Integer.valueOf(msg.substring(10, 12));
        ShopModelDB record_shop = DaoUtilsStore.getInstance().getShopDaoUtils().queryByQueryBuilder(ShopModelDBDao.Properties.ProductID.eq(pid)).get(0);
        //保存订单信息，目前默认出货状态为失败
        SaleRecordDB saleRecordDB = new SaleRecordDB(orderIDTemp, saleTime, from, record_shop, 0);
        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(saleRecordDB);

        //机器在忙碌的时候 拒绝
        if (bus_looperHeatBean.getControl_state() != 0) {
            sendData(MQTT_SHIPMENT + orderID + "00" + MQTT_END);
            ToastUtils.showLong("机器正在忙，请稍后再来");
            return;
        }
        //机器在故障的时候 拒绝
        if (FaultisSendAck) {
            sendData(MQTT_SHIPMENT + orderID + "00" + MQTT_END);
            ToastUtils.showLong("机器故障，无法出货。");
            return;
        }

        //判断货存是否充足
        List<ShopManagerDB> s = DaoUtilsStore.getInstance().getShopManagerDBUtils().queryByQueryBuilder(ShopManagerDBDao.Properties.ProductID.eq(pid));
        String  combString = s.get(0).getCombination();
        String data;
        Gson gson = new Gson();
        if (combString.equals("1")) {
            data = SharedPreferenceUtil.getStrData("comb1");
        } else {
            data = SharedPreferenceUtil.getStrData("comb2");
        }
        Type listType = new TypeToken<LinkedList<String>>() {
        }.getType();
        List<String> combLinkedString = gson.fromJson(data, listType);
        if(combLinkedString==null||combLinkedString.size()<1){
            sendData(MQTT_SHIPMENT + orderID + "00" + MQTT_END);
            ToastUtils.showLong("该商品货存不足！出货失败");
            return;
        }

        //替换 防止覆盖订单号
        orderID=orderIDTemp;

        //FAAF03001004202007280928130939770000012345BB
        isAck = false;
        //还没处理 通过from去区别
        if (fragmentIndex == 1) {
            //在支付页
        } else {
            //不在支付页
            commitAllowingStateLoss(1, record_shop, false);
        }
        PayFragment currentFragment = (PayFragment) mFragments.get(fragmentIndex);
        currentFragment.sendShipment();
    }

    void registrationBusiness(@NotNull String msg) {
        String r = msg.substring(10, 12);
        if (r.equals("00")) {
//                设备未注册
            binding.tvFault.setText("设备未注册");
            binding.llFault.setVisibility(View.VISIBLE);
        } else if (r.equals("02")) {
//              服务期已过
            binding.tvFault.setText("服务期已过");
            binding.llFault.setVisibility(View.VISIBLE);
        } else {
            SharedPreferenceUtil.initSharedPreferenc(this);
            heart_time = SharedPreferenceUtil.getIntData(SharePConstant.HEART_TIME, 30);
            int mode = SharedPreferenceUtil.getIntData(SharePConstant.TEMP_MODE, 1);
            int num = SharedPreferenceUtil.getIntData(SharePConstant.TEMP_NUM, 4);
            if(serialOpenResult){
                //首次查询主柜状态 轮询
//                AgreementManager.Companion.getInstance().checkCabinet(1);
//                AgreementManager.Companion.getInstance().startLoopCheckCabinet();
                AgreementManager.Companion.getInstance().setTemp(1, mode, String.valueOf(num));

            }


            //开启心跳
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendData(MQTT_HEART +CONTROL_STATE+ ChangeTool.codeAddOne(Integer.toHexString(ChangeTool.bitToByte(FAULT_STRING)), 2) + "0000" + MQTT_END);
                }
            }, 0, heart_time * 1000);
        }
    }

    boolean FaultisSendAck = false;

    @Override
    public void initViewObservable() {
        viewModel.uc.loop.observe(this, new Observer<Bus_LooperHeatBean>() {
            @Override
            public void onChanged(@androidx.annotation.Nullable Bus_LooperHeatBean looperHeatBean) {
                bus_looperHeatBean = looperHeatBean;
                if(looperHeatBean.getControl_state()==0&&CONTROL_STATE.equals("01")){
                    //机器空闲
                    CONTROL_STATE="00";
                    //马上发送心跳给服务器
                    sendData(MQTT_HEART +CONTROL_STATE+ "01"+ChangeTool.codeAddOne(Integer.toHexString(ChangeTool.bitToByte(FAULT_STRING)), 2) + "00" + MQTT_END);
                }else if((looperHeatBean.getControl_state()==1||looperHeatBean.getControl_state()==2)&&CONTROL_STATE.equals("00")){
                    CONTROL_STATE="01";
                    sendData(MQTT_HEART +CONTROL_STATE+ "01"+ ChangeTool.codeAddOne(Integer.toHexString(ChangeTool.bitToByte(FAULT_STRING)), 2) + "00" + MQTT_END);
                }
                //故障处理
                initFault(looperHeatBean);
                if (looperHeatBean.getControl_state() == 2 ) {
//                if (looperHeatBean.getControl_state() == 2 && !isAck) {
                    //这里会出现多次 看看是否需要修复
                    if (!FaultisSendAck) {
                        AgreementManager.Companion.getInstance().sendACK(1);
                    }
                    //根据订单 id改变出货状态
                    Log.i("qqqqq", "orderID=" + orderID);
                    if (!orderID.equals("")&& !isAck) {
                        SaleRecordDB s = null;
                        List recordDBS = DaoUtilsStore.getInstance().getSaleRecordDBUtils().queryByQueryBuilder(SaleRecordDBDao.Properties.OrderID.eq(orderID));
                        if (recordDBS.size() > 0) {
                            s = (SaleRecordDB) recordDBS.get(0);
                        }
                        if (looperHeatBean.getShipment_result() == 0) {
                            //出货成功
                            sendData(MQTT_SHIPMENT + orderID + "01" + MQTT_END);
                            if (s != null) {
                                s.setShipment_status(1);
                                DaoUtilsStore.getInstance().getSaleRecordDBUtils().update(s);
                            }
                        } else {
                            //出货失败
                            sendData(MQTT_SHIPMENT + orderID + "00" + MQTT_END);
                            if (s != null) {
                                s.setShipment_status(0);
                                DaoUtilsStore.getInstance().getSaleRecordDBUtils().update(s);
                            }
                        }
                        orderID = "";
                        isAck = true;
                    }
                }
            }
        });
        viewModel.uc.ack.observe(this, new Observer<Bus_ACKBean>() {
            @Override
            public void onChanged(Bus_ACKBean bus_ackBean) {
                AgreementManager.Companion.getInstance().sstartLoopCheckCabinet();
            }
        });
    }

    private void initFault(@androidx.annotation.Nullable Bus_LooperHeatBean looperHeatBean) {
        if (looperHeatBean.getCopmressorS() != 0 || looperHeatBean.getMicrowaveS() != 0
                || looperHeatBean.getUp_door_close_status() != 0 || looperHeatBean.getUp_door_open_status() != 0
                || looperHeatBean.getDown_door_close_status() != 0 || looperHeatBean.getDown_door_open_status() != 0) {
            if (looperHeatBean.getCopmressorS() != 0) {
                setFault("1", 2, false);
                binding.tvFault.setText("压缩机故障");
                FaultisSendAck = true;
                binding.llFault.setVisibility(View.VISIBLE);
            }
            if (looperHeatBean.getMicrowaveS() != 0) {
                setFault("1", 1, false);
                binding.tvFault.setText("微波炉故障");
                FaultisSendAck = true;
                binding.llFault.setVisibility(View.VISIBLE);
                //库存要减少

            }
            if (looperHeatBean.getUp_door_close_status() != 0) {
                setFault("1", 5, false);
                binding.tvFault.setText("上门关故障");
                FaultisSendAck = true;
                binding.llFault.setVisibility(View.VISIBLE);
            }
            if (looperHeatBean.getUp_door_open_status() != 0) {
                setFault("1", 6, false);
                binding.tvFault.setText("上门开故障");
                FaultisSendAck = true;
                binding.llFault.setVisibility(View.VISIBLE);
            }
            if (looperHeatBean.getDown_door_close_status() != 0) {
                setFault("1", 3, false);
                binding.tvFault.setText("下门关故障");
                FaultisSendAck = true;
                binding.llFault.setVisibility(View.VISIBLE);
            }
            if (looperHeatBean.getDown_door_open_status() != 0) {
                setFault("1", 4, false);
                binding.tvFault.setText("下门开故障");
                FaultisSendAck = true;
                binding.llFault.setVisibility(View.VISIBLE);
            }

        } else {
            setFault("0", 0, true);
            binding.llFault.setVisibility(View.GONE);
            FaultisSendAck = false;
        }

        if (looperHeatBean.getReal_Temp() == -40||looperHeatBean.getReal_Temp() == 90) {
            setFault("1", 0, false);
            binding.tvFault.setText("温度故障");
            FaultisSendAck = true;
            binding.llFault.setVisibility(View.VISIBLE);
        }
    }


    private void continuousClick(int count, long time) {
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            startActivity(LoginActivity.class);
            finish();
        }
    }


    /*
        0沒問題 1故障
     */
    public void setFault(String isFault, int index, boolean isNoPro) {
        if (isNoPro) {
            FAULT_STRING = "00000000";
        } else {
            String start = FAULT_STRING.substring(0, index);
            String end = FAULT_STRING.substring(index+1, 8);
            FAULT_STRING = start + isFault + end;
        }

    }

    //隐藏所有Fragment
    private void hideAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (currentFragment != null) {
                transaction.remove(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }


    private void startMqtt() {
        sendData(MQTT_REGISTER + deviceID + MQTT_END);
    }

    public void connect() {
        Vector<String> addrs = new Vector<>();
        addrs.addElement(MQTT_IP + " " + MQTT_PORT);
        MqttParamOptions mqttParamOptions = new MqttParamOptions.Builder()
                .hasUserName(true)
                .hasPassword(true)
                .setUserName(MQTT_USER)
                .setPassword(MQTT_PASSWORD)
                .setAddrs(addrs)
                .setLoginStateCallback(this)
                .setMessageReceiverListener(this)
                .build();
        MqttClientFactory.INSTANCE.getMqttClient().init(this, mqttParamOptions).connect();
    }

    public void close() {
        MqttClientFactory.INSTANCE.getMqttClient().close();
    }

    public void subscribe() {
        MqttClientFactory.INSTANCE.getMqttClient().subscribe(
                new SubscribeMessage[]{new SubscribeMessage(MQTT_TOPIC + deviceID + MQTT_CTRL, Qos.AT_MOST_ONCE)}, new SubscribeStateCallback() {
                    @Override
                    public void callbackSubscribeState(@NotNull SubscribeState state, @org.jetbrains.annotations.Nullable List<String> topicList) {

                    }
                }
        );

    }

    public void unSubscribe() {
        MqttClientFactory.INSTANCE.getMqttClient()
                .unsubscribe(new String[]{MQTT_TOPIC + deviceID + MQTT_CTRL}, new UnsubscribeStateCallback() {
                    @Override
                    public void callbackUnsubscribeState(@NotNull UnsubscribeState state, @org.jetbrains.annotations.Nullable List<String> topicList) {

                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        AgreementManager.Companion.getInstance().stopLoopCheckCabinet();
//        AgreementManager.Companion.getInstance().closeSerialPort();
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//        unSubscribe();
//        close();

    }
}
