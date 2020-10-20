package com.uroica.drinkmachine.ui;
 
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.ShopModel;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.constant.Constant;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.databinding.ActivityMainBinding;
import com.uroica.drinkmachine.db.CommonDaoUtils;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.gen.ShopModelDBDao;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.rxnetwork.RetrofitHelper;
import com.uroica.drinkmachine.ui.factorymode.FactoryModeActivity;
import com.uroica.drinkmachine.ui.sale.SalesPageActivity;
import com.uroica.drinkmachine.util.ChangeTool;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseActivity;
/*
    目前用于跳转
 */

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private String deviceID;
    CommonDaoUtils<ShopModelDB> shopDaoUtils;
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;
    Handler mHandler;
    boolean cabinetNumChange = false;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.mainViewModel;
    }

    @Override
    public void initData() {
        BarUtils.setStatusBarVisibility(this, false);
        mHandler = new Handler();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请读写权限
            requePermission();
        } else {
            doThings();
        }


    }

    public void doThings() {
        initLog();
//        //请求商品 -->更新到数据库-->商品管理这块
        cabinetNumChange = getIntent().getBooleanExtra("cabinetNumChange", false);
        AgreementManager.Companion.getInstance().closeSerialPort();
        deviceID = DeviceUtils.getAndroidID();
        deviceID = ChangeTool.codeAddOne(deviceID, 20).toUpperCase();
        deviceID="00006D8893B746CB2B7F";
        shopDaoUtils = DaoUtilsStore.getInstance().getShopDaoUtils();
        shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();
        binding.tvDeviceid.setText("设备号：" + deviceID);
        getShop();
    }

    private void initLog() {
        File downloadFile = new File(Environment.getExternalStorageDirectory()+"/Uroica");
        if (!downloadFile.mkdirs()) {
            downloadFile.mkdir();
        }
        LogUtils.getConfig().setDir(downloadFile);
        LogUtils.getConfig().setLogHeadSwitch(false);
        LogUtils.getConfig().setFilePrefix("log");
//        设置 log 总开关
        SharedPreferenceUtil.initSharedPreferenc(this);
        int logSwitch = SharedPreferenceUtil.getIntData(SharePConstant.LOG_SWITCH, 1);//就是0 关   1 开
        if (logSwitch == 0) {
            Log.i("roshenennene","关");
            LogUtils.getConfig().setLogSwitch(false);
        } else {
            Log.i("roshenennene","开");
            LogUtils.getConfig().setLogSwitch(true);
        }
        LogUtils.file("------------------------------------------------------------------------------------");
    }


    public void getShop() {
        RetrofitHelper.getShop().getShop("1", "10", deviceID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShopModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShopModel shopModel) {

                        if(Integer.valueOf(shopModel.getRet())<1){
                            binding.tv.setText("沒商品数据，需要后台登记");
                            return;
                        }
                        /*
                            商品有更新的情况下，商品管理和商品信息要全部删除；
                         */
                        List<String> pidList=new ArrayList<>();
                        for (int i = 0; i < shopModel.getData().size(); i++) {
                            pidList.add(shopModel.getData().get(i).getProductID());
                        }
                        //本地存在，后台不存在的话要删除
                        List<ShopModelDB>  benDiALL=    shopDaoUtils.queryAll();
                        if(benDiALL.size()>1){
                            for(ShopModelDB s:benDiALL){
                                if(!pidList.contains(s.getProductID())){
                                    LogUtils.file("商品信息更新，数据库全部移除");
                                    //证明商品信息更新了
                                    shopDaoUtils.deleteAll();
                                    shopManagerDBUtils.deleteAll();
                                }
                            }
                        }
                        //把数据保存到数据库中
                        for (int i = 0; i < shopModel.getData().size(); i++) {
                            ShopModel.DataBean dataBean = shopModel.getData().get(i);
                            //处理价钱
                            DecimalFormat df = new DecimalFormat("#0.00");
                            dataBean.setPrice(df.format(Float.valueOf(dataBean.getPrice())));
                            //处理pid
                            if (dataBean.getProductID().length() < 10) {
                                dataBean.setProductID(ChangeTool.codeAddOne(dataBean.getProductID(), 10));
                            }
                            ShopModelDB shopModelDB = new ShopModelDB(dataBean);
                            //先判断此商品在不在，在则更新 不在插入
                            List list = shopDaoUtils.queryByQueryBuilder(ShopModelDBDao.Properties.ID.eq(dataBean.getID().toString()));
                            if (list.size() > 0) {
                                ShopModelDB temp = (ShopModelDB) list.get(0);
                                shopModelDB.setSid(temp.getSid());
                                boolean v = shopDaoUtils.update(shopModelDB);
                            } else {
                                boolean v = shopDaoUtils.insert(shopModelDB);
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updataShopManager();
                            }
                        }, 500);


                    }

                    @Override
                    public void onError(Throwable e) {
                        getShop();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updataShopManager() {
        int cabinetNum = SharedPreferenceUtil.getIntData(SharePConstant.PARAM_MACHINE_CABINET_NUM, 1);
        List<ShopModelDB> shopModelDBList = shopDaoUtils.queryAll();
        if (cabinetNumChange||cabinetNum*Constant.TOTAL_CHANNEL!=shopManagerDBUtils.queryAll().size()) {
            Log.i("主副柜改变","删除商品管理数据库");
            shopManagerDBUtils.deleteAll();
            ShopManagerDB shopManagerDB;
            for(int i=0;i<cabinetNum;i++){
                for(int j = 0; j< Constant.TOTAL_CHANNEL; j++){
                    shopManagerDB = new ShopManagerDB(shopModelDBList.get(0), j+1, i+1);
//                    Log.i("roshen","SID="+shopManagerDB.getSid()+",getProductName="+shopManagerDB.getProductName());
                    shopManagerDBUtils.insert(shopManagerDB);
                }
            }
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(SalesPageActivity.class);
                finish();
            }
        }, 1000);

    }
    /*
     * 十六进制转byte[]数组
     */

    private void requePermission() {
        new RxPermissions(MainActivity.this).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)//多个权限用","隔开
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            doThings();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
