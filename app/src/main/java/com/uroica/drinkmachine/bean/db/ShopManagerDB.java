package com.uroica.drinkmachine.bean.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class ShopManagerDB {
    @Id(autoincrement = true)
    private Long Sid;

    @Property(nameInDb = "CabinetID")
    private int CabinetID;
    @Property(nameInDb = "StockNum")
    private String StockNum;
    @Property(nameInDb = "ChannleID")
    private String ChannleID;
    @Property(nameInDb = "Combination")
    private String Combination;
    @Property(nameInDb = "ChannelFault")
    private String ChannelFault;

    @Property(nameInDb = "DeviceID")
    private String DeviceID;
    @Property(nameInDb = "ProductID")
    private String ProductID;
    @Property(nameInDb = "ProductName")
    private String ProductName;
    @Property(nameInDb = "EnName")
    private String EnName;
    @Property(nameInDb = "Points")
    private String Points;
    @Property(nameInDb = "Price")
    private String Price;
    @Property(nameInDb = "CB")
    private String CB;
    @Property(nameInDb = "Detail")
    private String Detail;
    @Property(nameInDb = "Store")
    private String Store;
    @Property(nameInDb = "ListIndex")
    private String ListIndex;
    @Property(nameInDb = "ImgURL")
    private String ImgURL;
    @Property(nameInDb = "ID")
    private Long ID;
    public ShopManagerDB(ShopModelDB shopModelDB,int ChannleID,int CabinetID) {
        this.CabinetID = CabinetID;
        this.DeviceID = shopModelDB.getDeviceID();
        this.ProductID = shopModelDB.getProductID();
        this.ProductName = shopModelDB.getProductName();
        this.EnName = shopModelDB.getEnName();
        this.Points = shopModelDB.getPoints();
        this.Price = shopModelDB.getPrice();
        this.CB = shopModelDB.getCB();
        this.Detail = shopModelDB.getDetail();
        this.Store = shopModelDB.getStore();
        this.ListIndex = shopModelDB.getListIndex();
        this.ImgURL = shopModelDB.getImgURL();
        this.ID = shopModelDB.getID();
        this.StockNum="0";
        this.ChannleID=String.valueOf(ChannleID);
        this.Combination="1";
        this.ChannelFault="0";
    }




    @Generated(hash = 1798870168)
    public ShopManagerDB(Long Sid, int CabinetID, String StockNum, String ChannleID,
            String Combination, String ChannelFault, String DeviceID,
            String ProductID, String ProductName, String EnName, String Points,
            String Price, String CB, String Detail, String Store, String ListIndex,
            String ImgURL, Long ID) {
        this.Sid = Sid;
        this.CabinetID = CabinetID;
        this.StockNum = StockNum;
        this.ChannleID = ChannleID;
        this.Combination = Combination;
        this.ChannelFault = ChannelFault;
        this.DeviceID = DeviceID;
        this.ProductID = ProductID;
        this.ProductName = ProductName;
        this.EnName = EnName;
        this.Points = Points;
        this.Price = Price;
        this.CB = CB;
        this.Detail = Detail;
        this.Store = Store;
        this.ListIndex = ListIndex;
        this.ImgURL = ImgURL;
        this.ID = ID;
    }




    @Generated(hash = 1283715975)
    public ShopManagerDB() {
    }




    public void setShopModel(ShopModelDB shopModelDB){
        this.DeviceID = shopModelDB.getDeviceID();
        this.ProductID = shopModelDB.getProductID();
        this.ProductName = shopModelDB.getProductName();
        this.EnName = shopModelDB.getEnName();
        this.Points = shopModelDB.getPoints();
        this.Price = shopModelDB.getPrice();
        this.CB = shopModelDB.getCB();
        this.Detail = shopModelDB.getDetail();
        this.Store = shopModelDB.getStore();
        this.ListIndex = shopModelDB.getListIndex();
        this.ImgURL = shopModelDB.getImgURL();
        this.ID = shopModelDB.getID();
    }


    public Long getSid() {
        return this.Sid;
    }


    public void setSid(Long Sid) {
        this.Sid = Sid;
    }


    public String getStockNum() {
        return this.StockNum;
    }


    public void setStockNum(String StockNum) {
        this.StockNum = StockNum;
    }


    public String getChannleID() {
        return this.ChannleID;
    }


    public void setChannleID(String ChannleID) {
        this.ChannleID = ChannleID;
    }


    public String getDeviceID() {
        return this.DeviceID;
    }


    public void setDeviceID(String DeviceID) {
        this.DeviceID = DeviceID;
    }


    public String getProductID() {
        return this.ProductID;
    }


    public void setProductID(String ProductID) {
        this.ProductID = ProductID;
    }


    public String getProductName() {
        return this.ProductName;
    }


    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }


    public String getEnName() {
        return this.EnName;
    }


    public void setEnName(String EnName) {
        this.EnName = EnName;
    }


    public String getPoints() {
        return this.Points;
    }


    public void setPoints(String Points) {
        this.Points = Points;
    }


    public String getPrice() {
        return this.Price;
    }


    public void setPrice(String Price) {
        this.Price = Price;
    }


    public String getCB() {
        return this.CB;
    }


    public void setCB(String CB) {
        this.CB = CB;
    }


    public String getDetail() {
        return this.Detail;
    }


    public void setDetail(String Detail) {
        this.Detail = Detail;
    }


    public String getStore() {
        return this.Store;
    }


    public void setStore(String Store) {
        this.Store = Store;
    }


    public String getListIndex() {
        return this.ListIndex;
    }


    public void setListIndex(String ListIndex) {
        this.ListIndex = ListIndex;
    }


    public String getImgURL() {
        return this.ImgURL;
    }


    public void setImgURL(String ImgURL) {
        this.ImgURL = ImgURL;
    }


    public Long getID() {
        return this.ID;
    }


    public void setID(Long ID) {
        this.ID = ID;
    }


    public String getCombination() {
        return this.Combination;
    }


    public void setCombination(String Combination) {
        this.Combination = Combination;
    }


    public String getChannelFault() {
        return this.ChannelFault;
    }


    public void setChannelFault(String ChannelFault) {
        this.ChannelFault = ChannelFault;
    }


    public int getCabinetID() {
        return this.CabinetID;
    }


    public void setCabinetID(int CabinetID) {
        this.CabinetID = CabinetID;
    }


}
