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

import java.util.List;


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

        shopNames = new String[shopModelDBList.size()];
        for (int i = 0; i < shopModelDBList.size(); i++) {
            shopNames[i] = shopModelDBList.get(i).getProductName();
        }
        shopManagerDBUtils = DaoUtilsStore.getInstance().getShopManagerDBUtils();

//        for (ShopManagerDB sdb : shopManagerDBS) {
//            if(sdb.getCombination().equals("1")){
//                linkedList1.add(sdb);
//            }else{
//                linkedList2.add(sdb);
//            }
//
//        }

    }

    public  List<ShopManagerDB> getList(){
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
        final String combinationString[] = context.getResources().getStringArray(R.array.combination);
        ArrayAdapter<String> shopAdapter =
                new ArrayAdapter<String>(context, R.layout.spinner_default_item, shopNames);
        shopAdapter.setDropDownViewResource(R.layout.spinner_item);
        holder.spinner_shop.setAdapter(shopAdapter);

        ArrayAdapter<String> stockAdapter =
                new ArrayAdapter<String>(context, R.layout.spinner_default_item, stockString);
        stockAdapter.setDropDownViewResource(R.layout.spinner_item);
        holder.spinner_stock.setAdapter(stockAdapter);

        ArrayAdapter<String> combinationAdapter =
                new ArrayAdapter<String>(context, R.layout.spinner_default_item, combinationString);
        combinationAdapter.setDropDownViewResource(R.layout.spinner_item);
        holder.spinner_combination.setAdapter(combinationAdapter);


        ShopManagerDB shopManagerDB = shopManagerDBS.get(position);
        Log.i("图片","t ="+position);
        Glide.with(context).load(shopManagerDB.getImgURL()).into(holder.iv_shoppic);

        holder.spinner_shop.setSelection((Integer.valueOf(shopManagerDB.getCombination()) - 1));
        if(shopManagerDB.getChannelFault().equals("0")){
            //正常
            holder.tv_channelfault.setTextColor(context.getResources().getColor(R.color.green));
            holder.tv_channelfault.setText("正常");
        }else{
            holder.tv_channelfault.setTextColor(context.getResources().getColor(R.color.red));
            holder.tv_channelfault.setText("故障");
        }

        holder.tv_price.setText(shopManagerDB.getPrice());

        holder.spinner_stock.setSelection(Integer.valueOf(shopManagerDB.getStockNum()));
        holder.spinner_combination.setSelection(Integer.valueOf(shopManagerDB.getCombination()) - 1);
        holder.spinner_shop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int p1, long id) {
                ShopManagerDB s = shopManagerDBS.get(position);
                ShopModelDB shopModelDB = shopModelDBList.get(p1);
                s.setShopModel(shopModelDB);
                shopManagerDBUtils.update(s);
                Glide.with(context).load(s.getImgURL()).into(holder.iv_shoppic);
                holder.tv_price.setText(s.getPrice());
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
                    onStockClickListener.onClick(p2,s);
                }
                String stock = stockString[p2];
                s.setStockNum(stock);
                shopManagerDBUtils.update(s);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holder.spinner_combination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int p3, long l) {
                ShopManagerDB s = shopManagerDBS.get(position);
                s.setCombination(String.valueOf(p3 + 1));
                shopManagerDBUtils.update(s);
                if (onComBClickListener != null) {
                    onComBClickListener.onClick(p3,position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holder.tv_channelId.setText("货道" + (position + 1));
        holder.tv_capacity.setText(String.valueOf(Constant.TOTAL_NUM));
    }

    @Override
    public int getItemCount() {
        return shopManagerDBS.size();
    }

    public void setFull() {
        for (int i = 0; i < shopManagerDBS.size(); i++) {
            shopManagerDBS.get(i).setStockNum(String.valueOf(Constant.TOTAL_NUM));
            shopManagerDBUtils.update(shopManagerDBS.get(i));
            notifyDataSetChanged();
        }
    }

    public void setClear() {
        for (int i = 0; i < shopManagerDBS.size(); i++) {
            shopManagerDBS.get(i).setStockNum("0");
            shopManagerDBUtils.update(shopManagerDBS.get(i));
            notifyDataSetChanged();
        }
    }


    public interface OnComBClickListener {
        void onClick(int p1,int p2);
    }

    OnComBClickListener onComBClickListener;

    public void setOnComBClickListener(OnComBClickListener onComBClickListener) {
        this.onComBClickListener = onComBClickListener;
    }

    public interface OnStockClickListener {
        void onClick(int p1,ShopManagerDB s);
    }

    OnStockClickListener onStockClickListener;

    public void setOnStockClickListener(OnStockClickListener onStockClickListener) {
        this.onStockClickListener = onStockClickListener;
    }

}


class MyShopManagerHolder extends RecyclerView.ViewHolder {
    TextView tv_channelId;
    TextView tv_channelfault;
    Spinner spinner_shop;
    Spinner spinner_combination;
    ImageView iv_shoppic;
    TextView tv_capacity;
    Spinner spinner_stock;
    TextView tv_price;

    public MyShopManagerHolder(View itemView) {
        super(itemView);
        tv_channelId = itemView.findViewById(R.id.tv_channelId);
        tv_channelfault = itemView.findViewById(R.id.tv_channelfault);
        spinner_shop = itemView.findViewById(R.id.spinner_shop);
        spinner_combination = itemView.findViewById(R.id.spinner_combination);
        iv_shoppic = itemView.findViewById(R.id.iv_shoppic);
        tv_capacity = itemView.findViewById(R.id.tv_capacity);
        spinner_stock = itemView.findViewById(R.id.spinner_stock);
        tv_price = itemView.findViewById(R.id.tv_price);
    }


}
