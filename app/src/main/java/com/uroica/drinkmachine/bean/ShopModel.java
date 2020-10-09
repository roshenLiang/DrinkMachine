package com.uroica.drinkmachine.bean;
 
import java.io.Serializable;
import java.util.List;
 
public class ShopModel implements Serializable {
    private static final long serialVersionUID = 2378118456L;
    /**
     * Ret : 1
     * data : [{"DeviceID":"0102030405060708090A","ProductID":"U1","ProductName":"å\u008f¯ä¹\u0090","EnName":"","Points":"-1","Price":"3.0000","CB":"1.0000","Detail":"å\u2020»å\u008f¯ä¹\u0090","Store":"23","ListIndex":"0","ImgURL":"http://140.143.5.187:9000/Img/ProductImg/cola.jpg","ID":"1"}]
     */

    private String Ret;
    private List<DataBean> data;

    public String getRet() {
        return Ret;
    }

    public void setRet(String Ret) {
        this.Ret = Ret;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public static class DataBean implements Serializable {
        private static final long serialVersionUID = 23018456L;
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
         * Heattime : 1
         * ID : 1
         */
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
        private String Heattime;
        private Long ID;

        public String getDeviceID() {
            return DeviceID;
        }

        public void setDeviceID(String DeviceID) {
            this.DeviceID = DeviceID;
        }

        public String getProductID() {
            return ProductID;
        }

        public void setProductID(String ProductID) {
            this.ProductID = ProductID;
        }

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String ProductName) {
            this.ProductName = ProductName;
        }

        public String getEnName() {
            return EnName;
        }

        public void setEnName(String EnName) {
            this.EnName = EnName;
        }

        public String getPoints() {
            return Points;
        }

        public void setPoints(String Points) {
            this.Points = Points;
        }

        public String getPrice() {
            return Price;
        }

        public void setPrice(String Price) {
            this.Price = Price;
        }

        public String getCB() {
            return CB;
        }

        public void setCB(String CB) {
            this.CB = CB;
        }

        public String getDetail() {
            return Detail;
        }

        public void setDetail(String Detail) {
            this.Detail = Detail;
        }

        public String getStore() {
            return Store;
        }

        public void setStore(String Store) {
            this.Store = Store;
        }

        public String getListIndex() {
            return ListIndex;
        }

        public void setListIndex(String ListIndex) {
            this.ListIndex = ListIndex;
        }

        public String getImgURL() {
            return ImgURL;
        }

        public void setImgURL(String ImgURL) {
            this.ImgURL = ImgURL;
        }

        public String getHeattime() {
            return Heattime;
        }

        public void setHeattime(String Heattime) {
            this.Heattime = Heattime;
        }

        public Long getID() {
            return ID;
        }

        public void setID(Long ID) {
            this.ID = ID;
        }
    }
}
