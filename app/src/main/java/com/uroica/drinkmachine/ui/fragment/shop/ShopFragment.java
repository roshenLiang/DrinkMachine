package com.uroica.drinkmachine.ui.fragment.shop;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.adapter.ShopCarAdapter;
import com.uroica.drinkmachine.adapter.ShopAdapter;
import com.uroica.drinkmachine.bean.CodeModel;
import com.uroica.drinkmachine.bean.ShopCarModel;
import com.uroica.drinkmachine.bean.ShopInfoModel;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.databinding.FragmentShopBinding;
import com.uroica.drinkmachine.db.CommonDaoUtils;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.gen.ShopManagerDBDao;
import com.uroica.drinkmachine.gen.ShopModelDBDao;
import com.uroica.drinkmachine.rxnetwork.RetrofitHelper;
import com.uroica.drinkmachine.ui.sale.SalesPageActivity;
import com.uroica.drinkmachine.util.ChangeTool;
import com.uroica.drinkmachine.util.ZxingUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseFragment;


public class ShopFragment extends BaseFragment<FragmentShopBinding, ShopViewModel> {
    RecyclerView ryShop;
    ShopAdapter adapter;
    private List<String> shopPidList;
    private List<ShopModelDB> shopModelList;
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;
    //    private Handler mHandler = new Handler();
    CountDownTimer backTimer;
    CountDownTimer backDialogTimer;
    long BACK_NUM = 120 * 1000;
    //总价格
    String totalPrice="";
    private String deviceID;


