package com.uroica.drinkmachine.adapter;
 
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.db.SaleRecordDB;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
    private Context context;
    private List<SaleRecordDB> mList;

    public RecordAdapter(Context context, List<SaleRecordDB> mList) {
        this.context = context;
        this.mList = mList;
    }


    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_record, parent, false);
        return new RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, int position) {
        SaleRecordDB saleRecordDB=mList.get(position);
        //
        String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(saleRecordDB.getTime());
        holder.tv_id.setText(saleRecordDB.getSid()+"");
        holder.tv_orderID.setText(saleRecordDB.getOrderID());
        holder.tv_time.setText(timeNow);
        holder.tv_name.setText(saleRecordDB.getProductName());
        holder.tv_price.setText(saleRecordDB.getPrice());
        if(saleRecordDB.getFrom()==1){
            holder.tv_from.setText("支付宝扫码支付");
        }else if(saleRecordDB.getFrom()==2){
            holder.tv_from.setText("微信扫码支付");
        }else if(saleRecordDB.getFrom()==3){
            holder.tv_from.setText("账户余额支付");
        }else{
            holder.tv_from.setText("微信小程序支付");
        }
        if(saleRecordDB.getShipment_status()==0){
            holder.tv_shipment_status.setTextColor(Color.RED);
            holder.tv_shipment_status.setText("出货失败");
        }else{
            holder.tv_shipment_status.setTextColor(Color.GREEN);
            holder.tv_shipment_status.setText("出货成功");
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

class RecordHolder extends RecyclerView.ViewHolder {
    TextView tv_id, tv_orderID,tv_time,tv_name,tv_price,tv_from,tv_shipment_status;

    public RecordHolder(View itemView) {
        super(itemView);
        tv_id = itemView.findViewById(R.id.tv_id);
        tv_orderID = itemView.findViewById(R.id.tv_orderID);
        tv_time = itemView.findViewById(R.id.tv_time);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_price = itemView.findViewById(R.id.tv_price);
        tv_from = itemView.findViewById(R.id.tv_from);
        tv_shipment_status = itemView.findViewById(R.id.tv_shipment_status);
    }

}
