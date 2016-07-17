package com.example.dribbbleapiservicedemo.retrofit;

import android.text.TextUtils;
import android.util.Log;

import com.example.dribbbleapiservicedemo.utils.Constants;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sqsong on 16-7-10.
 */
public class DribbbleApiServiceFactory {

    public static DribbbleApiService createDribbbleService(String baseUrl, final String accessToken) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + accessToken).build();
//                Response response = chain.proceed(request);
//                Log.w("sqsong", "Response ----------> " + new Gson().toJson(chain.proceed(request)));
                return chain.proceed(request);
            }
        }).build();

        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = Constants.DRIBBBLE_BASE_URL;
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        return retrofit.create(DribbbleApiService.class);
    }

}