    private LinkedHashMap<String, ShopCarModel> shopcarMap;
    private ShopCarAdapter shopCarAdapter;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_shop;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        ryShop = binding.ryShop;
        deviceID = DeviceUtils.getAndroidID();
        deviceID = ChangeTool.codeAddOne(deviceID, 20).toUpperCase();
        deviceID="00006D8893B746CB2B7F";
//        LooperLayoutManager layoutManager = new LooperLayoutManager();
//        layoutManager.setLooperEnable(true);
//        ryShop.setLayoutManager(layoutManager);
        GridLayoutManager layoutManage = new GridLayoutManager(getContext(), 4);
        ryShop.setLayoutManager(layoutManage);
        LinearLayoutManager layoutManage2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rySc.setLayoutManager(layoutManage2);
        shopPidList = new ArrayList<>();
        shopModelList = new ArrayList<>();
        shopcarMap = new LinkedHashMap<>();
        shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();
        List<ShopManagerDB> lists = shopManagerDBUtils.queryAll();
        for (ShopManagerDB shopManagerDB : lists) {
            if (!shopPidList.contains(shopManagerDB.getProductID())) {
                //集合里面不包含pid 那么添加到展示页中
                //通过pid 去查询到对象
                shopPidList.add(shopManagerDB.getProductID());
                ShopModelDB sb = DaoUtilsStore.getInstance().getShopDaoUtils().queryByQueryBuilder(ShopModelDBDao.Properties.ProductID.eq(shopManagerDB.getProductID())).get(0);
                shopModelList.add(sb);
            }
        }
        //去重
        final SalesPageActivity salesPageActivity = (SalesPageActivity) getActivity();
        adapter = new ShopAdapter(getActivity(), shopModelList);
        ryShop.setAdapter(adapter);
        shopCarAdapter = new ShopCarAdapter(getActivity(), shopcarMap);
        binding.rySc.setAdapter(shopCarAdapter);
        adapter.setOnItemShopClickListener(new ShopAdapter.OnItemShopClickListener() {
            @Override
            public void onClick(int position, ShopModelDB dataBean) {
                //检查库存
                //判断货存是否充足
                List<ShopManagerDB> s = DaoUtilsStore.getInstance().getShopManagerDBUtils().queryByQueryBuilder(ShopManagerDBDao.Properties.ProductID.eq(dataBean.getProductID()));
//                int shopNum = 0;
                boolean isCheckStock = false;
                for (ShopManagerDB sm : s) {
                    if (Integer.valueOf(sm.getStockNum()) > 1 && sm.getChannelFault().equals("0") && !isCheckStock) {
                        isCheckStock = true;
                        break;
                    }
                }
                if (!isCheckStock) {
                    ToastUtils.showLong("该商品货存不足！");
                    return;
                }
                ShopCarModel shopCarModel = shopcarMap.get(dataBean.getProductID());
                if (shopCarModel == null) {
                    shopcarMap.put(dataBean.getProductID(), new ShopCarModel(dataBean, 1));
                } else {
                    shopCarModel.setNum(shopCarModel.getNum() + 1);
                    shopcarMap.put(dataBean.getProductID(), shopCarModel);
                }
                shopCarAdapter = new ShopCarAdapter(getActivity(), shopcarMap);
                binding.rySc.setAdapter(shopCarAdapter);
                //总数量
               int  total = 0;
                for (Map.Entry<String, ShopCarModel> entry : shopcarMap.entrySet()) {
                    ShopCarModel ss = entry.getValue();
                    total += ss.getNum();
                }
                binding.tvNumsc.setText("" + total);
                if (!binding.tvTotal.getText().toString().equals("")) {
                    Float f = Float.valueOf(binding.tvTotal.getText().toString()) + Float.valueOf(dataBean.getPrice());
                    BigDecimal bd = new BigDecimal(f);
                    totalPrice=bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                    binding.tvTotal.setText(totalPrice);
                } else {
                    totalPrice= "" + Float.valueOf(dataBean.getPrice());
                    binding.tvTotal.setText(totalPrice);
                }


                shopCarAdapter.setOnAddClickListener(new ShopCarAdapter.OnAddClickListener() {
                    @Override
                    public void onAdd(int position, ShopCarModel dataBean) {
                        binding.tvNumsc.setText("" + (Integer.valueOf(binding.tvNumsc.getText().toString()) + 1));
                        Float f = Float.valueOf(totalPrice) + Float.valueOf(dataBean.getPrice());
                        BigDecimal bd = new BigDecimal(f);
                        totalPrice=bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        binding.tvTotal.setText(totalPrice);

                    }
                });
                shopCarAdapter.setOnItemPlusClickListener(new ShopCarAdapter.OnItemPlusClickListener() {
                    @Override
                    public void onPlus(int position, ShopCarModel dataBean) {
                        binding.tvNumsc.setText("" + (Integer.valueOf(binding.tvNumsc.getText().toString()) - 1));
                        Float f = Float.valueOf(totalPrice) - Float.valueOf(dataBean.getPrice());
                        BigDecimal bd = new BigDecimal(f);
                        totalPrice=bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        binding.tvTotal.setText(totalPrice);

                    }
                });
            }
        });
        binding.tvClearsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopcarMap.clear();
                totalPrice="0";
                binding.tvNumsc.setText("0");
                binding.tvTotal.setText(totalPrice);
                shopCarAdapter = new ShopCarAdapter(getActivity(), shopcarMap);
                binding.rySc.setAdapter(shopCarAdapter);
            }
        });

        binding.tvSettlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shopcarMap.size() > 0) {
                    //先請求完二維碼
//                    binding.rlDialog.setVisibility(View.VISIBLE);
                    showDialog();
                } else {
                    ToastUtils.showLong("购物车还没商品！");
                }
            }
        });


    }

    //
    @Override
    public void onResume() {
        super.onResume();
        startBackCountDown();
    }

    @Override
    public void onStop() {
        super.onStop();
        finishBackCountDown();
    }


    public List<ShopInfoModel> getDatas(){
        LinkedHashMap<String,ShopCarModel> scMap= shopCarAdapter.getDatas();
        List<ShopInfoModel> list = new ArrayList<>();
        for(Map.Entry<String,ShopCarModel> entry : scMap.entrySet()) {
            ShopInfoModel bean = new ShopInfoModel(entry.getValue().getProductID(),entry.getValue().getProductName(),entry.getValue().getNum()+"",""+Float.valueOf(entry.getValue().getPrice())*entry.getValue().getNum());
            list.add(bean);
        }
        return list;
    }

    AlertDialog dialog;
    TextView tv_second;

    ImageView iv_wxpay,iv_zfbpay;

    //初始化并弹出对话框方法
    private void showDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_settlement, null);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        dialog.setCanceledOnTouchOutside(false);
        tv_second = view.findViewById(R.id.tv_second);
        iv_wxpay=view.findViewById(R.id.iv_wxpay);
        iv_zfbpay=view.findViewById(R.id.iv_zfbpay);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();

            }
        });
        dialog.show();
        finishBackCountDown();
        startBackDialogCountDown();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ScreenUtils.getScreenWidth() * 4 / 5, ScreenUtils.getScreenWidth() * 4 / 5);
        window.setWindowAnimations(R.style.dialogWindowAnim);  //添加动画
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        getWxPay();
        getAliPay();
    }

    public void getAliPay() {
        LinkedHashMap<String,ShopCarModel> scMap= shopCarAdapter.getDatas();
        List<ShopInfoModel> list = new ArrayList<>();
        for(Map.Entry<String,ShopCarModel> entry : scMap.entrySet()) {
            ShopInfoModel bean = new ShopInfoModel(entry.getValue().getProductID(),entry.getValue().getProductName(),entry.getValue().getNum()+"",""+Float.valueOf(entry.getValue().getPrice())*entry.getValue().getNum());
            list.add(bean);
        }

        String json = new Gson().toJson(list);
        Log.i("roshen","json="+json);
        RetrofitHelper.getCode().getAliCode("尤洛卡购物车", totalPrice, "", "", "", deviceID,json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CodeModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CodeModel codeModel) {
                        Log.i("roshen","codeModel="+codeModel.getDetail().get(0).getCode());
                        if (!codeModel.getRet().equals("-1")) {
                            Bitmap bitmap = ZxingUtils.createQRCode1(codeModel.getDetail().get(0).getCode(), 400);
                            iv_zfbpay.setImageBitmap(bitmap);
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
        LinkedHashMap<String,ShopCarModel> scMap= shopCarAdapter.getDatas();
        List<ShopInfoModel> list = new ArrayList<>();
        for(Map.Entry<String,ShopCarModel> entry : scMap.entrySet()) {
            ShopInfoModel bean = new ShopInfoModel(entry.getValue().getProductID(),entry.getValue().getProductName(),entry.getValue().getNum()+"",""+Float.valueOf(entry.getValue().getPrice())*entry.getValue().getNum());
            list.add(bean);
        }

        String json = new Gson().toJson(list);
        Log.i("roshen","json="+json);
        RetrofitHelper.getCode().getWxCode("尤洛卡购物车",totalPrice , "", deviceID, "", "",json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CodeModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CodeModel codeModel) {
                        Log.i("roshen","codeModel="+codeModel.getDetail().get(0).getCode());
                        if (!codeModel.getRet().equals("-1")) {
                            Bitmap bitmap = ZxingUtils.createQRCode1(codeModel.getDetail().get(0).getCode(), 400);
                            iv_wxpay.setImageBitmap(bitmap);
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


    private void hideDialog() {
        if(dialog!=null)
            dialog.dismiss();
        finishBackDialogCountDown();
        startBackCountDown();
    }


    public void startBackDialogCountDown() {
        finishBackDialogCountDown();
        backDialogTimer = new CountDownTimer(120 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_second.setText((int) (millisUntilFinished / 1000) + "S");
            }

            @Override
            public void onFinish() {
                hideDialog();
            }
        };
        backDialogTimer.start();
    }


    public void finishBackDialogCountDown() {
        if (backDialogTimer != null) {
            backDialogTimer.cancel();
            backDialogTimer = null;
        }
    }

    public void startBackCountDown() {
        finishBackCountDown();
        backTimer = new CountDownTimer(BACK_NUM, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvS.setText((int) (millisUntilFinished / 1000) + "");
            }

            @Override
            public void onFinish() {
                ((SalesPageActivity) getActivity()).commitAllowingStateLoss(0);
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


}
