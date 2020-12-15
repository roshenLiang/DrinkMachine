package com.uroica.drinkmachine.adapter;
 
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.db.ShopModelDB;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<MyShopHolder> {
//    private final int TWO = 2;
//    private final int ONE = 1;
    private Context context;
    private List<ShopModelDB> mList;

    public ShopAdapter(Context context, List<ShopModelDB> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void addData(ShopModelDB dataBean) {
        mList.add(dataBean);
    }

//    @Override
//    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if (manager instanceof GridLayoutManager) {
//            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
//            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    int type = getItemViewType(position);
//                    switch (type) {
//                        case ONE:
//                            return 1;
//                        default:
//                            return 2;
//                    }
//                }
//            });
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (mList.size() % 2 == 1)
//            if (mList.size() - 1 == position)
//                return TWO;
//            else
//                return ONE;
//        else
//            return ONE;
//
//    }

    @Override
    public MyShopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false);
        return new MyShopHolder(view);
    }

    @Override
    public void onBindViewHolder(MyShopHolder holder, final int position) {
        final ShopModelDB dataBean = mList.get(position);
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(6);
        //通过RequestOptions扩展功能
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
//        DecimalFormat df = new DecimalFormat("#0.00");
        holder.tv_price.setText("￥ "+dataBean.getPrice());
        holder.tv_name.setText(dataBean.getProductName());
//        holder.tv_describe.setText(dataBean.getDetail());
        Glide.with(context).load(dataBean.getImgURL()).apply(options).into(holder.iv_shop);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(position, dataBean);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemShopClickListener {
        void onClick(int position, ShopModelDB dataBean);
    }

    OnItemShopClickListener listener;

    public void setOnItemShopClickListener(OnItemShopClickListener listener) {
        this.listener = listener;
    }
}


class MyShopHolder extends RecyclerView.ViewHolder {
    TextView tv_price;
    TextView tv_name;
//    TextView tv_describe;
    ImageView iv_shop;

    public MyShopHolder(View itemView) {
        super(itemView);
        tv_price = itemView.findViewById(R.id.tv_price);
        tv_name = itemView.findViewById(R.id.tv_shopname);
//        tv_describe = itemView.findViewById(R.id.tv_describe);
        iv_shop = itemView.findViewById(R.id.iv_shop);
    }


}
