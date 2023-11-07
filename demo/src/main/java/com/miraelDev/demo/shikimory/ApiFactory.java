package com.miraelDev.demo.shikimory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {

    private static String BASE_URL = "https://shikimori.one/api/";
    private static Retrofit retrofit = new Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build();

    public static ApiCall apiService = retrofit.create(ApiCall.class);
}
