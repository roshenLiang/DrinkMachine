package com.uroica.drinkmachine.ui.fragment.pay;
 
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class PayViewModel extends BaseViewModel {
    private String TAG="PayViewModel";
    private boolean isVisible=true;
    //订阅者
//    private Disposable mSubscription_LoopBean;
    public ObservableField<String> info = new ObservableField<>("扫一扫付款");
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public PayViewModel(@NonNull Application application) {
        super(application);
    }

    public class UIChangeObservable {
        //发送测试 隐藏键盘
        public SingleLiveEvent go2ShopFragEvent = new SingleLiveEvent<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible=true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible=false;
    }

//    @Override
//    public void registerRxBus() {
//        super.registerRxBus();
//        mSubscription_LoopBean = RxBus.getDefault().toObservable(Bus_LooperHeatBean.class)
//                .subscribe(new Consumer<Bus_LooperHeatBean>() {
//                    @Override
//                    public void accept(Bus_LooperHeatBean looperBean) throws Exception {
//                        //接受到的数据
//                        if(isVisible){
////                            Log.i(TAG,"RxBus 接收到轮询数据"+looperBean.getData());
//                            if (looperBean.getControl_state() == 1) {
////                                出貨成功
//                                if(info!=null)
//                                info.set("正在出货...");
//                            }else if(looperBean.getControl_state() == 2){
//                                if(info!=null)
//                                info.set("出货完成！");
//                                AgreementManager.Companion.getInstance().sendACK(1);
//                                if(looperBean.getShipment_result()==0)
//                                    uc.go2ShopFragEvent.postValue(true);
//                                else
//                                    uc.go2ShopFragEvent.postValue(false);
//                                uc.go2ShopFragEvent.call2();
//                            }
//                        }
//
//                    }
//                });
//        //将订阅者加入管理站
//        RxSubscriptions.add(mSubscription_LoopBean);
//    }
//
//    @Override
//    public void removeRxBus() {
//        super.removeRxBus();
//        //将订阅者从管理站中移除
//        RxSubscriptions.remove(mSubscription_LoopBean);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        removeRxBus();
//    }
}
