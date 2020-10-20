package com.uroica.drinkmachine.ui.fragment.pay;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.DeviceUtils;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.CodeModel;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.bean.rxbus.Bus_LooperDrinkBean;
import com.uroica.drinkmachine.bean.rxbus.Bus_LooperHeatBean;
import com.uroica.drinkmachine.constant.SharePConstant;
import com.uroica.drinkmachine.databinding.FragmentPayBinding;
import com.uroica.drinkmachine.db.CommonDaoUtils;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.gen.ShopManagerDBDao;
import com.uroica.drinkmachine.greement.AgreementManager;
import com.uroica.drinkmachine.rxnetwork.RetrofitHelper;
import com.uroica.drinkmachine.ui.sale.SalesPageActivity;
import com.uroica.drinkmachine.util.ChangeTool;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;
import com.uroica.drinkmachine.util.ZxingUtils;

import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseFragment;


public class PayFragment extends BaseFragment<FragmentPayBinding, PayViewModel> {
    private String deviceID;
    private SalesPageActivity activity;
    private Handler mHandler;
    private CountDownTimer backTimer;
    private long BACK_NUM = 121000;
    private ShopModelDB dataBean;
    boolean isChannelFault = false;
    boolean isWbFault = false;
    CommonDaoUtils<ShopModelDB> shopDaoUtils;
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;
    boolean wxPay = false, aliPay = false;
    boolean fromMachine;
    ShopManagerDB shopManagerDBInfo;//要出货的商品信息

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_pay;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        activity = (SalesPageActivity) getActivity();
        deviceID = DeviceUtils.getAndroidID();
        deviceID = ChangeTool.codeAddOne(deviceID, 20).toUpperCase();
        dataBean = (ShopModelDB) getArguments().getSerializable("ShopModelDB");
        fromMachine = getArguments().getBoolean("fromMachine");
        shopDaoUtils = DaoUtilsStore.getInstance().getShopDaoUtils();
        shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();
        SharedPreferenceUtil.initSharedPreferenc(activity);

        mHandler = new Handler();
        initShowPro();
        initListen();

    }

    void initShowPro() {
        BACK_NUM = 121000;
        isChannelFault = false;
        isWbFault = false;
//        isAllFault = false;
        isShipmenting = false;//是否有出貨結果
        isShipmentResult = false;//是否有出貨結果
        wxPay = false;
        aliPay = false;
        if (fromMachine) {
            binding.tvPrice.setText("￥ " + dataBean.getPrice());
            binding.tvName.setText(dataBean.getProductName());
            binding.tvDescribe.setText(dataBean.getDetail());
            Glide.with(getActivity()).load(dataBean.getImgURL()).transition(GenericTransitionOptions.with(R.anim.zoomin)).into(binding.ivShopPay);
            getAliPay();
            getWxPay();
            startBackCountDown();
        } else {
            shipmenting();
        }
    }


    boolean isShipmenting = false;//是否有出貨中
    boolean isShipmentResult = false;//是否有出貨結果

    private void initListen() {
        AgreementManager.Companion.getInstance().setReceivedListener(new AgreementManager.onReceivedListener() {
            @Override
            public void OnListener(@NotNull String data) {
                final Bus_LooperDrinkBean looperBean = new Bus_LooperDrinkBean(data);
                if (looperBean.getControl_state() == 1 && !isShipmenting) {
                    shipmenting();
                } else if (looperBean.getControl_state() == 2 && isShipmenting && !isShipmentResult) {
                    shipmentResult(looperBean);
                }
            }
        });
        binding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.commitAllowingStateLoss(0);
            }
        });
    }


    public void sendShipment(final ShopManagerDB sdb) {
        shopManagerDBInfo=sdb;
        if (isShipmentResult) {
            //在出货完成的时候，小程序那边突然又安排出货
            initShowPro();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                int lostGood=SharedPreferenceUtil.getIntData(SharePConstant.LOSTGOOD_SWITCH,1);
                AgreementManager.Companion.getInstance().runDrinkShipment(shopManagerDBInfo.getCabinetID(), lostGood,Integer.valueOf(shopManagerDBInfo.getChannleID()));
            }
        }, 500);
    }

    private void shipmentResult(final Bus_LooperDrinkBean looperBean) {
        isShipmentResult = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (looperBean.getResult_shipment() == 0) {
                    BACK_NUM = 6000;
                    Glide.with(getActivity()).load(R.mipmap.icon_shipmenttrue).transition(GenericTransitionOptions.with(R.anim.zoomin)).into(binding.ivShipmentStatus);
                    binding.tvShipmentStatus.setText("出货成功");
                    //减少库存
                    shopManagerDBInfo.setStockNum(String.valueOf(Integer.valueOf(shopManagerDBInfo.getStockNum()) - 1));
                    shopManagerDBUtils.update(shopManagerDBInfo);
                } else {
                    BACK_NUM = 20000;
                    Glide.with(getActivity()).load(R.mipmap.icon_shipmentfalse).transition(GenericTransitionOptions.with(R.anim.zoomin)).into(binding.ivShipmentStatus);
                    if (isChannelFault)
                        binding.tvShipmentStatus.setText("出货失败，机器故障，一小时之内自动退款");
                    else
                        binding.tvShipmentStatus.setText("出货失败，一小时之内自动退款");
                }
                startBackCountDown();
            }
        });
    }

