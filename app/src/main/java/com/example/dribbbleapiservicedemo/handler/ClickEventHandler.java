package com.example.dribbbleapiservicedemo.handler;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.dribbbleapiservicedemo.model.User;
import com.example.dribbbleapiservicedemo.ui.UserInfoActivity;

/**
 * Created by sqsong on 16-7-16.
 */
public class ClickEventHandler {

    public void onViewClicked(View view, User user) {
        Context context = view.getContext();
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(UserInfoActivity.USER_INFO, user);
        context.startActivity(intent);
    }

}
