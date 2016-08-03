package com.example.dribbbleapiservicedemo.retrofit;

import android.text.TextUtils;

<<<<<<< HEAD
=======
import com.example.dribbbleapiservicedemo.BuildConfig;
>>>>>>> master
import com.example.dribbbleapiservicedemo.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
<<<<<<< HEAD
=======
import okhttp3.logging.HttpLoggingInterceptor;
>>>>>>> master
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sqsong on 16-7-10.
 */
public class DribbbleApiServiceFactory {

    public static DribbbleApiService createDribbbleService(String baseUrl, final String accessToken) {

<<<<<<< HEAD
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
=======
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
>>>>>>> master
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + accessToken).build();
                return chain.proceed(request);
            }
<<<<<<< HEAD
        }).build();
=======
        });

        if (BuildConfig.DEBUG) {
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }
>>>>>>> master

        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = Constants.DRIBBBLE_BASE_URL;
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
<<<<<<< HEAD
                .client(okHttpClient)
=======
                .client(okHttpBuilder.build())
>>>>>>> master
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        return retrofit.create(DribbbleApiService.class);
    }

}
