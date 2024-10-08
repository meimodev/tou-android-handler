/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler;

import android.util.Log;

import com.github.squti.guru.Guru;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private Retrofit retrofit;
    private static RetrofitClient retrofitClient;
    private static final String BASE_URL = Constant.ROOT_URL_API;

    private RetrofitClient(String token) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Cache-Control", "max-age=640000")
                    .build();
            return chain.proceed(newRequest);
        }).connectTimeout(4, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.e(TAG, "RetrofitClient: with token build, token=Bearer " + token);
    }

    public static synchronized RetrofitClient getInstance(String token) {

        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient(Guru.getString(Constant.KEY_USER_ACCESS_TOKEN, ""));
        }
        return retrofitClient;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public static void reBuiltRetrofitClient() {
        retrofitClient = null;
    }

    public ApiServices getApiServices() {
        return retrofit.create(ApiServices.class);
    }

}
