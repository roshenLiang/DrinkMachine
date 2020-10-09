package com.uroica.drinkmachine.bean;
 
import java.util.List;

/**
 * Created by Administrator on 2018/3/22.
 * URL就是广告地址，adtype是广告类型，0表示视频，1表示图片，pri表示优先级
 */

public class BannerModel {

    /**
     * Ret : 1
     * data : [{"AD_ID":"1","ADName":"售卖机介绍","URL":"https://payforpark.cn:9983/AD/ad1.mp4","ADType":"0","ExpDate":"2020/9/1 0:00:00","PRI":"0","UploadTime":"2020/7/20 0:00:00","FileSize":"6509775","ID":"1"}]
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

    public static class DataBean {
        /**
         * AD_ID : 1
         * ADName : 售卖机介绍
         * URL : https://payforpark.cn:9983/AD/ad1.mp4
         * ADType : 0
         * ExpDate : 2020/9/1 0:00:00
         * PRI : 0
         * UploadTime : 2020/7/20 0:00:00
         * FileSize : 6509775
         * ID : 1
         */

        private String AD_ID;
        private String ADName;
        private String URL;
        private String ADType;
        private String ExpDate;
        private String PRI;
        private String UploadTime;
        private String FileSize;
        private String ID;

        public String getAD_ID() {
            return AD_ID;
        }

        public void setAD_ID(String AD_ID) {
            this.AD_ID = AD_ID;
        }

        public String getADName() {
            return ADName;
        }

        public void setADName(String ADName) {
            this.ADName = ADName;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public String getADType() {
            return ADType;
        }

        public void setADType(String ADType) {
            this.ADType = ADType;
        }

        public String getExpDate() {
            return ExpDate;
        }

        public void setExpDate(String ExpDate) {
            this.ExpDate = ExpDate;
        }

        public String getPRI() {
            return PRI;
        }

        public void setPRI(String PRI) {
            this.PRI = PRI;
        }

        public String getUploadTime() {
            return UploadTime;
        }

        public void setUploadTime(String UploadTime) {
            this.UploadTime = UploadTime;
        }

        public String getFileSize() {
            return FileSize;
        }

        public void setFileSize(String FileSize) {
            this.FileSize = FileSize;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }
    }
}
