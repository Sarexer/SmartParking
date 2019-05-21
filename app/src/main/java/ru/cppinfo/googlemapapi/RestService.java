package ru.cppinfo.googlemapapi;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestService {
    @GET("/get")
    Call<Object> get();


    //home
/*
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://100.64.42.29:3347")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
*/
    //сервер
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://185.146.157.97:3349")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
