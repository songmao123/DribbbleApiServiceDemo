package com.example.dribbbleapiservicedemo.utils;

import android.text.TextUtils;

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

}
