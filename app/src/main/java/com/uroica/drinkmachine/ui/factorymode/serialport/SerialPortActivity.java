package com.uroica.drinkmachine.ui.factorymode.serialport;
 
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.databinding.ActivitySerialportBinding;
import com.uroica.drinkmachine.ui.fragment.log.LogFragment;
import com.uroica.drinkmachine.util.ChangeTool;
import com.uroica.drinkmachine.util.serialport.OnSerialPortDataListener;
import com.uroica.drinkmachine.util.serialport.SerialPortManager;

import me.goldze.mvvmhabit.base.BaseActivity;

public class SerialPortActivity extends BaseActivity<ActivitySerialportBinding, SerialPortViewModel> implements AdapterView.OnItemSelectedListener {
    private String[] mDevices;
    private String[] mBaudrates;
    private int mDeviceIndex = 0;
    private int mBaudrateIndex = 0;
    SerialPortManager mSerialPortManager;
    Spinner mSpinnerDevices, mSpinnerBaudrate;
    Button mBtnOpenDevice, mBtnSendData;
    EditText mEtData;
    private boolean mOpened = false;
    LogFragment logFragment;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_serialport;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        getSupportActionBar().setTitle("串口调试");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BarUtils.setStatusBarVisibility(this, false);
        mSpinnerDevices = binding.spinnerDevices;
        mSpinnerBaudrate = binding.spinnerBaudrate;
        mBtnOpenDevice = binding.btnOpenDevice;
        mBtnSendData = binding.btnSendData;
        mEtData = binding.etData;
        initDevice();
        initSpinners();
        initFragment();
        initListener();
        updateViewState(mOpened);
//        AgreementManager.Companion.getInstance().openSerial(this);
//        //首次查询主柜状态 轮询
//        AgreementManager.Companion.getInstance().checkCabinet(1);
    }

    private void initFragment() {
        logFragment =new LogFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl, logFragment);
        transaction.commitAllowingStateLoss();
    }


    private void initDevice() {
        mSerialPortManager = SerialPortManager.instance();
        // 设备
        mDevices = mSerialPortManager.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[]{"找不到串口设备"};
        }
        // 波特率
        mBaudrates = getResources().getStringArray(R.array.baudrates);
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

    private void initListener() {
        mBtnOpenDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOpened) {
                    mSerialPortManager.closeSerialPort();
                    mOpened = false;
                } else {
                    mOpened = mSerialPortManager.openSerialPort(mDevices[mDeviceIndex], mBaudrates[mBaudrateIndex]);
                    if (mOpened) {
                        Toast.makeText(SerialPortActivity.this, "打开串口成功！", Toast.LENGTH_LONG).show();
                        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
                            @Override
                            public void onDataReceived(byte[] bytes) {
                                String data=  ChangeTool.ByteArrToHex(bytes);
//                Log.i("roshen","串口 OnListener="+data);
                                //接受到的數據回調
                                if(logFragment!=null)
                                    logFragment.receiveData(data);
                            }

                            @Override
                            public void onDataSent(byte[] bytes) {

                            }
                        });
                    } else {
                        Toast.makeText(SerialPortActivity.this, "打开串口失败！", Toast.LENGTH_LONG).show();
                    }
                }
                updateViewState(mOpened);
            }
        });
        mBtnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mEtData.getText().toString().trim();
                if (TextUtils.isEmpty(text) || text.length() % 2 != 0) {
                    ToastUtils.showLong("无效数据");
                    return;
                }
                KeyboardUtils.hideSoftInput(SerialPortActivity.this);
                mSerialPortManager.sendBytes(ChangeTool.HexToByteArr(text));
                if(logFragment!=null)
                    logFragment.sendData(text);
            }
        });
//        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
//            @Override
//            public void onDataReceived(byte[] bytes) {
//                String data=    ChangeTool.ByteArrToHex(bytes);
////                Log.i("roshen","串口 OnListener="+data);
//                //接受到的數據回調
//                if(logFragment!=null)
//                    logFragment.receiveData(data);
//            }
//
//            @Override
//            public void onDataSent(byte[] bytes) {
//
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
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * 更新视图状态
     *
     * @param isSerialPortOpened
     */
    private void updateViewState(boolean isSerialPortOpened) {
        int stringRes = isSerialPortOpened ? R.string.close_serial_port : R.string.open_serial_port;
        mBtnOpenDevice.setText(stringRes);
        mSpinnerDevices.setEnabled(!isSerialPortOpened);
        mSpinnerBaudrate.setEnabled(!isSerialPortOpened);
        mBtnSendData.setEnabled(isSerialPortOpened);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            logFragment=null;
            mSerialPortManager.closeSerialPort();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
