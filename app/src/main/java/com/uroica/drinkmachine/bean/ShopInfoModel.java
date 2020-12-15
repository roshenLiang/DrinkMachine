package com.uroica.drinkmachine.bean;

import java.io.Serializable;

/*
    post给服务器的商品信息
 */
public class ShopInfoModel implements Serializable {
        String goods_id;
        String goods_name;
        String quantity;
        String price;

    public ShopInfoModel() {
    }

    public ShopInfoModel(String goods_id, String goods_name, String quantity, String price) {
        this.goods_id = goods_id;
        this.goods_name = goods_name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
