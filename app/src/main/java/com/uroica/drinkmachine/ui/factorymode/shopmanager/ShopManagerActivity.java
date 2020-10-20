package com.uroica.drinkmachine.ui.factorymode.shopmanager;
 
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.adapter.ShopManagerAdapter;
import com.uroica.drinkmachine.bean.ShopNumModel;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.constant.Constant;
import com.uroica.drinkmachine.databinding.ActivityShopmanagerBinding;
import com.uroica.drinkmachine.db.CommonDaoUtils;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.rxnetwork.RetrofitHelper;
import com.uroica.drinkmachine.util.ChangeTool;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseActivity;

public class ShopManagerActivity extends BaseActivity<ActivityShopmanagerBinding, ShopManagerViewModel> {
    ShopManagerAdapter adapter;
    List<ShopManagerDB> shopManagerDBList;
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;
    private  String deviceID;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_shopmanager;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        getSupportActionBar().setTitle("商品管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BarUtils.setStatusBarVisibility(this, false);
        shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();
        deviceID = DeviceUtils.getAndroidID();
        deviceID = ChangeTool.codeAddOne(deviceID, 20).toUpperCase();
        binding.rlDialog.setVisibility(View.GONE);
        SharedPreferenceUtil.initSharedPreferenc(this);
        shopManagerDBList=shopManagerDBUtils.queryAll();
//        List<ShopManagerDB> shopManagerDBS=    shopManagerDBUtils.queryAll();
//        for(int i=0;i<shopManagerDBS.size();i++){
//            ShopManagerDB s= shopManagerDBS.get(i);
//            Log.i("roshen",s.getProductName());
//        }
        adapter = new ShopManagerAdapter(this, shopManagerDBList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.ryShopmanager.setLayoutManager(linearLayoutManager);
        binding.ryShopmanager.setAdapter(adapter);
        initListen();
    }

    private void initListen() {
        binding.tvOne2full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setFull();
            }
        });
        binding.tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setClear();

            }
        });
        binding.c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(ShopManagerActivity.this);
            }
        });
//        adapter.setOnStockClickListener(new ShopManagerAdapter.OnStockClickListener() {
//            @Override
//            public void onClick(int p1,ShopManagerDB s) {
//            }
//        });
    }

    public void setShopNum2Server() {
        binding.rlDialog.setVisibility(View.VISIBLE);
        Map<String,Integer> map=new HashMap<>();
        for(ShopManagerDB shopManagerDB:shopManagerDBList){
            if(map.get(shopManagerDB.getProductID())!=null){
                //代表之前有數量
                map.put(shopManagerDB.getProductID(),map.get(shopManagerDB.getProductID())+Integer.valueOf(shopManagerDB.getStockNum()));
            }else{
                map.put(shopManagerDB.getProductID(),Integer.valueOf(shopManagerDB.getStockNum()));
            }

        }
        StringBuffer sbPid=new StringBuffer();
        StringBuffer sbNum=new StringBuffer();
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            String mapKey = entry.getKey();
            Integer mapValue = entry.getValue();
            Log.i("qwerqweqr","商品id="+mapKey+",數量="+mapValue);
            sbPid.append(mapKey);
            sbPid.append("@#");
            sbNum.append(mapValue);
            sbNum.append("@#");
        }


        RetrofitHelper.setShopNum().setShopNum(deviceID,"123",
                sbPid.toString().substring(0,sbPid.toString().length()-2),sbNum.toString().substring(0,sbNum.toString().length()-2))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShopNumModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShopNumModel shopNumModel) {
                        binding.rlDialog.setVisibility(View.GONE);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
//        RetrofitHelper.setShopNum().setShopNum(deviceID,"123",
//                shopModelDB1.getProductID()+"@#"+shopModelDB2.getProductID(),combAdapter1.getLinkedList().size()+"@#"+combAdapter2.getLinkedList().size())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ShopNumModel>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(ShopNumModel shopNumModel) {
//                        binding.rlDialog.setVisibility(View.GONE);
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishA();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void finishA() {
//        setShopNum2Server();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishA();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
