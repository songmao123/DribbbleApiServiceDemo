package com.example.dribbbleapiservicedemo.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.dribbbleapiservicedemo.ui.OAuthWebActivity;

/**
 * Created by 青松 on 2016/7/14.
 */
public class CommonUtils {

    public static CharSequence noTrailingwhiteLines(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    public static void startOauthWebActivity(Context context) {
        Intent loginIntent = new Intent(context, OAuthWebActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(loginIntent);
    }

}
