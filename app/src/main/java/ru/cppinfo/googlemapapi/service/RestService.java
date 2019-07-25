package ru.cppinfo.googlemapapi.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import ru.cppinfo.googlemapapi.model.Parking;

public interface RestService {
    @GET("/get")
    Call<HashMap<Integer, Parking>> get();

    Gson gson = new GsonBuilder().setLenient().create();

    //home
/*
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://100.64.76.121:3349")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
*/
    //сервер

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://185.146.157.97:3349")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
}
