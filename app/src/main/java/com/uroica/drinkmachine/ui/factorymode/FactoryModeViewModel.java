package com.uroica.drinkmachine.ui.factorymode;
  
import android.app.Application;

import androidx.annotation.NonNull;

import com.uroica.drinkmachine.ui.factorymode.parameter.ParameterActivity;
import com.uroica.drinkmachine.ui.factorymode.salerecord.SaleRecordActivity;
import com.uroica.drinkmachine.ui.factorymode.shopmanager.ShopManagerActivity;
import com.uroica.drinkmachine.ui.factorymode.socket.SocketActivity;
import com.uroica.drinkmachine.ui.factorymode.serialport.SerialPortActivity;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/**
 *
 */

public class FactoryModeViewModel extends BaseViewModel {
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //连接
        public SingleLiveEvent testEvent = new SingleLiveEvent<>();
    }

    public FactoryModeViewModel(@NonNull Application application) {
        super(application);
    }

    public BindingCommand setParameterOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(ParameterActivity.class);
        }
    });
    public BindingCommand testModeOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.testEvent.call();
        }
    });
    public BindingCommand shopmanagerOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(ShopManagerActivity.class);
        }
    });
    public BindingCommand salerecordOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(SaleRecordActivity.class);
        }
    });
    public BindingCommand serialportOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(SerialPortActivity.class);
        }
    });
    public BindingCommand mqttOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(SocketActivity.class);
        }
    });


}
