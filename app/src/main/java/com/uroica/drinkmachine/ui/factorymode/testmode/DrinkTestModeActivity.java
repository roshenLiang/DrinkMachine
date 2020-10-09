package com.uroica.drinkmachine.ui.factorymode.testmode;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.databinding.ActivityDrinktestmodeBinding;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class DrinkTestModeActivity extends BaseActivity<ActivityDrinktestmodeBinding, DrinkTestModeViewModel> {
    //安卓板的串口名
    String deviceAddress = "";
    //波特率
    String baudRate = "";
    int machine_type;

    boolean openSerialPortResult=false;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_drinktestmode;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        getSupportActionBar().setTitle("饮料机测试模式");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BarUtils.setStatusBarVisibility(this, false);
        SharedPreferenceUtil.initSharedPreferenc(this);
        machine_type = SharedPreferenceUtil.getIntData(SharePConstant.PARAM_MACHINE_TYPE_INDEX);
        deviceAddress = SharedPreferenceUtil.getStrData(SharePConstant.PARAM_SERIALPORT_DEVICE);
        baudRate = SharedPreferenceUtil.getStrData(SharePConstant.PARAM_SERIALPORT_BAUDRATE);

        String faultdoor=SharedPreferenceUtil.getStrData(SharePConstant.FAULT_DOOR);
        String faultshipment=SharedPreferenceUtil.getStrData(SharePConstant.FAULT_SHIPMENT);
        viewModel.setFaultDoor(faultdoor);
        viewModel.setFaultShipment(faultshipment);

        if ((deviceAddress.equals("")) && (baudRate.equals(""))) {
            deviceAddress = "沒设置";
            baudRate = "沒设置";
        }
        binding.tvSerialport.setText("串口名：" + deviceAddress + " 波特率：" + baudRate);
        //打开串口
        openSerialPortResult=  AgreementManager.Companion.getInstance().openSerial(this);
//        if(openSerialPortResult){
//            //首次查询主柜状态 轮询
//            AgreementManager.Companion.getInstance().checkCabinet(1);
//            AgreementManager.Companion.getInstance().startLoopCheckCabinet();
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                readTemp();
            }
        }, 500);
        binding.c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(DrinkTestModeActivity.this);
            }
        });
        int logSwitch = SharedPreferenceUtil.getIntData(SharePConstant.LOG_SWITCH, 1);//就是关
        if (logSwitch == 0) {
            binding.switchLog.setChecked(false);
        } else {
            binding.switchLog.setChecked(true);
        }
        binding.switchLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ToastUtils.showShort("开");
                    SharedPreferenceUtil.saveData(SharePConstant.LOG_SWITCH,1);
                } else {
                    ToastUtils.showShort("关");
                    SharedPreferenceUtil.saveData(SharePConstant.LOG_SWITCH,0);
                }
            }
        });
    }

    private void readTemp() {
        int mode = SharedPreferenceUtil.getIntData(SharePConstant.TEMP_MODE, 1);
        int num = SharedPreferenceUtil.getIntData(SharePConstant.TEMP_NUM, 4);
        int heart = SharedPreferenceUtil.getIntData(SharePConstant.HEART_TIME, 30);
        if (mode == 1) {
            binding.rgTempMode.check(binding.rbCold.getId());
        } else if (mode == 0) {
            binding.rgTempMode.check(binding.rbNormal.getId());
        } else {
            binding.rgTempMode.check(binding.rbHot.getId());
        }
        binding.etHeart.setText(String.valueOf(heart));
        binding.etTemp.setText(String.valueOf(num));
        if(openSerialPortResult) {
            AgreementManager.Companion.getInstance().setTemp(1, mode, String.valueOf(num));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
    public void initViewObservable() {
        viewModel.uc.hintKeyBoardEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                KeyboardUtils.hideSoftInput(DrinkTestModeActivity.this);
            }
        });
        viewModel.uc.tempEvent.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                KeyboardUtils.hideSoftInput(DrinkTestModeActivity.this);
                SharedPreferenceUtil.saveData(SharePConstant.TEMP_MODE, viewModel.temp_mode);
                SharedPreferenceUtil.saveData(SharePConstant.TEMP_NUM, Integer.valueOf(viewModel.temp.get()));
            }
        });
        viewModel.uc.heartEvent.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                KeyboardUtils.hideSoftInput(DrinkTestModeActivity.this);
                SharedPreferenceUtil.saveData(SharePConstant.HEART_TIME, Integer.valueOf(viewModel.heart.get()));
            }
        });
        viewModel.uc.volumeAddEvent.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
//                currentVolume++;
//                Log.i("roshen","音量"+currentVolume);
//                audio.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                ToastUtils.showShort("音量增加！");
            }
        });
        viewModel.uc.volumePlusEvent.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
//                currentVolume--;
//                Log.i("roshen","音量"+currentVolume);
//                audio.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                ToastUtils.showShort("音量减少！");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AgreementManager.Companion.getInstance().stopLoopCheckCabinet();
        AgreementManager.Companion.getInstance().closeSerialPort();
    }
}
