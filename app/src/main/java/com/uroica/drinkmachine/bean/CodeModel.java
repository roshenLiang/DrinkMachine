package com.uroica.drinkmachine.bean;

import java.util.List;
 
public class CodeModel {
 
    /**
     * Ret : 1
     * Detail : [{"Code":"https://qr.alipay.com/bax02323fqhqmbacfeod0023","OrderNO":"20200708115456"}]
     */

    private String Ret;
    private List<DetailBean> Detail;

    public String getRet() {
        return Ret;
    }

    public void setRet(String Ret) {
        this.Ret = Ret;
    }

    public List<DetailBean> getDetail() {
        return Detail;
    }

    public void setDetail(List<DetailBean> Detail) {
        this.Detail = Detail;
    }

    public static class DetailBean {
        /**
         * Code : https://qr.alipay.com/bax02323fqhqmbacfeod0023
         * OrderNO : 20200708115456
         */

        private String Code;
        private String OrderNO;

        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }

        public String getOrderNO() {
            return OrderNO;
        }

        public void setOrderNO(String OrderNO) {
            this.OrderNO = OrderNO;
        }
    }
}
