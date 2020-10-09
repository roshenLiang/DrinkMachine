package com.uroica.drinkmachine.rxnetwork;
 
import com.uroica.drinkmachine.bean.ShopNumModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ShopNumApiService {
//    @GET("/banner/json")
//    Observable<BannerModel> getAd();

    @FormUrlEncoded
    @POST("webservice.asmx/Fun_extra")
    Observable<ShopNumModel> setShopNum(@Field("DeviceID") String device, @Field("Usr") String usr, @Field("ProductID") String productID, @Field("NumofProduct") String numofProduct);
}
