package com.uroica.drinkmachine.ui.factorymode.parameter;
 
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.databinding.ActivityFactorymodeBinding;
import com.uroica.drinkmachine.databinding.ActivityParameterBinding;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;
import com.uroica.drinkmachine.util.serialport.SerialPortManager;

import me.goldze.mvvmhabit.base.BaseActivity;

public class ParameterActivity extends BaseActivity<ActivityParameterBinding, ParameterViewModel> implements AdapterView.OnItemSelectedListener {
    private String[] mDevices;
    private String[] mBaudrates;
//    private String[] mMachineTypes;
    private int mDeviceIndex;
    private int mBaudrateIndex;
//    private int cabinetNum;
//    private int mMachineIndex;
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
//        mSpinnerMachineType=binding.spinnerMachineType;
        initDevice();
        initSpinners();
//        AgreementManager.Companion.getInstance().putMachineBoardNum(Integer.valueOf(cabinetNum));
        initListen();
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
        //设备类型
//        mMachineTypes=getResources().getStringArray(R.array.machine_type);
        mDeviceIndex=SharedPreferenceUtil.getIntData(SharePConstant.PARAM_SERIALPORT_DEVICEINDEX,0);
        mBaudrateIndex=SharedPreferenceUtil.getIntData(SharePConstant.PARAM_SERIALPORT_BAUDRATEINDEX,0);
//        cabinetNum=SharedPreferenceUtil.getIntData(SharePConstant.PARAM_MACHINE_CABINET_NUM,1);
//        mMachineIndex=SharedPreferenceUtil.getIntData(SharePConstant.PARAM_MACHINE_TYPE_INDEX, Constant.MACHINE_TYPE_HEAT);
//        binding.etCabinetNum.setText(String.valueOf(cabinetNum));
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

//        ArrayAdapter<String> machineTypeAdapter =
//                new ArrayAdapter<String>(this, R.layout.spinner_default_item, mMachineTypes);
//        machineTypeAdapter.setDropDownViewResource(R.layout.spinner_item);
//        mSpinnerMachineType.setAdapter(machineTypeAdapter);
//        mSpinnerMachineType.setOnItemSelectedListener(this);

        mSpinnerDevices.setSelection(mDeviceIndex);
        mSpinnerBaudrate.setSelection(mBaudrateIndex);
//        mSpinnerMachineType.setSelection(mMachineIndex);

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
//        binding.btnSaveCabinet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String text = binding.etCabinetNum.getText().toString().trim();
//                if (TextUtils.isEmpty(text) ) {
//                    ToastUtils.showLong("无效数据");
//                    return;
//                }
//                if(mMachineIndex==Constant.MACHINE_TYPE_HEAT&& Integer.valueOf(text)>1){
//                    ToastUtils.showLong("热卖机类型的主副柜数量只能填1！");
//                    return;
//                }
//                SharedPreferenceUtil.saveData(SharePConstant.PARAM_MACHINE_CABINET_NUM,Integer.valueOf(text));
//                AgreementManager.Companion.getInstance().putMachineBoardNum(Integer.valueOf(text));
//                ToastUtils.showShort("保存主副柜数量成功！");
//            }
//        });
//        binding.btnSaveMachineType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferenceUtil.saveData(SharePConstant.PARAM_MACHINE_TYPE_INDEX,mMachineIndex);
//                ToastUtils.showShort("保存设备类型成功！");
//            }
//        });
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
//            case R.id.spinner_machine_type:
//                mMachineIndex = position;
//                if(position==0){
//                    binding.llCabinet.setVisibility(View.GONE);
//                }else{
//                    binding.llCabinet.setVisibility(View.VISIBLE);
//                }
//                break;
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
