package com.uroica.drinkmachine.ui.factorymode.socket;
 
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/**
 *
 */

public class SocketViewModel extends BaseViewModel  {
    public String deviceID="12345";
//    public String defaultAddress="193.112.9.99";
//    public String defaultPort="61613";
//    public String defaultUser="admin";
//    public String defaultPassword="Cxl26yang22";
//    public String defaultTopic="UROICA/SaleBox/12345";//12345为机器号
//    public String defaultsendcontent="UROICA/SaleBox/12345";//12345为机器号

    public ObservableField<String> address = new ObservableField<>("193.112.9.99");
    public ObservableField<String> port = new ObservableField<>("61613");
    public ObservableField<String> user = new ObservableField<>("admin");
    public ObservableField<String> password = new ObservableField<>("Cxl26yang22");
    public ObservableField<String> topic = new ObservableField<>("UROICA/SaleBox/12345");
    public ObservableField<String> sendcontent = new ObservableField<>("hello for android");
    public ObservableField<String> heatTime = new ObservableField<>("10");

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //连接
        public SingleLiveEvent connectEvent = new SingleLiveEvent<>();
        public SingleLiveEvent disconnectEvent = new SingleLiveEvent<>();
        public SingleLiveEvent subscribeEvent = new SingleLiveEvent<>();
        public SingleLiveEvent unsubscribeEvent = new SingleLiveEvent<>();
        public SingleLiveEvent sendContentEvent = new SingleLiveEvent<>();
        public SingleLiveEvent sendRegisterEvent = new SingleLiveEvent<>();
//        public SingleLiveEvent sendHeartEvent = new SingleLiveEvent<>();
//        public SingleLiveEvent sendShipmentStautsEvent = new SingleLiveEvent<>();
        //发送测试 隐藏键盘
        public SingleLiveEvent hintKeyBoardEvent = new SingleLiveEvent<>();
    }


    public SocketViewModel(@NonNull Application application) {
        super(application);
//        address.set(defaultAddress);
//        port.set(defaultPort);
//        user.set(defaultUser);
//        password.set(defaultPassword);
//        topic.set(defaultTopic);
    }

    public BindingCommand subscribeOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.subscribeEvent.call();
            uc.hintKeyBoardEvent.call();
        }


    });
    public BindingCommand unSubscribeOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.unsubscribeEvent.call();
            uc.hintKeyBoardEvent.call();
        }
    });
    public BindingCommand connectOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.connectEvent.call();
            uc.hintKeyBoardEvent.call();
        }
    });
    public BindingCommand disconnectOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.disconnectEvent.call();
            uc.hintKeyBoardEvent.call();
        }
    });
    public BindingCommand sendOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.sendContentEvent.call();
            uc.hintKeyBoardEvent.call();
        }
    });
    public BindingCommand sendRegisterOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.sendRegisterEvent.call();
            uc.hintKeyBoardEvent.call();
        }
    });
//    public BindingCommand sendHeartOnClickCommand = new BindingCommand(new BindingAction() {
//        @Override
//        public void call() {
//            uc.sendHeartEvent.call();
//            uc.hintKeyBoardEvent.call();
//        }
//    });
//    public BindingCommand sendShipmentStatusOnClickCommand = new BindingCommand(new BindingAction() {
//        @Override
//        public void call() {
//            uc.sendShipmentStautsEvent.call();
//            uc.hintKeyBoardEvent.call();
//        }
//    });



}
