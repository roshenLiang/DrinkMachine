package com.uroica.drinkmachine.ui.factorymode.parameter;
 
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.databinding.ActivityFactorymodeBinding;
import com.uroica.drinkmachine.databinding.ActivityParameterBinding;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.ui.MainActivity;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;
import com.uroica.drinkmachine.util.serialport.SerialPortManager;

import me.goldze.mvvmhabit.base.BaseActivity;

public class ParameterActivity extends BaseActivity<ActivityParameterBinding, ParameterViewModel> implements AdapterView.OnItemSelectedListener {
    private String[] mDevices;
    private String[] mBaudrates;
    private int mDeviceIndex;
    private int mBaudrateIndex;
    private int cabinetNum;
    SerialPortManager mSerialPortManager;
    Spinner mSpinnerDevices,mSpinnerBaudrate;//mSpinnerMachineType

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_parameter;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel ;
    }

    @Override
    public void initData() {
        getSupportActionBar().setTitle("设置参数");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BarUtils.setStatusBarVisibility(this,false);
        mSpinnerDevices=binding.spinnerDevices;
        mSpinnerBaudrate=binding.spinnerBaudrate;
        initDevice();
        initSpinners();
        initListen();
        initDiaLog();
    }



    private void initDevice(){
        SharedPreferenceUtil.initSharedPreferenc(this);
        mSerialPortManager = SerialPortManager.instance();
        // 设备
        mDevices = mSerialPortManager.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[] {"找不到串口设备"};
        }
        // 波特率
        mBaudrates = getResources().getStringArray(R.array.baudrates);
        mDeviceIndex=SharedPreferenceUtil.getIntData(SharePConstant.PARAM_SERIALPORT_DEVICEINDEX,0);
        mBaudrateIndex=SharedPreferenceUtil.getIntData(SharePConstant.PARAM_SERIALPORT_BAUDRATEINDEX,0);
        cabinetNum=SharedPreferenceUtil.getIntData(SharePConstant.PARAM_MACHINE_CABINET_NUM,1);
        binding.etCabinetNum.setText(String.valueOf(cabinetNum));
        AgreementManager.Companion.getInstance().putMachineBoardNum(Integer.valueOf(cabinetNum));
    }

    private void initSpinners() {
        ArrayAdapter<String> deviceAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_default_item, mDevices);
        deviceAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerDevices.setAdapter(deviceAdapter);
        mSpinnerDevices.setOnItemSelectedListener(this);

        ArrayAdapter<String> baudrateAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_default_item, mBaudrates);
        baudrateAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerBaudrate.setAdapter(baudrateAdapter);
        mSpinnerBaudrate.setOnItemSelectedListener(this);


        mSpinnerDevices.setSelection(mDeviceIndex);
        mSpinnerBaudrate.setSelection(mBaudrateIndex);

    }
    private void initListen() {
        binding.btnSaveSerialport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceUtil.saveData(SharePConstant.PARAM_SERIALPORT_DEVICEINDEX,mDeviceIndex);
                SharedPreferenceUtil.saveData(SharePConstant.PARAM_SERIALPORT_BAUDRATEINDEX,mBaudrateIndex);
                SharedPreferenceUtil.saveData(SharePConstant.PARAM_SERIALPORT_DEVICE,mDevices[mDeviceIndex]);
                SharedPreferenceUtil.saveData(SharePConstant.PARAM_SERIALPORT_BAUDRATE,mBaudrates[mBaudrateIndex]);
                ToastUtils.showShort("保存串口参数成功！");
            }
        });
        binding.btnSaveCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.etCabinetNum.getText().toString().trim();
                if (TextUtils.isEmpty(text) ) {
                    ToastUtils.showLong("请输入主副柜数量");
                    return;
                }
                //提示要重啓系统
                if(cabinetNum==Integer.valueOf(text)){
                    ToastUtils.showLong("主副柜数量没变化，已保存");
                }else{
                    showDialog();
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Spinner 选择监听
        switch (parent.getId()) {
            case R.id.spinner_devices:
                mDeviceIndex = position;
                break;
            case R.id.spinner_baudrate:
                mBaudrateIndex = position;
                break;
        }
    }

    AlertDialog dialog;
    /**
     * 两个按钮的 dialog
     */
    private void initDiaLog() {
        dialog = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher)
                .setMessage("更改主副柜数量需要重启系统").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferenceUtil.saveData(SharePConstant.PARAM_MACHINE_CABINET_NUM,Integer.valueOf(binding.etCabinetNum.getText().toString().trim()));
                        AgreementManager.Companion.getInstance().putMachineBoardNum(Integer.valueOf(binding.etCabinetNum.getText().toString().trim()));
                        ToastUtils.showShort("保存主副柜数量成功！");
                        Bundle b=new Bundle();
                        b.putBoolean("cabinetNumChange",true);
                        startActivity(MainActivity.class,b);
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
    }

    public void showDialog(){
        if(dialog!=null){
            dialog.show();
        }
    }
    public void dismissDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
