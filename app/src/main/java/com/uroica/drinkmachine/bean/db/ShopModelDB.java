package com.uroica.drinkmachine.bean.db;

import com.uroica.drinkmachine.bean.ShopModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity
public class ShopModelDB  implements Serializable { 
    private static final long serialVersionUID = 241971L;
    /**
     * DeviceID : 0102030405060708090A
     * ProductID : U1
     * ProductName : å¯ä¹
     * EnName :
     * Points : -1
     * Price : 3.0000
     * CB : 1.0000
     * Detail : å†»å¯ä¹
     * Store : 23
     * ListIndex : 0
     * ImgURL : http://140.143.5.187:9000/Img/ProductImg/cola.jpg
     * ID : 1
     */
    @Id(autoincrement = true)
    private Long Sid;

    @Property(nameInDb = "HeartTime")
    private String HeartTime;

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

    public ShopModelDB(ShopModel.DataBean dataBean){
        this.DeviceID = dataBean.getDeviceID();
        this.ProductID = dataBean.getProductID();
        this.ProductName = dataBean.getProductName();
        this.EnName = dataBean.getEnName();
        this.Points = dataBean.getPoints();
        this.Price = dataBean.getPrice();
        this.CB = dataBean.getCB();
        this.Detail = dataBean.getDetail();
        this.Store = dataBean.getStore();
        this.ListIndex = dataBean.getListIndex();
        this.ImgURL = dataBean.getImgURL();
        //加热时间跟服务器没关
//        this.HeartTime = dataBean.getHeattime();
        this.ID = dataBean.getID();
    }


    @Generated(hash = 1975347264)
    public ShopModelDB(Long Sid, String HeartTime, String DeviceID,
            String ProductID, String ProductName, String EnName, String Points,
            String Price, String CB, String Detail, String Store, String ListIndex,
            String ImgURL, Long ID) {
        this.Sid = Sid;
        this.HeartTime = HeartTime;
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


    @Generated(hash = 107453549)
    public ShopModelDB() {
    }


    public Long getSid() {
        return this.Sid;
    }

    public void setSid(Long Sid) {
        this.Sid = Sid;
    }

    public String getHeartTime() {
        return this.HeartTime;
    }

    public void setHeartTime(String HeartTime) {
        this.HeartTime = HeartTime;
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




}
