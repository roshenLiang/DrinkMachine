package com.uroica.drinkmachine.rxnetwork;
 
import com.uroica.drinkmachine.bean.CodeModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CodeApiService {
    @FormUrlEncoded
    @POST("webservice.asmx/RetAliCode")
    Observable<CodeModel> getAliCode(@Field("ProductName") String productName, @Field("Price") String pPrice,
                                  @Field("ProductID") String productID, @Field("ShowURL") String showURL,
                                  @Field("Detail") String detail,@Field("DeviceID") String deviceID);
    @FormUrlEncoded
    @POST("webservice.asmx/RetWechatCode")
    Observable<CodeModel> getWxCode(@Field("ProductName") String productName, @Field("Price") String pPrice,
                                    @Field("ProductID") String productID,@Field("DeviceID") String deviceID,
                                    @Field("BoardID") String BoardID  ,@Field("RoundID") String RoundID);
}