//    void shipmentFaildUI() {
//        isShipmentResult = true;
//        BACK_NUM = 10000;
//        Glide.with(getActivity()).load(R.mipmap.icon_shipmentfalse).transition(GenericTransitionOptions.with(R.anim.zoomin)).into(binding.ivShipmentStatus);
//        if (isChannelFault)
//            binding.tvShipmentStatus.setText("出货失败，机器故障，一小时之内自动退款");
//        else
//            binding.tvShipmentStatus.setText("出货失败，一小时之内自动退款");
//        startBackCountDown();
//    }

    private void shipmenting() {
        isShipmenting = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                BACK_NUM = 10000;
                binding.llPayInfo.setVisibility(View.GONE);
                binding.llPayResult.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(R.mipmap.icon_shipmenting).transition(GenericTransitionOptions.with(R.anim.zoomin)).into(binding.ivShipmentStatus);
                binding.tvShipmentStatus.setText("请稍等，正在出货...");
                startBackCountDown();
            }
        });
    }

    public void startBackCountDown() {
        finishBackCountDown();
        backTimer = new CountDownTimer(BACK_NUM, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isShipmenting) {
                    if(binding.tvBack.getVisibility()!=View.VISIBLE){
                        binding.tvBack.setVisibility(View.VISIBLE);
                    }
                    binding.tvBack.setText((int) (millisUntilFinished / 1000) + "s");
                } else {
                    binding.tvBackResult.setText((int) (millisUntilFinished / 1000) + "s");
                }

            }

            @Override
            public void onFinish() {
                if (isShipmenting) {
                    binding.tvBackResult.setVisibility(View.GONE);
                    if(isShipmentResult){
                        activity.commitAllowingStateLoss(0);
                    }
                }else{
                    activity.commitAllowingStateLoss(0);
                }
            }
        };
        backTimer.start();
    }


    public void finishBackCountDown() {
        if (backTimer != null) {
            backTimer.cancel();
            backTimer = null;
        }
    }


    public void getAliPay() {
        RetrofitHelper.getCode().getAliCode(dataBean.getProductName(), dataBean.getPrice(), dataBean.getProductID(), dataBean.getImgURL(), dataBean.getDetail(), deviceID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CodeModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CodeModel codeModel) {
                        aliPay = true;
                        if (aliPay && wxPay) {
                            binding.rlDialog.setVisibility(View.GONE);
                        }
                        if (!codeModel.getRet().equals("-1")) {
                            Bitmap bitmap = ZxingUtils.createQRCode1(codeModel.getDetail().get(0).getCode(), 400);
                            binding.ivZfbpay.setImageBitmap(bitmap);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        getAliPay();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getWxPay() {
        RetrofitHelper.getCode().getWxCode(dataBean.getProductName(), dataBean.getPrice(), dataBean.getProductID(), deviceID, "255", "255")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CodeModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        showDialog("请求支付中，请稍等...");
                    }

                    @Override
                    public void onNext(CodeModel codeModel) {
//                        dismissDialog();
                        wxPay = true;
                        if (aliPay && wxPay) {
                            binding.rlDialog.setVisibility(View.GONE);
                        }
                        if (!codeModel.getRet().equals("-1")) {
                            Bitmap bitmap = ZxingUtils.createQRCode1(codeModel.getDetail().get(0).getCode(), 400);
                            binding.ivWxpay.setImageBitmap(bitmap);
//                            binding.ivZfbpay.setImageBitmap(bitmap);
//                            activity.setOrderID(codeModel.getDetail().get(0).getOrderNO());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
//                        dismissDialog();
                        getWxPay();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AgreementManager.Companion.getInstance().setReceivedListener(null);
        finishBackCountDown();
    }
}
