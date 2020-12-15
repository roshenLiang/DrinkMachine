package com.uroica.drinkmachine.ui.fragment.pay;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.DeviceUtils;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.CodeModel;
import com.uroica.drinkmachine.bean.ShopInfoModel;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.bean.rxbus.Bus_LooperDrinkBean;
import com.uroica.drinkmachine.databinding.FragmentShipmentBinding;
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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseFragment;


public class ShipmentFragment extends BaseFragment<FragmentShipmentBinding, ShipmentViewModel> {
    private SalesPageActivity activity;
    List<ShopInfoModel> datas;//要出货的商品信息
    List<ShopInfoModel> realdatas;//要出货的商品信息
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;
    int index=0;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_shipment;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        activity = (SalesPageActivity) getActivity();
        datas = (List<ShopInfoModel>) getArguments().getSerializable("datas");
        realdatas=new ArrayList<>();
        for(int i=0;i<datas.size();i++){
            for(int j=0;i<Integer.valueOf(datas.get(i).getQuantity());i++){
                realdatas.add(datas.get(i));
            }
        }
//        shopDaoUtils = DaoUtilsStore.getInstance().getShopDaoUtils();
        shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();
//        SharedPreferenceUtil.initSharedPreferenc(activity);
//        mHandler = new Handler();
        initListen();

        /**
         * 出貨完一個到另一個
         */
        ShopManagerDB shopManagerDB= shopManagerDBUtils.queryByQueryBuilder(ShopManagerDBDao.Properties.ProductID.eq(realdatas.get(index).getGoods_id())).get(0);
        AgreementManager.Companion.getInstance().runDrinkShipment(1,0,Integer.valueOf(shopManagerDB.getChannleID()));

    }



    private void initListen() {
        AgreementManager.Companion.getInstance().setReceivedListener(new AgreementManager.onReceivedListener() {
            @Override
            public void OnListener(@NotNull String data) {
                if (data.substring(2, 4).equals("03")) {
                    final Bus_LooperDrinkBean looperBean = new Bus_LooperDrinkBean(data);
                    if(looperBean.getControl_state()==2){
                        AgreementManager.Companion.getInstance().sendACK(1);
                    }
                }
                else if(data.substring(2, 4).equals("06")){
                    index++;
                    if(index>realdatas.size()){
                        return;
                    }
                    ShopManagerDB shopManagerDB= shopManagerDBUtils.queryByQueryBuilder(ShopManagerDBDao.Properties.ProductID.eq(realdatas.get(index).getGoods_id())).get(0);
                    AgreementManager.Companion.getInstance().runDrinkShipment(1,0,Integer.valueOf(shopManagerDB.getChannleID()));
                }
            }
        });
    }








    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AgreementManager.Companion.getInstance().setReceivedListener(null);
    }
}
