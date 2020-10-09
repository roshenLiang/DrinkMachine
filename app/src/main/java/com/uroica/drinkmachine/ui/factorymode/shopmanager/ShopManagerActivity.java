package com.uroica.drinkmachine.ui.factorymode.shopmanager;
 
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.adapter.CombAdapter;
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
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseActivity;

public class ShopManagerActivity extends BaseActivity<ActivityShopmanagerBinding, ShopManagerViewModel> {
    ShopManagerAdapter adapter;
    List<ShopModelDB> shopModelDBList;
    List<ShopManagerDB> shopManagerDBList;
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;
    CommonDaoUtils<ShopModelDB> shopModelDBCommonDaoUtils;
    ShopModelDB shopModelDB1, shopModelDB2;
    private LinkedList<ShopManagerDB> shopManagerDBLinkeds1, shopManagerDBLinkeds2;
    private CombAdapter combAdapter1, combAdapter2;
    private LinkedList<String> comb1LinkedString,comb2LinkedString;
//    private LinkedList<CombModel> comb1Linked,comb2Linked;
    private  String deviceID;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_shopmanager;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    int  loadCombIndex=0;
    int  loadStockIndex=0;
    @Override
    public void initData() {
        getSupportActionBar().setTitle("商品管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BarUtils.setStatusBarVisibility(this, false);
        deviceID = DeviceUtils.getAndroidID();
        deviceID = ChangeTool.codeAddOne(deviceID, 20).toUpperCase();
        binding.rlDialog.setVisibility(View.GONE);
        SharedPreferenceUtil.initSharedPreferenc(this);
        String data1= SharedPreferenceUtil.getStrData("comb1");
        String data2= SharedPreferenceUtil.getStrData("comb2");
        if(data1.equals("")||data2.equals("")){
            comb1LinkedString = new LinkedList<>();
            comb2LinkedString = new LinkedList<>();

        }else{
            Gson gson = new Gson();
            Type listType = new TypeToken<LinkedList<String>>() {}.getType();
            comb1LinkedString = gson.fromJson(data1, listType);
            comb2LinkedString = gson.fromJson(data2, listType);
            if(comb1LinkedString==null)
                comb1LinkedString = new LinkedList<>();
            if(comb2LinkedString==null)
                comb2LinkedString = new LinkedList<>();
        }


        shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();
        shopModelDBCommonDaoUtils = DaoUtilsStore.getInstance().getShopDaoUtils();

        shopManagerDBList = shopManagerDBUtils.queryAll();
        Log.i("rrrr","size="+shopManagerDBList.size());
        shopModelDBList = shopModelDBCommonDaoUtils.queryAll();

        adapter = new ShopManagerAdapter(this, shopManagerDBList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.ryShopmanager.setLayoutManager(linearLayoutManager);
        binding.ryShopmanager.setAdapter(adapter);

        shopModelDB1 = shopModelDBList.get(0);
        shopModelDB2 = shopModelDBList.get(1);
        binding.tvHeatTime1.setText(shopModelDB1.getProductName() + "加热时间：");
        binding.tvHeatTime2.setText(shopModelDB2.getProductName() + "加热时间：");
        binding.etHeatTime1.setText(shopModelDB1.getHeartTime());
        binding.etHeatTime2.setText(shopModelDB2.getHeartTime());



        shopManagerDBLinkeds1 = new LinkedList<>();
        shopManagerDBLinkeds2 = new LinkedList<>();
        for (ShopManagerDB sdb : shopManagerDBList) {
            if (sdb.getCombination().equals("1"))
                shopManagerDBLinkeds1.add(sdb);
            else
                shopManagerDBLinkeds2.add(sdb);
        }
        combAdapter1 = new CombAdapter(this, shopManagerDBLinkeds1,comb1LinkedString);
        combAdapter2 = new CombAdapter(this, shopManagerDBLinkeds2,comb2LinkedString);
        GridLayoutManager ll_comb1 = new GridLayoutManager(this, 6, GridLayoutManager.HORIZONTAL, false);
        GridLayoutManager ll_comb2 = new GridLayoutManager(this, 6, GridLayoutManager.HORIZONTAL, false);
        binding.ryCombination1.setLayoutManager(ll_comb1);
        binding.ryCombination2.setLayoutManager(ll_comb2);
        binding.ryCombination1.setAdapter(combAdapter1);
        binding.ryCombination2.setAdapter(combAdapter2);
        initListen();
    }

    private void initListen() {
        binding.tvSetHeart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etHeatTime1.getText().toString().equals("") ) {
                    ToastUtils.showLong("商品1加热温度不能设置为null");
                    return ;
                }
                int t1=Integer.valueOf(binding.etHeatTime1.getText().toString());
                if (t1<1||t1>150) {
                    ToastUtils.showLong("商品1加热时间1-150秒");
                    return ;
                }
                shopModelDB1.setHeartTime(binding.etHeatTime1.getText().toString());
                shopModelDBCommonDaoUtils.update(shopModelDB1);
                ToastUtils.showShort("设置成功！");
            }
        });
        binding.tvSetHeart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etHeatTime2.getText().toString().equals("") ) {
                    ToastUtils.showLong("商品2加热温度不能设置为null");
                    return ;
                }
                int t2=Integer.valueOf(binding.etHeatTime2.getText().toString());
                if (t2<1||t2>150) {
                    ToastUtils.showLong("商品2加热时间1-150秒");
                    return ;
                }
                shopModelDB2.setHeartTime(binding.etHeatTime2.getText().toString());
                shopModelDBCommonDaoUtils.update(shopModelDB2);
                ToastUtils.showShort("设置成功！");
            }
        });
        binding.tvOne2full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCombIndex=0;
                loadStockIndex=0;
                combAdapter1.setFull();
                combAdapter2.setFull();
                adapter.setFull();

            }
        });
        binding.tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCombIndex=0;
                loadStockIndex=0;
                combAdapter1.setClear();
                combAdapter2.setClear();
                adapter.setClear();

            }
        });

        binding.c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(ShopManagerActivity.this);
            }
        });

        adapter.setOnComBClickListener(new ShopManagerAdapter.OnComBClickListener() {
            @Override
            public void onClick(int p1,int p2) {
                loadCombIndex++;
                if(loadCombIndex< Constant.TOTAL_CHANNEL+1){
                    return;
                }
                ShopManagerDB shopManagerDB=adapter.getList().get(p2);
                // 如果 该货道故障 则不添加
                Log.i("组合","setOnComBClickListenersetOnComBClickListener 組合="+p1+"，数量="+shopManagerDB.getStockNum()+",商品hao="+shopManagerDB.getChannleID());
                if(shopManagerDB.getChannelFault().equals("1")){
                    //故障
                }else{
                    if(p1==0){
                        //组合1 增加 组合2减少
                        combAdapter1.add(shopManagerDB);
                        combAdapter2.remove(shopManagerDB);
                    }else{
                        combAdapter2.add(shopManagerDB);
                        combAdapter1.remove(shopManagerDB);
                    }
                }




            }
        });
        adapter.setOnStockClickListener(new ShopManagerAdapter.OnStockClickListener() {
            @Override
            public void onClick(int p1,ShopManagerDB s) {
                loadStockIndex++;
                if(loadStockIndex<Constant.TOTAL_CHANNEL+1){
                    return;
                }
                // p1 是修改后 p2 是修改前
//                ShopManagerDB shopManagerDB=adapter.getList().get(p2);
                if(s.getChannelFault().equals("1")){

                }else{
                if(s.getCombination().equals("1")){
                    combAdapter1.setData(p1,s);
                }else{
                    combAdapter2.setData(p1,s);
                }
                }
            }
        });
    }

    public void setShopNum2Server() {
        binding.rlDialog.setVisibility(View.VISIBLE);
        RetrofitHelper.setShopNum().setShopNum(deviceID,"123",
                shopModelDB1.getProductID()+"@#"+shopModelDB2.getProductID(),combAdapter1.getLinkedList().size()+"@#"+combAdapter2.getLinkedList().size())
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
        Gson gson = new Gson();
        String data1 = gson.toJson(combAdapter1.getLinkedList());
        SharedPreferenceUtil.saveData("comb1", data1);
        String data2 = gson.toJson(combAdapter2.getLinkedList());
        SharedPreferenceUtil.saveData("comb2", data2);
        setShopNum2Server();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishA();
        }
        return super.onOptionsItemSelected(item);
    }
}
