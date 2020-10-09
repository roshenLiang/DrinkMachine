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
import com.uroica.drinkmachine.bean.rxbus.Bus_LooperHeatBean;
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
//    boolean isAllFault = false;
    CommonDaoUtils<ShopModelDB> shopDaoUtils;
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;
    LinkedList<String> combLinkedString;
    String combString;
    boolean wxPay = false, aliPay = false;
    boolean fromMachine;

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

        Log.i("数据库", "加热时间 " + dataBean.getHeartTime());

        mHandler = new Handler();
        initShowPro();
        initListen();

    }

    void initShowPro() {
        shopModel2Comb();
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

    private void shopModel2Comb() {
        List<ShopManagerDB> s = shopManagerDBUtils.queryByQueryBuilder(ShopManagerDBDao.Properties.ProductID.eq(dataBean.getProductID()));
        combString = s.get(0).getCombination();
        String data;
        Gson gson = new Gson();
        if (combString.equals("1")) {
            data = SharedPreferenceUtil.getStrData("comb1");
        } else {
            data = SharedPreferenceUtil.getStrData("comb2");
        }
        Type listType = new TypeToken<LinkedList<String>>() {
        }.getType();
        combLinkedString = gson.fromJson(data, listType);
        Log.i("rrr", "combLinkedString=" + combLinkedString);
    }

    boolean isShipmenting = false;//是否有出貨中
    boolean isShipmentResult = false;//是否有出貨結果

    private void initListen() {
        AgreementManager.Companion.getInstance().setReceivedListener(new AgreementManager.onReceivedListener() {
            @Override
            public void OnListener(@NotNull String data) {
//                LogUtils.file("云端数据", "Pay --接收到的数据= " + data);
                final Bus_LooperHeatBean looperBean = new Bus_LooperHeatBean(data);
                //机器出货故障
                if (looperBean.getChannelDStatus() == 1 || looperBean.getChannelDStatus() == 2) {
                    if (!isChannelFault) {
                        //禁用货道
                        int channelID = looperBean.getCurrent_Channel();
                        ShopManagerDB s = shopManagerDBUtils.queryByQueryBuilder(ShopManagerDBDao.Properties.ChannleID.eq(String.valueOf(channelID))).get(0);
                        s.setChannelFault("1");
                        shopManagerDBUtils.update(s);
                        //组合要移除
                        for (int i = 0; i < Integer.valueOf(s.getStockNum()); i++) {
                            Log.i("rrr", "故障 移除=" + String.valueOf(channelID));
                            combLinkedString.remove(s.getChannleID());
                        }
                        //保存
                        Gson gson = new Gson();
                        String d = gson.toJson(combLinkedString);
                        if (combString.equals("1")) {
                            SharedPreferenceUtil.saveData("comb1", d);
                        } else {
                            SharedPreferenceUtil.saveData("comb2", d);
                        }
                    }
                    isChannelFault = true;
                }

                if ( looperBean.getMicrowaveS() != 0&&!isWbFault) {
//                    isAllFault = true;
                    //微波炉故障 库存也要减少
                    ShopManagerDB ss = shopManagerDBUtils.queryByQueryBuilder(ShopManagerDBDao.Properties.ChannleID.eq(combLinkedString.getFirst())).get(0);
                    ss.setStockNum(String.valueOf(Integer.valueOf(ss.getStockNum()) - 1));
                    shopManagerDBUtils.update(ss);
                    //comb也要移除后保存
                    combLinkedString.removeFirst();
                    Gson gson = new Gson();
                    String data2 = gson.toJson(combLinkedString);
                    if (combString.equals("1")) {
                        SharedPreferenceUtil.saveData("comb1", data2);
                    } else {
                        SharedPreferenceUtil.saveData("comb2", data2);
                    }
                    isWbFault=true;
                }
                //
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


    public void sendShipment() {
        if (isShipmentResult) {
            //在出货完成的时候，小程序那边突然又安排出货
            initShowPro();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                AgreementManager.Companion.getInstance().runHeatShipment(1, Integer.valueOf(combLinkedString.getFirst()), 0, Integer.valueOf(dataBean.getHeartTime()), 0, 0);
            }
        }, 500);
    }


    private void shipmentResult(final Bus_LooperHeatBean looperBean) {
        isShipmentResult = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (looperBean.getShipment_result() == 0) {
                    finishShipmentCountDown();
                    BACK_NUM = 6000;
                    Glide.with(getActivity()).load(R.mipmap.icon_shipmenttrue).transition(GenericTransitionOptions.with(R.anim.zoomin)).into(binding.ivShipmentStatus);
                    binding.tvShipmentStatus.setText("出货成功");
                    //减少库存
                    ShopManagerDB ss = shopManagerDBUtils.queryByQueryBuilder(ShopManagerDBDao.Properties.ChannleID.eq(combLinkedString.getFirst())).get(0);
                    ss.setStockNum(String.valueOf(Integer.valueOf(ss.getStockNum()) - 1));
                    shopManagerDBUtils.update(ss);
                    //comb也要移除后保存
                    combLinkedString.removeFirst();
                    Gson gson = new Gson();
                    String data = gson.toJson(combLinkedString);
                    if (combString.equals("1")) {
                        SharedPreferenceUtil.saveData("comb1", data);
                    } else {
                        SharedPreferenceUtil.saveData("comb2", data);
                    }
                    startBackCountDown();
                } else {
                    finishShipmentCountDown();
                    shipmentFaildUI();
                }

            }
        });
    }

    void shipmentFaildUI() {
        isShipmentResult = true;
        BACK_NUM = 10000;
        Glide.with(getActivity()).load(R.mipmap.icon_shipmentfalse).transition(GenericTransitionOptions.with(R.anim.zoomin)).into(binding.ivShipmentStatus);
        if (isChannelFault)
            binding.tvShipmentStatus.setText("出货失败，机器故障，一小时之内自动退款");
        else
            binding.tvShipmentStatus.setText("出货失败，一小时之内自动退款");
        startBackCountDown();
    }

    private void shipmenting() {
        isShipmenting = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                BACK_NUM = Integer.valueOf(dataBean.getHeartTime()) * 1000 + 15000;
                startBackCountDown();
                binding.llPayInfo.setVisibility(View.GONE);
                binding.llPayResult.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(R.mipmap.icon_shipmenting).transition(GenericTransitionOptions.with(R.anim.zoomin)).into(binding.ivShipmentStatus);
                binding.tvShipmentStatus.setText("请稍等，正在出货...");
            }
        });
    }

    public void startBackCountDown() {
        finishBackCountDown();
        backTimer = new CountDownTimer(BACK_NUM, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isShipmenting) {
                    if (binding.tvBack.getVisibility() != View.VISIBLE) {
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
                    if (isShipmentResult) {
                        activity.commitAllowingStateLoss(0);
                    } else {
                        //开启倒数10秒没收到就默认为出货失败
                        startShipmentFailed();
                    }
                } else {
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

    CountDownTimer shipmentFailedTimer;

    public void startShipmentFailed() {
        shipmentFailedTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        shipmentFaildUI();
                    }
                });
            }
        };
        shipmentFailedTimer.start();
    }

    public void finishShipmentCountDown() {
        if (shipmentFailedTimer != null) {
            shipmentFailedTimer.cancel();
            shipmentFailedTimer = null;
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
        finishShipmentCountDown();
    }
}
