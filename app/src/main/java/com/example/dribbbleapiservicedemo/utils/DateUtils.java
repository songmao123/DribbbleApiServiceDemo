package com.example.dribbbleapiservicedemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 青松 on 2016/7/14.
 */
public class DateUtils {

    public static String getNormalFormatTime(String t) {
        String time = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = format.parse(t);
            format.applyPattern("MM/dd/yyyy HH:mm aaa");
            time = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

}
