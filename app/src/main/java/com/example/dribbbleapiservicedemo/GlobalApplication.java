package com.example.dribbbleapiservicedemo;

import android.app.Application;

import com.example.dribbbleapiservicedemo.model.User;

/**
 * Created by 青松 on 2016/7/18.
 */
public class GlobalApplication extends Application {

    private User mUserInfo;
    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static GlobalApplication getInstance() {
        return instance;
    }

    public User getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(User mUserInfo) {
        this.mUserInfo = mUserInfo;
    }
}
