package com.uroica.drinkmachine.rxnetwork;
 
import com.uroica.drinkmachine.bean.ShopModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ShopApiService {
    @FormUrlEncoded
    @POST("webservice.asmx/ShowProduct")
    Observable<ShopModel> getShop(@Field("PageInt") String page,@Field("maxperpage") String maxPage,@Field("DeviceID") String api_type);

//    @FormUrlEncoded
//    @POST("webservice.asmx/ShowProduct")
//    Call<ResponseBody> getShopString(@Field("PageInt") String page, @Field("maxperpage") String maxPage, @Field("DeviceID") String api_type);



}
