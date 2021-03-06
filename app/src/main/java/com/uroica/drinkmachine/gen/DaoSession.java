package com.uroica.drinkmachine.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.uroica.drinkmachine.bean.db.SaleRecordDB;
import com.uroica.drinkmachine.bean.db.ShopManagerDB;
import com.uroica.drinkmachine.bean.db.ShopModelDB;

import com.uroica.drinkmachine.gen.SaleRecordDBDao;
import com.uroica.drinkmachine.gen.ShopManagerDBDao;
import com.uroica.drinkmachine.gen.ShopModelDBDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig saleRecordDBDaoConfig;
    private final DaoConfig shopManagerDBDaoConfig;
    private final DaoConfig shopModelDBDaoConfig;

    private final SaleRecordDBDao saleRecordDBDao;
    private final ShopManagerDBDao shopManagerDBDao;
    private final ShopModelDBDao shopModelDBDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        saleRecordDBDaoConfig = daoConfigMap.get(SaleRecordDBDao.class).clone();
        saleRecordDBDaoConfig.initIdentityScope(type);

        shopManagerDBDaoConfig = daoConfigMap.get(ShopManagerDBDao.class).clone();
        shopManagerDBDaoConfig.initIdentityScope(type);

        shopModelDBDaoConfig = daoConfigMap.get(ShopModelDBDao.class).clone();
        shopModelDBDaoConfig.initIdentityScope(type);

        saleRecordDBDao = new SaleRecordDBDao(saleRecordDBDaoConfig, this);
        shopManagerDBDao = new ShopManagerDBDao(shopManagerDBDaoConfig, this);
        shopModelDBDao = new ShopModelDBDao(shopModelDBDaoConfig, this);

        registerDao(SaleRecordDB.class, saleRecordDBDao);
        registerDao(ShopManagerDB.class, shopManagerDBDao);
        registerDao(ShopModelDB.class, shopModelDBDao);
    }
    
    public void clear() {
        saleRecordDBDaoConfig.clearIdentityScope();
        shopManagerDBDaoConfig.clearIdentityScope();
        shopModelDBDaoConfig.clearIdentityScope();
    }

    public SaleRecordDBDao getSaleRecordDBDao() {
        return saleRecordDBDao;
    }

    public ShopManagerDBDao getShopManagerDBDao() {
        return shopManagerDBDao;
    }

    public ShopModelDBDao getShopModelDBDao() {
        return shopModelDBDao;
    }

}
