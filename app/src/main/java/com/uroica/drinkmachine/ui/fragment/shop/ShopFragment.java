package com.uroica.drinkmachine.ui.fragment.shop;
 
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.adapter.ShopAdapter;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.databinding.FragmentShopBinding;
import com.uroica.drinkmachine.db.CommonDaoUtils;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.gen.ShopManagerDBDao;
import com.uroica.drinkmachine.gen.ShopModelDBDao;
import com.uroica.drinkmachine.ui.sale.SalesPageActivity;
import com.uroica.drinkmachine.util.SharedPreferenceUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;


public class ShopFragment extends BaseFragment<FragmentShopBinding, ShopViewModel> {
    RecyclerView ryShop;
    ShopAdapter adapter;
    private List<String> shopPidList;
    private List<ShopModelDB> shopModelList;
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;

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
        GridLayoutManager layoutManage = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        ryShop.setLayoutManager(layoutManage);
        shopPidList=new ArrayList<>();
        shopModelList=new ArrayList<>();
        shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();
        List<ShopManagerDB> lists=shopManagerDBUtils.queryAll();
        for(ShopManagerDB shopManagerDB:lists){
            if(!shopPidList.contains(shopManagerDB.getProductID())){
                //集合里面不包含pid 那么添加到展示页中
                //通过pid 去查询到对象
                shopPidList.add(shopManagerDB.getProductID());
                ShopModelDB sb = DaoUtilsStore.getInstance().getShopDaoUtils().queryByQueryBuilder(ShopModelDBDao.Properties.ProductID.eq(shopManagerDB.getProductID())).get(0);
                shopModelList.add(sb);
            }
        }
        //去重
        adapter = new ShopAdapter(getActivity(), shopModelList);
        ryShop.setAdapter(adapter);
        adapter.setOnItemShopClickListener(new ShopAdapter.OnItemShopClickListener() {
            @Override
            public void onClick(int position, ShopModelDB dataBean) {
                //检查库存
                LinkedList l = shopModel2Comb(dataBean);
                if (l==null||l.size() < 1) {
                    ToastUtils.showLong("该商品库存不足！");
                    return;
                }
                SalesPageActivity salesPageActivity = (SalesPageActivity) getActivity();
                salesPageActivity.commitAllowingStateLoss(1, dataBean,true);
            }
        });
    }

    private LinkedList<String> shopModel2Comb(ShopModelDB dataBean) {
        List<ShopManagerDB> s = shopManagerDBUtils.queryByQueryBuilder(ShopManagerDBDao.Properties.ProductID.eq(dataBean.getProductID()));
        String combString = s.get(0).getCombination();
        String data;
        Gson gson = new Gson();
        if (combString.equals("1")) {
            data = SharedPreferenceUtil.getStrData("comb1");
        } else {
            data = SharedPreferenceUtil.getStrData("comb2");
        }
        if(data.equals("")){
            return null;
        }
        Type listType = new TypeToken<LinkedList<String>>() {}.getType();
        return gson.fromJson(data, listType);
    }



}
