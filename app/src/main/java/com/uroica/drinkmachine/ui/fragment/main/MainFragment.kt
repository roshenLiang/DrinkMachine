package com.uroica.drinkmachine.ui.fragment.main

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uroica.drinkmachine.BR
import com.uroica.drinkmachine.R
import com.uroica.drinkmachine.adapter.MainAdapter
import com.uroica.drinkmachine.bean.db.ShopManagerDB
import com.uroica.drinkmachine.bean.db.ShopModelDB
import com.uroica.drinkmachine.databinding.FragmentMainBinding
import com.uroica.drinkmachine.db.CommonDaoUtils
import com.uroica.drinkmachine.db.DaoUtilsStore
import com.uroica.drinkmachine.gen.ShopModelDBDao
import com.uroica.drinkmachine.ui.sale.SalesPageActivity
import me.goldze.mvvmhabit.base.BaseFragment


class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {
    var ryShop: RecyclerView? = null
    var adapter: MainAdapter? = null
    private var shopPidList: List<String>? = null
    private var shopModelList: List<ShopModelDB>? = null
    var shopManagerDBUtils: CommonDaoUtils<ShopManagerDB>? = null
    private val mHandler = Handler()
    private var layoutManager: LinearLayoutManager? = null
    var saleActivity: SalesPageActivity?=null

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_main
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
        ryShop = binding.ryShop
        saleActivity= activity as SalesPageActivity?
//        val layoutManager = LooperLayoutManager()
//        layoutManager.setLooperEnable(true)
        layoutManager =  LinearLayoutManager(activity);
        layoutManager!!.orientation = LinearLayoutManager.HORIZONTAL;

        ryShop!!.layoutManager = layoutManager
        shopPidList =ArrayList()
        shopModelList =ArrayList()
        shopManagerDBUtils = DaoUtilsStore.getInstance().shopManagerDBUtils
        val lists = shopManagerDBUtils!!.queryAll()
        for (shopManagerDB in lists) {
            if (!shopPidList!!.contains(shopManagerDB.productID)) {
                //集合里面不包含pid 那么添加到展示页中
                //通过pid 去查询到对象
                (shopPidList as ArrayList<String>).add(shopManagerDB.productID)
                val sb = DaoUtilsStore.getInstance().shopDaoUtils
                    .queryByQueryBuilder(ShopModelDBDao.Properties.ProductID.eq(shopManagerDB.productID))[0]
                (shopModelList as ArrayList<ShopModelDB>).add(sb)
            }
        }
        adapter = MainAdapter(activity, shopModelList)
        ryShop!!.adapter = adapter
        binding.llMain.setOnClickListener(View.OnClickListener {
            saleActivity!!.commitAllowingStateLoss(1);
        })
    }

    var scrollRunnable: Runnable = object : Runnable {
        override fun run() {
            ryShop!!.scrollBy(1, 0)
            mHandler.postDelayed(this, 10)
        }
    }

    override fun onResume() {
        super.onResume()
        mHandler.postDelayed(scrollRunnable, 10)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(scrollRunnable)
    }

}