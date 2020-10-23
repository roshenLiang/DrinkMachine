package com.uroica.drinkmachine.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.constant.Constant;
import com.uroica.drinkmachine.db.CommonDaoUtils;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.gen.ShopManagerDBDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShopManagerAdapter extends RecyclerView.Adapter<MyShopManagerHolder> {
    private Context context;
    private List<ShopModelDB> shopModelDBList;
    private List<ShopManagerDB> shopManagerDBS;
    private String[] shopNames;
    CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;


    public ShopManagerAdapter(Context context, List<ShopManagerDB> shopManagerDBS) {
        this.context = context;
        this.shopManagerDBS = shopManagerDBS;
        this.shopModelDBList = DaoUtilsStore.getInstance().getShopDaoUtils().queryAll();
        this.shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();
        shopNames = new String[shopModelDBList.size()];
        for (int i = 0; i < shopModelDBList.size(); i++) {
            shopNames[i] = shopModelDBList.get(i).getProductName();
        }

//        for (ShopManagerDB sdb : shopManagerDBS) {
//            if(sdb.getCombination().equals("1")){
//                linkedList1.add(sdb);
//            }else{
//                linkedList2.add(sdb);
//            }
//
//        }

    }

//    public void setData(List<ShopManagerDB> datas) {
////        for(int i=0;i<datas.size();i++){
////            Log.i("roshen","datas"+datas.get(i).getSid()+",名字"+datas.get(i).getProductName());
////        }
//        shopManagerDBS.clear();
//        this.shopManagerDBS.addAll(datas);
//        notifyDataSetChanged();
//    }

    public List<ShopManagerDB> getList() {
        return shopManagerDBS;
    }

    @Override
    public MyShopManagerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopmanager, parent, false);
        return new MyShopManagerHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyShopManagerHolder holder, final int position) {
        final String stockString[] = context.getResources().getStringArray(R.array.stock);
        ArrayAdapter<String> shopAdapter =
                new ArrayAdapter<String>(context, R.layout.spinner_default_item, shopNames);
        shopAdapter.setDropDownViewResource(R.layout.spinner_item);
        holder.spinner_shop.setAdapter(shopAdapter);

        ArrayAdapter<String> stockAdapter =
                new ArrayAdapter<String>(context, R.layout.spinner_default_item, stockString);
        stockAdapter.setDropDownViewResource(R.layout.spinner_item);
        holder.spinner_stock.setAdapter(stockAdapter);


        final ShopManagerDB shopManagerDB = shopManagerDBS.get(position);
        Glide.with(context).load(shopManagerDB.getImgURL()).into(holder.iv_shoppic);

        if (shopManagerDB.getChannelFault().equals("0")) {
            //正常
            holder.tv_channelfault.setTextColor(context.getResources().getColor(R.color.green));
            holder.tv_channelfault.setText("正常");
        } else {
            holder.tv_channelfault.setTextColor(context.getResources().getColor(R.color.red));
            holder.tv_channelfault.setText("故障");
        }
        for(int i=0;i<shopNames.length;i++){
            if(shopNames[i].equals(shopManagerDB.getProductName())){
                holder.spinner_shop.setSelection(i, false);
            }
        }
        holder.tv_price.setText(shopManagerDB.getPrice());

        holder.spinner_stock.setSelection(Integer.valueOf(shopManagerDB.getStockNum()), false);
        holder.spinner_shop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int p1, long id) {
//                if (p1 == Integer.valueOf(shopManagerDB.getCombination()) - 1) {
//                    Log.i("roshen", "return");
//                    return;
//                }
                ShopManagerDB s = shopManagerDBS.get(position);
                ShopModelDB shopModelDB = shopModelDBList.get(p1);
                s.setShopModel(shopModelDB);
                shopManagerDBUtils.update(s);
                Glide.with(context).load(s.getImgURL()).into(holder.iv_shoppic);
                holder.tv_price.setText(s.getPrice());
//                ShopManagerDB sss = shopManagerDBUtils.queryByQueryBuilder(ShopManagerDBDao.Properties.Sid.eq(s.getSid())).get(0);
//                Log.i("roshen", "查询=" + sss.getSid() + "，name=" + sss.getProductName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holder.spinner_stock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int p2, long id) {
                ShopManagerDB s = shopManagerDBS.get(position);
                if (onStockClickListener != null) {
                    onStockClickListener.onClick(p2, s);
                }
                String stock = stockString[p2];
                s.setStockNum(stock);
                shopManagerDBUtils.update(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holder.tv_channelId.setText("货道" + shopManagerDB.getChannleID());
        holder.tv_cabinetId.setText("柜号" + shopManagerDB.getCabinetID());
        holder.tv_capacity.setText(String.valueOf(Constant.TOTAL_NUM));
    }

    @Override
    public int getItemCount() {
        return shopManagerDBS.size();
    }

    public void setFull() {
        for (int i = 0; i < shopManagerDBS.size(); i++) {
            ShopManagerDB sb = shopManagerDBS.get(i);
            Log.i("roshen", "sb" + sb.getProductName());
            sb.setStockNum(String.valueOf(Constant.TOTAL_NUM));
            shopManagerDBUtils.update(sb);
        }
        notifyDataSetChanged();
    }

    public void setClear() {
        for (int i = 0; i < shopManagerDBS.size(); i++) {
            ShopManagerDB sb = shopManagerDBS.get(i);
            Log.i("roshen", "sb" + sb.getProductName());
            sb.setStockNum("0");
            shopManagerDBUtils.update(sb);
        }
        notifyDataSetChanged();
    }


    public interface OnStockClickListener {
        void onClick(int p1, ShopManagerDB s);
    }

    OnStockClickListener onStockClickListener;

    public void setOnStockClickListener(OnStockClickListener onStockClickListener) {
        this.onStockClickListener = onStockClickListener;
    }

}


class MyShopManagerHolder extends RecyclerView.ViewHolder {
    TextView tv_cabinetId;
    TextView tv_channelId;
    TextView tv_channelfault;
    Spinner spinner_shop;
    //    Spinner spinner_combination;
    ImageView iv_shoppic;
    TextView tv_capacity;
    Spinner spinner_stock;
    TextView tv_price;

    public MyShopManagerHolder(View itemView) {
        super(itemView);
        tv_cabinetId = itemView.findViewById(R.id.tv_cabinetId);
        tv_channelId = itemView.findViewById(R.id.tv_channelId);
        tv_channelfault = itemView.findViewById(R.id.tv_channelfault);
        spinner_shop = itemView.findViewById(R.id.spinner_shop);
        iv_shoppic = itemView.findViewById(R.id.iv_shoppic);
        tv_capacity = itemView.findViewById(R.id.tv_capacity);
        spinner_stock = itemView.findViewById(R.id.spinner_stock);
        tv_price = itemView.findViewById(R.id.tv_price);
    }


}
