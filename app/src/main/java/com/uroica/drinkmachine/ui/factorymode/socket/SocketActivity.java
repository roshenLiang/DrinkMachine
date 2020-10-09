package com.uroica.drinkmachine.ui.factorymode.socket;
 
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.gson.Gson;
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
import com.uroica.drinkmachine.databinding.ActivitySocketBinding;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.ui.fragment.log.LogFragment;
import com.uroica.drinkmachine.util.ChangeTool;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import me.goldze.mvvmhabit.base.BaseActivity;

/*
 */
public class SocketActivity extends BaseActivity<ActivitySocketBinding, SocketViewModel> implements LoginStateCallback, MessageReceiverListener {
    private String deviceID;//00009af600ac7ade19d6
    Gson gson = new Gson();
    LogFragment logFragment;
    Timer timer;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_socket;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        getSupportActionBar().setTitle("后台测试");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AgreementManager.Companion.getInstance().closeSerialPort();
         deviceID=DeviceUtils.getAndroidID();
        deviceID= ChangeTool.codeAddOne(deviceID,20).toUpperCase();
        Log.i("dedede",deviceID.toUpperCase());
        BarUtils.setStatusBarVisibility(this, false);
        logFragment=new LogFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl, logFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.connectEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                connect();
            }
        });
        viewModel.uc.disconnectEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                close();
                if(timer!=null){
                    timer.cancel();
                    timer=null;
                }
            }
        });
        viewModel.uc.subscribeEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                subscribe();
            }
        });
        viewModel.uc.unsubscribeEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                unSubscribe();
            }
        });
        viewModel.uc.sendContentEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                Map<String, String> params = new HashMap<>();
                params.put("msg", viewModel.sendcontent.get());
                sendData(gson.toJson(params));
            }
        });
        viewModel.uc.sendRegisterEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                sendData("FAAF010A"+deviceID+"FE");
                //开启一个线程去跑
                if(timer!=null){
                    timer.cancel();
                    timer=null;
                }
                timer=new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        sendData("FAAF020102FE");
                    }
                },0,10000);
            }
        });


//        viewModel.uc.sendHeartEvent.observe(this, new Observer() {
//            @Override
//            public void onChanged(@Nullable Object o) {

//            }
//        });
        viewModel.uc.sendContentEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                sendData("FAAF03020101FE");
            }
        });

        viewModel.uc.hintKeyBoardEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                KeyboardUtils.hideSoftInput(SocketActivity.this);
            }
        });
    }

    public void connect() {
        Vector<String> addrs = new Vector<>();
        addrs.addElement(viewModel.address.get() + " " + viewModel.port.get());
        MqttParamOptions mqttParamOptions = new MqttParamOptions.Builder()
                .hasUserName(true)
                .hasPassword(true)
                .setUserName(viewModel.user.get())
                .setPassword(viewModel.password.get())
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
                new SubscribeMessage[]{new SubscribeMessage(viewModel.topic.get(), Qos.AT_MOST_ONCE)}, new SubscribeStateCallback() {
                    @Override
                    public void callbackSubscribeState(@NotNull SubscribeState state, @org.jetbrains.annotations.Nullable List<String> topicList) {

                    }
                }
        );

    }

    public void unSubscribe() {
        MqttClientFactory.INSTANCE.getMqttClient()
                .unsubscribe(new String[]{viewModel.topic.get()}, new UnsubscribeStateCallback() {
                    @Override
                    public void callbackUnsubscribeState(@NotNull UnsubscribeState state, @org.jetbrains.annotations.Nullable List<String> topicList) {

                    }
                });
    }

    public void sendData(String data) {
        MqttClientFactory.INSTANCE.getMqttClient().publishMsg(viewModel.topic.get(), data);
        logFragment.sendData(data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callbackLoginState(@NotNull LoginState state, @org.jetbrains.annotations.Nullable String errMsg) {
        if (state == LoginState.LoginSuccessful) {
            Log.d("roshen", "登录成功");
        } else if (state == LoginState.LoginFailure)
            Log.w("roshen", "登录失败：$errMsg");

}

    @Override
    public void onReceiverMessage(@NotNull String topic, @NotNull String msg) {
        //接受到的數據
        Log.i("roshen", "接受到的數據=" + msg);
        logFragment.receiveData(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }
}
