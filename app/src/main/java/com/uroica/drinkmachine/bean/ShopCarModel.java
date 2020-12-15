package com.uroica.drinkmachine.bean;

import com.uroica.drinkmachine.bean.db.ShopModelDB;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

public class ShopCarModel implements Serializable {
    private static final long serialVersionUID = 24197231L;
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
    private Long Sid;

    private String HeartTime;

    private String DeviceID;
    private String ProductID;
    private String ProductName;
    private String EnName;
    private String Points;
    private String Price;
    private String CB;
    private String Detail;
    private String Store;
    private String ListIndex;
    private String ImgURL;
    private Long ID;
    private int Num;//数量

    public ShopCarModel(ShopModelDB dataBean, int num){
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
        this.ID = dataBean.getID();
        this.Num=num;
    }


    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
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
