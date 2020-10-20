package com.uroica.drinkmachine.ui.factorymode.testmode;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.adapter.TestModeCabinetAdapter;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.databinding.ActivityDrinktestmodeBinding;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;
import com.uroica.drinkmachine.view.Transformer;

import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager;
import me.goldze.mvvmhabit.base.BaseActivity;

public class DrinkTestModeActivity extends BaseActivity<ActivityDrinktestmodeBinding, DrinkTestModeViewModel> {
    RecyclerView ryCabinet;
    TextView tvCurCabinet;
    //安卓板的串口名
    String deviceAddress = "";
    //波特率
    String baudRate = "";
    int machine_type;
    //主副柜数量
    int cabinetNum=1;

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
        ryCabinet = binding.ryCabinet;
        tvCurCabinet = binding.tvCurCabinet;
        machine_type = SharedPreferenceUtil.getIntData(SharePConstant.PARAM_MACHINE_TYPE_INDEX);
        deviceAddress = SharedPreferenceUtil.getStrData(SharePConstant.PARAM_SERIALPORT_DEVICE);
        baudRate = SharedPreferenceUtil.getStrData(SharePConstant.PARAM_SERIALPORT_BAUDRATE);


        if ((deviceAddress.equals("")) && (baudRate.equals(""))) {
            deviceAddress = "沒设置";
            baudRate = "沒设置";
        }
        binding.tvSerialport.setText("串口名：" + deviceAddress + " 波特率：" + baudRate);
        //打开串口
        openSerialPortResult=  AgreementManager.Companion.getInstance().openSerial(this);
        cabinetNum=SharedPreferenceUtil.getIntData(SharePConstant.PARAM_MACHINE_CABINET_NUM,1);
        initAdapter();

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

        int lostgoodSwitch = SharedPreferenceUtil.getIntData(SharePConstant.LOSTGOOD_SWITCH, 1);//就是关
        if (lostgoodSwitch == 0) {
            binding.switchHalfcir.setChecked(false);
        } else {
            binding.switchHalfcir.setChecked(true);
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
        binding.switchHalfcir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ToastUtils.showShort("开");
                    SharedPreferenceUtil.saveData(SharePConstant.LOSTGOOD_SWITCH,1);
                } else {
                    ToastUtils.showShort("关");
                    SharedPreferenceUtil.saveData(SharePConstant.LOSTGOOD_SWITCH,0);
                }
            }
        });
    }

    private void initAdapter() {
        GalleryLayoutManager manager = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL);
        manager.attach(ryCabinet);
        //设置滑动缩放效果
        manager.setItemTransformer(new Transformer());
        ryCabinet.setAdapter(new TestModeCabinetAdapter(this, cabinetNum));
        manager.setOnItemSelectedListener(new GalleryLayoutManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(RecyclerView recyclerView, View view, int i) {
                viewModel.clearStatus();
                viewModel.setCurMainBoard(i+1);
                tvCurCabinet.setText("当前柜号(主板)为:" + (i + 1));
                //开启轮询
                if(openSerialPortResult){
                    AgreementManager.Companion.getInstance().putMachineBoardNum(cabinetNum);
                    AgreementManager.Companion.getInstance().stopLoopCheckCabinet();
                    AgreementManager.Companion.getInstance().startLoopCheckCabinet();
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
