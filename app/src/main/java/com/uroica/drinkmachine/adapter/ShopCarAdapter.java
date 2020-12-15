package com.uroica.drinkmachine.adapter;
 
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.ShopCarModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ShopCarAdapter extends RecyclerView.Adapter<MyShopCarHolder> {
//    private final int TWO = 2;
//    private final int ONE = 1;
    private Context context;
    private LinkedHashMap<String,ShopCarModel> shopcarMap;

    public ShopCarAdapter(Context context, LinkedHashMap<String,ShopCarModel> shopcarMap) {
        this.context = context;
        this.shopcarMap = shopcarMap;
    }



    @Override
    public MyShopCarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopcar, parent, false);
        return new MyShopCarHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyShopCarHolder holder, final int position) {
        List<ShopCarModel> list= new ArrayList(shopcarMap.values());
         final ShopCarModel dataBean =list.get(position);
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(6);
        //通过RequestOptions扩展功能
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        holder.tv_num.setText(""+dataBean.getNum());
        Glide.with(context).load(dataBean.getImgURL()).apply(options).into(holder.iv_shop);

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addClickListener!=null){
                    Log.i("rsss","addClickListener!=null");
                    addClickListener.onAdd(position,dataBean);
                }
                ShopCarModel sc=shopcarMap.get(dataBean.getProductID());
                sc.setNum(sc.getNum()+1);
                holder.tv_num.setText(""+sc.getNum());
                //还没跟数据做对比
                shopcarMap.put(sc.getProductID(),sc);
            }
        });
        holder.iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(plusClickListener!=null){
                    Log.i("rsss","plusClickListener!=null");
                    plusClickListener.onPlus(position,dataBean);
                }
                ShopCarModel sc=shopcarMap.get(dataBean.getProductID());
                sc.setNum(sc.getNum()-1);
                holder.tv_num.setText(""+sc.getNum());
                //还没跟数据做对比
                if(sc.getNum()<=0){
                    shopcarMap.remove(sc.getProductID());
                    notifyDataSetChanged();
                }else{
                    shopcarMap.put(sc.getProductID(),sc);
                }

            }
        });
    }
    public LinkedHashMap<String,ShopCarModel> getDatas(){return shopcarMap;}

    @Override
    public int getItemCount() {
        return shopcarMap.size();
    }

    public interface OnItemShopClickListener {
        void onClick(int position, ShopCarModel dataBean);
    }

    OnItemShopClickListener listener;

    public void setOnItemShopClickListener(OnItemShopClickListener listener) {
        this.listener = listener;
    }



    public interface OnAddClickListener {
        void onAdd(int position,ShopCarModel dataBean);
    }

    OnAddClickListener addClickListener;

    public void setOnAddClickListener(OnAddClickListener a) {
        this.addClickListener = a;
    }

    public interface OnItemPlusClickListener {
        void onPlus(int position,ShopCarModel dataBean);
    }

    OnItemPlusClickListener plusClickListener;

    public void setOnItemPlusClickListener(OnItemPlusClickListener plusClickListener) {
        this.plusClickListener = plusClickListener;
    }
}


class MyShopCarHolder extends RecyclerView.ViewHolder {
    TextView tv_num;
    ImageView iv_shop,iv_add,iv_plus;

    public MyShopCarHolder(View itemView) {
        super(itemView);
        tv_num = itemView.findViewById(R.id.tv_num);
        iv_shop = itemView.findViewById(R.id.iv_shop);
        iv_add = itemView.findViewById(R.id.iv_add);
        iv_plus = itemView.findViewById(R.id.iv_plus);
    }


}
