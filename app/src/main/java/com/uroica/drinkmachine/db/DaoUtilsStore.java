package com.uroica.drinkmachine.db;
 
import com.uroica.drinkmachine.bean.db.SaleRecordDB;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;
import com.uroica.drinkmachine.gen.SaleRecordDBDao;
import com.uroica.drinkmachine.gen.ShopManagerDBDao;
import com.uroica.drinkmachine.gen.ShopModelDBDao;

public class DaoUtilsStore {
    private volatile static DaoUtilsStore instance = new DaoUtilsStore();
    private CommonDaoUtils<ShopModelDB> shopDaoUtils;
    private CommonDaoUtils<ShopManagerDB> shopManagerDBUtils;
    private CommonDaoUtils<SaleRecordDB> saleRecordDBUtils;
  
    public static DaoUtilsStore getInstance()
    {
        return instance;
    }

    private DaoUtilsStore()
    {
        DaoManager mManager = DaoManager.getInstance();
        ShopModelDBDao shopModelDBDao = mManager.getDaoSession().getShopModelDBDao();
        ShopManagerDBDao shopManagerDBDao = mManager.getDaoSession().getShopManagerDBDao();
        SaleRecordDBDao saleRecordDBDao = mManager.getDaoSession().getSaleRecordDBDao();
        shopDaoUtils = new CommonDaoUtils(ShopModelDB.class, shopModelDBDao);
        shopManagerDBUtils = new CommonDaoUtils(ShopManagerDB.class, shopManagerDBDao);
        saleRecordDBUtils = new CommonDaoUtils(SaleRecordDB.class, saleRecordDBDao);

    }

    public CommonDaoUtils<ShopModelDB> getShopDaoUtils()
    {
        return shopDaoUtils;
    }
    public CommonDaoUtils<ShopManagerDB> getShopManagerDBUtils()
    {
        return shopManagerDBUtils;
    }
    public CommonDaoUtils<SaleRecordDB> getSaleRecordDBUtils()
    {
        return saleRecordDBUtils;
    }

}
