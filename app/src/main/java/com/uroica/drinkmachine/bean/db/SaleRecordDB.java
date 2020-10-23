package com.uroica.drinkmachine.bean.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
   shipment_status  0失败 1成功
 */

@Entity
public class SaleRecordDB {
    @Id(autoincrement = true)
    private Long Sid;
    @Property(nameInDb = "orderID")
    String orderID;
    @Property(nameInDb = "time")
    long time;
    @Property(nameInDb = "from")
    int from;
    @Property(nameInDb = "productName")
    String productName;
    @Property(nameInDb = "price")
    String price;
    @Property(nameInDb = "shipment_status")
    int shipment_status;
    @Property(nameInDb = "month")
    int month;

    public SaleRecordDB(String orderID,long time, int from,ShopModelDB shopModelDB,int shipment_status) {
        this.orderID = orderID;
        this.time = time;
        this.from = from;
        this.productName = shopModelDB.getProductName();
        this.price = shopModelDB.getPrice();
        this.shipment_status=shipment_status;
        this.month=Integer.valueOf(getLong2Month(time));
    }
    public SaleRecordDB(String orderID,long time, int from,String productName,String price,int shipment_status) {
        this.orderID = orderID;
        this.time = time;
        this.from = from;
        this.productName = productName;
        this.price = price;
        this.shipment_status=shipment_status;
        this.month=Integer.valueOf(getLong2Month(time));
    }
    @Generated(hash = 1668946463)
    public SaleRecordDB(Long Sid, String orderID, long time, int from, String productName, String price,
            int shipment_status, int month) {
        this.Sid = Sid;
        this.orderID = orderID;
        this.time = time;
        this.from = from;
        this.productName = productName;
        this.price = price;
        this.shipment_status = shipment_status;
        this.month = month;
    }
    @Generated(hash = 628109274)
    public SaleRecordDB() {
    }

    public  String getLong2Month(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.CHINA);
        Date date = new Date(time);
        String dateStr = dateFormat.format(date);
        return dateStr;
    }
    public Long getSid() {
        return this.Sid;
    }
    public void setSid(Long Sid) {
        this.Sid = Sid;
    }
    public String getOrderID() {
        return this.orderID;
    }
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public int getFrom() {
        return this.from;
    }
    public void setFrom(int from) {
        this.from = from;
    }
    public String getProductName() {
        return this.productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getPrice() {
        return this.price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public int getShipment_status() {
        return this.shipment_status;
    }
    public void setShipment_status(int shipment_status) {
        this.shipment_status = shipment_status;
    }
    public int getMonth() {
        return this.month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
}
