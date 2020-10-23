package com.uroica.drinkmachine.ui.sale;
 
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.uroica.drinkmachine.bean.rxbus.Bus_ACKBean;
import com.uroica.drinkmachine.bean.rxbus.Bus_LooperDrinkBean;

import java.util.Date;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/**
 *
 */

public class SalesPageViewModel extends BaseViewModel {

    private String TAG = "SalesPageViewModel";
    private boolean isVisible = true;
    //订阅者
    private Disposable mSubscription;
    private Disposable mSubscription2;
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent loop = new SingleLiveEvent<>();
        public SingleLiveEvent ack = new SingleLiveEvent<>();
    }

    public SalesPageViewModel(@NonNull Application application) {
        super(application);
        new TimeThread().start();
    }

    public ObservableField<String> tvTime = new ObservableField<>("");

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(Bus_LooperDrinkBean.class)
                .subscribe(new Consumer<Bus_LooperDrinkBean>() {
                    @Override
                    public void accept(Bus_LooperDrinkBean looperBean) throws Exception {
                        uc.loop.postValue(looperBean);
                    }
                });
        mSubscription2 = RxBus.getDefault().toObservable(Bus_ACKBean.class)
                .subscribe(new Consumer<Bus_ACKBean>() {
                    @Override
                    public void accept(Bus_ACKBean looperBean) throws Exception {
                        uc.ack.postValue(looperBean);
                    }
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
        RxSubscriptions.add(mSubscription2);
    }


    @Override
    public void removeRxBus() {
        super.removeRxBus();
        //将订阅者从管理站中移除
        RxSubscriptions.remove(mSubscription);
        RxSubscriptions.remove(mSubscription2);
    }

    int MSG_ONE = 1;
    long currentTimeMillis;
    Date mDate;

    /**
     * 时间变化handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //通过消息的内容msg.what  分别更新ui
            if (msg.what == MSG_ONE) {
                //获取到系统当前时间 long类型
                currentTimeMillis = System.currentTimeMillis();
                //将long类型的时间转换成日历格式
                mDate = new Date(currentTimeMillis);
                CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd  HH:mm:ss", mDate);//时间显示格式
                tvTime.set(sysTimeStr.toString()); //更新时间
            }
        }
    };

    /**
     * 开启一个线程，每个一秒钟更新时间
     */
    public class TimeThread extends Thread {
        //重写run方法
        @Override
        public void run() {
            super.run();
            do {
                try {
                    //每隔一秒 发送一次消息
                    Thread.sleep(1000);
                    Message msg = new Message();
                    //消息内容 为MSG_ONE
                    msg.what = MSG_ONE;
                    //发送
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }


}
