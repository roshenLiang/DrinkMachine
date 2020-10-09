package com.uroica.drinkmachine.ui.factorymode.salerecord;
 
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.BarUtils;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.adapter.RecordAdapter;
import com.uroica.drinkmachine.bean.db.SaleRecordDB;
import com.uroica.drinkmachine.databinding.ActivityParameterBinding;
import com.uroica.drinkmachine.databinding.ActivitySalerecordBinding;
import com.uroica.drinkmachine.db.DaoUtilsStore;
import com.uroica.drinkmachine.gen.SaleRecordDBDao;
import com.uroica.drinkmachine.util.DensityUtil;
import com.uroica.drinkmachine.util.TimeUtil;
import com.uroica.drinkmachine.util.stickydecoration.PowerfulStickyDecoration;
import com.uroica.drinkmachine.util.stickydecoration.listener.PowerGroupListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

public class SaleRecordActivity extends BaseActivity<ActivitySalerecordBinding, SaleRecordViewModel> {
    RecyclerView mRv;
    List<SaleRecordDB> dataList = new ArrayList<>();
    PowerfulStickyDecoration decoration;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_salerecord;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        getSupportActionBar().setTitle("销售记录");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BarUtils.setStatusBarVisibility(this, false);
        mRv = binding.ryRecord;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRv.setLayoutManager(linearLayoutManager);
        initPowerfulSticky();
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB("01", 1596609307000l, 1, "可乐", "3.00", 1));
        dataList = DaoUtilsStore.getInstance().getSaleRecordDBUtils().queryAll();
//        initSaleRecordDBData();
        mRv.addItemDecoration(decoration);
        RecordAdapter adapter = new RecordAdapter(this, dataList);
        mRv.setAdapter(adapter);

        List<SaleRecordDB> totalLists=DaoUtilsStore.getInstance().getSaleRecordDBUtils().queryByQueryBuilder(SaleRecordDBDao.Properties.Shipment_status.eq(1));
        float totalF=0;
        for(SaleRecordDB saleRecordDB:totalLists){
            totalF+=Float.valueOf(saleRecordDB.getPrice());
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        binding.tvTotalprice.setText("总销售额："+df.format(totalF)+"元");
//        Log.i("roshen","0点="+TimeUtil.getTimesMorning()+",24点="+TimeUtil.getTimesNight());
        List<SaleRecordDB> todayLists=DaoUtilsStore.getInstance().getSaleRecordDBUtils().queryByQueryBuilder(
                SaleRecordDBDao.Properties.Time.gt(TimeUtil.getTimesMorning()), SaleRecordDBDao.Properties.Time.lt(TimeUtil.getTimesNight())
                ,SaleRecordDBDao.Properties.Shipment_status.eq(1));
        float todayTotalF=0;
        for(SaleRecordDB saleRecordDB:todayLists){
            todayTotalF+=Float.valueOf(saleRecordDB.getPrice());
        }
        binding.tvTodayprice.setText("今天销售额："+df.format(todayTotalF)+"元");
    }

