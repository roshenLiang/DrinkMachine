package com.uroica.drinkmachine.ui.factorymode;
 
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.BarUtils;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.constant.Constant;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.databinding.ActivityFactorymodeBinding;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.ui.MainActivity;
//import com.uroica.machine.ui.factorymode.testmode.DrinkTestModeActivity;
import com.uroica.drinkmachine.ui.factorymode.testmode.DrinkTestModeActivity;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class FactoryModeActivity extends BaseActivity<ActivityFactorymodeBinding, FactoryModeViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_factorymode;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        getSupportActionBar().setTitle("工厂模式");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AgreementManager.Companion.getInstance().closeSerialPort();
        BarUtils.setStatusBarVisibility(this, false);
        binding.btnSetParam.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                binding.btnSerialport.setVisibility(View.VISIBLE);
                binding.btnSocket.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.testEvent.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                SharedPreferenceUtil.initSharedPreferenc(FactoryModeActivity.this);
                int machine_type = SharedPreferenceUtil.getIntData(SharePConstant.PARAM_MACHINE_TYPE_INDEX);
                if(machine_type==Constant.MACHINE_TYPE_HEAT){
                    startActivity(DrinkTestModeActivity.class);
                }else if(machine_type==Constant.MACHINE_TYPE_DRINK){
//                    startActivity(DrinkTestModeActivity.class);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(MainActivity.class);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(MainActivity.class);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
