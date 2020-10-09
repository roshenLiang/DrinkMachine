package com.uroica.drinkmachine.rxnetwork;
 
import com.uroica.drinkmachine.bean.BannerModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AdApiService {
//    @GET("/banner/json")
//    Observable<BannerModel> getAd();

    @FormUrlEncoded
    @POST("webservice.asmx/Load_AD")
    Observable<BannerModel> getAD( @Field("DeviceID") String device);
}