//    private void initSaleRecordDBData() {
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB("01", 1590973126000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "02", 1590973136000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "03", 1590973146000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "04", 1590973156000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "05", 1590973166000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB("06", 1591145926000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "07", 1591145936000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "08", 1591145946000l, 1, "可乐", "3.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "09", 1591145956000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "10", 1591405126000l, 1, "雪碧", "5.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "11", 1591405226000l, 1, "雪碧", "5.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "12", 1591405326000l, 1, "雪碧", "5.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "13", 1591405426000l, 1, "雪碧", "5.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "14", 1591405526000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "15", 1592182726000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "16", 1592183726000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "17", 1592184726000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "18", 1592185726000l, 1, "可乐", "3.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "19", 1592614716000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "20", 1592614726000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "21", 1592614736000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "22", 1592614746000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "23", 1592614756000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "24", 1592614766000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "25", 1592614776000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "26", 1593478726000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "27", 1593478736000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "28", 1593478746000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "29", 1593478756000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "30", 1593488594000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "31", 1593492194000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "32", 1593499394000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "33 ", 1593499594000l, 1, "可乐", "3.00", 1));
//        //7月份
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "01", 1593586726000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "02", 1593586736000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "03", 1593586746000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "04", 1593586756000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "05", 1593673126000l, 1, "可乐", "3.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "06", 1593673136000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "07", 1593673146000l, 1, "可乐", "3.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "08", 1593673156000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "09", 1593759526000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "10", 1593759536000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "11", 1593759546000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "12", 1593759556000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "13", 1593759526000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "14", 1593759536000l, 1, "雪碧", "5.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "15", 1593759546000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "16", 1593759556000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "17", 1593759566000l, 1, "雪碧", "5.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "18", 1593773926000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "19", 1593773936000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "20", 1594378726000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "21", 1594378736000l, 1, "可乐", "3.00", 0));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "22", 1594810726000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "23", 1594810756000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "24", 1595242726000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "25", 1595242766000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "26", 1595329126000l, 1, "可乐", "3.00", 1));
//        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "27", 1595329136000l, 1, "可乐", "3.00", 1));
////        7月份
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "01", 1593586726000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "02", 1593586736000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "03", 1593586746000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "04", 1593586756000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "05", 1593673126000l, 1, "可乐", "3.00", 0));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "06", 1593673136000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "07", 1593673146000l, 1, "可乐", "3.00", 0));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "08", 1593673156000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "09", 1593759526000l, 1, "雪碧", "5.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "10", 1593759536000l, 1, "雪碧", "5.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "11", 1593759546000l, 1, "雪碧", "5.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "12", 1593759556000l, 1, "雪碧", "5.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "13", 1593759526000l, 1, "雪碧", "5.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "14", 1593759536000l, 1, "雪碧", "5.00", 0));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "15", 1593759546000l, 1, "雪碧", "5.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "16", 1593759556000l, 1, "雪碧", "5.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "17", 1593759566000l, 1, "雪碧", "5.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "18", 1593773926000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "19", 1593773936000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "20", 1594378726000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "21", 1594378736000l, 1, "可乐", "3.00", 0));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "22", 1594810726000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "23", 1594810756000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "24", 1595242726000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "25", 1595242766000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "26", 1595329126000l, 1, "可乐", "3.00", 1));
////        DaoUtilsStore.getInstance().getSaleRecordDBUtils().insert(new SaleRecordDB( "27", 1595329136000l, 1, "可乐", "3.00", 1));
//
//    }

    private void initPowerfulSticky() {
        //------------- PowerfulStickyDecoration 使用部分  ----------------
        decoration = PowerfulStickyDecoration.Builder
                .init(new PowerGroupListener() {
                    @Override
                    public String getGroupName(int position) {
                        //获取组名，用于判断是否是同一组
                        if (dataList.size() > position) {
                            return String.valueOf(dataList.get(position).getMonth());
                        }
                        position++;
                        return null;
                    }

                    @Override
                    public View getGroupView(int position) {
                        //获取自定定义的组View
                        if (dataList.size() > position) {
                            final View view = getLayoutInflater().inflate(R.layout.item_recordhead, null, false);
                            ((TextView) view.findViewById(R.id.tv_time)).setText(dataList.get(position).getMonth() + "月份");
                            List<SaleRecordDB>  saleRecordDBS=DaoUtilsStore.getInstance().getSaleRecordDBUtils().queryByQueryBuilder
                                    (SaleRecordDBDao.Properties.Month.eq(dataList.get(position).getMonth()),SaleRecordDBDao.Properties.Shipment_status.eq(1));
                            float totalF=0;
                            for(SaleRecordDB saleRecordDB:saleRecordDBS){
                                totalF+=Float.valueOf(saleRecordDB.getPrice());
                            }
                            //
                            DecimalFormat df = new DecimalFormat("#0.00");
                            ((TextView) view.findViewById(R.id.tv_totalprice)).setText("月销售额："+df.format(totalF)+"元");
                            position++;
                            return view;
                        } else {
                            position++;
                            return null;
                        }
                    }
                })
                .setCacheEnable(true)
                .setGroupHeight(DensityUtil.dip2px(SaleRecordActivity.this, 50))   //设置高度
                .build();
        //----------------                 -------------
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
