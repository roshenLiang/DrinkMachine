//package com.uroica.drinkmachine.adapter;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.uroica.drinkmachine.R;
//import com.uroica.drinkmachine.bean.db.ShopManagerDB;
//import com.uroica.drinkmachine.constant.Constant;
//
//import java.util.LinkedList;
//import java.util.List;
//
//
//public class CombAdapter extends RecyclerView.Adapter<MyCombHolder> {
//    private Context context;
//    private List<ShopManagerDB> shopManagerDBS;
//    //    private int size = 0;
//    private LinkedList<String> linkedList;
//
//
//    public CombAdapter(Context context, LinkedList<ShopManagerDB> shopManagerDBS, LinkedList<String> linkedList) {
//        this.context = context;
//        this.shopManagerDBS = shopManagerDBS;
//        this.linkedList = linkedList;
////        size=linkedList.size();
////        updateSize();
//    }
//
//    public LinkedList getLinkedList() {
//        return linkedList;
//    }
//
//
//    /*
//        组合操作
//     */
//    public void remove(ShopManagerDB s) {
//        int stock = Integer.valueOf(s.getStockNum());
//        shopManagerDBS.remove(s);
//        for (int i = 0; i < stock; i++) {
//            linkedList.remove(s.getChannleID());
//        }
////        size -= stock;
//        notifyDataSetChanged();
//    }
//
//    /*
//      组合操作
//   */
//    public void add(ShopManagerDB s) {
////        Log.i("组合", "add");
//        int stock = Integer.valueOf(s.getStockNum());
//        shopManagerDBS.add(s);
//        for (int i = 0; i < stock; i++) {
//            linkedList.add(s.getChannleID());
//        }
////        size += stock;
//        notifyDataSetChanged();
//    }
//
//    public void setFull() {
//        for (int i = 0; i < shopManagerDBS.size(); i++) {
//            ShopManagerDB s = shopManagerDBS.get(i);
//            if (s.getChannelFault().equals("0")) {
////            Log.i("组合", "shopManagerDBS.size()=" + shopManagerDBS.size() + " stock= " + s.getStockNum());
//                if (Integer.valueOf(s.getStockNum()) < Constant.TOTAL_NUM) {
//                    for (int j = 0; j < Constant.TOTAL_NUM - Integer.valueOf(s.getStockNum()); j++) {
////                    Log.i("组合", "add=" + s.getChannleID());
//                        linkedList.add(s.getChannleID());
//                    }
////                size += (7 - Integer.valueOf(s.getStockNum()));
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    public void setClear() {
//        linkedList.clear();
////        size = 0;
//        notifyDataSetChanged();
//    }
//
//    /*
//      库存操作
//   */
//    public void setData(int p1, ShopManagerDB shopManagerDB) {
//        int p2 = Integer.valueOf(shopManagerDB.getStockNum());
//        // p1 是修改后 p2 是修改前
////        Log.i("组合", "库存 修改后 p1=" + p1 + ",修改前 p2=" + p2 + ",货道：=" + shopManagerDB.getChannleID());
//        if (p1 > p2) {
//            //增加
////            Log.i("组合", "增加");
////            size += (p1 - p2);
//            for (int i = 0; i < (p1 - p2); i++) {
//                linkedList.add(shopManagerDB.getChannleID());
//            }
//        } else if (p1 == p2) {
////            Log.i("组合", "一键操作过");
//            //
////            if (p1 == 7){
////                for (int i = 0; i < shopManagerDBS.size(); i++) {
////                    ShopManagerDB s = shopManagerDBS.get(i);
////                    Log.i("组合", "shopManagerDBS.size()=" + shopManagerDBS.size() + " stock= " + s.getStockNum());
////                    if (Integer.valueOf(s.getStockNum()) < 7) {
////                        for (int j = 0; j < 7 - Integer.valueOf(s.getStockNum()); j++) {
////                            Log.i("组合", "add=" + s.getChannleID());
////                            linkedList.add(s.getChannleID());
////                        }
////                        size += (7 - Integer.valueOf(s.getStockNum()));
////                    }
////                }
////            }
////            else{
////                shopManagerDBS.clear();
////                linkedList.clear();
////                size = 0;
////            }
//
//        } else {
////            Log.i("组合", "减少");
//            //减少
////            size -= (p2 - p1);
//            for (int i = 0; i < (p2 - p1); i++) {
//                linkedList.remove(shopManagerDB.getChannleID());
//            }
//
//        }
//
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public MyCombHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_comb, null);
//        return new MyCombHolder(view);
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    public void onBindViewHolder(final MyCombHolder holder, final int position) {
//        //这里查询重复
//        String channelId = linkedList.get(position);
////        ShopManagerDB s=DaoUtilsStore.getInstance().getShopManagerDBUtils().queryByQueryBuilder(ShopManagerDBDao.Properties.ChannleID.eq(channelId)).get(0);
////        if(s.getChannelFault().equals("0")){
//        holder.tv_channelID.setBackground(context.getResources().getDrawable(R.drawable.rectangle_bg_green));
////        }else{
////            holder.tv_channelID.setBackground(context.getResources().getDrawable(R.drawable.rectangle_bg_red));
////        }
//        holder.tv_channelID.setText(linkedList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return linkedList.size();
//    }
//
//
//}
//
//
//class MyCombHolder extends RecyclerView.ViewHolder {
//    TextView tv_channelID;
//
//    public MyCombHolder(View itemView) {
//        super(itemView);
//        tv_channelID = itemView.findViewById(R.id.tv_channelID);
//    }
//
//
//}
