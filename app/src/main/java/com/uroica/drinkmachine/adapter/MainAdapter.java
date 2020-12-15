package com.uroica.drinkmachine.adapter;
 
import android.content.Context;
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
import com.uroica.drinkmachine.bean.db.ShopModelDB;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MyMainHolder> {
//    private final int TWO = 2;
//    private final int ONE = 1;
    private Context context;
    private List<ShopModelDB> mList;

    public MainAdapter(Context context, List<ShopModelDB> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void addData(ShopModelDB dataBean) {
        mList.add(dataBean);
    }


    @Override
    public MyMainHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        return new MyMainHolder(view);
    }

    @Override
    public void onBindViewHolder(MyMainHolder holder, final int position) {
        int newPos=position%mList.size();

        final ShopModelDB dataBean = mList.get(newPos);
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(6);
        //通过RequestOptions扩展功能
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(context).load(dataBean.getImgURL()).apply(options).into(holder.iv_shop);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener != null) {
//                    listener.onClick(position, dataBean);
//                }
//            }
//        });
        holder.itemView.setTag(position);


    }

    @Override
    public int getItemCount() {
//        return mList.size();
        return Integer.MAX_VALUE;
    }

    public interface OnItemShopClickListener {
        void onClick(int position, ShopModelDB dataBean);
    }

    OnItemShopClickListener listener;

    public void setOnItemShopClickListener(OnItemShopClickListener listener) {
        this.listener = listener;
    }
}


class MyMainHolder extends RecyclerView.ViewHolder {
//    TextView tv_price;
//    TextView tv_name;
//    TextView tv_describe;
    ImageView iv_shop;

    public MyMainHolder(View itemView) {
        super(itemView);
//        tv_price = itemView.findViewById(R.id.tv_price);
//        tv_name = itemView.findViewById(R.id.tv_name);
//        tv_describe = itemView.findViewById(R.id.tv_describe);
        iv_shop = itemView.findViewById(R.id.iv_shop);
    }


}
