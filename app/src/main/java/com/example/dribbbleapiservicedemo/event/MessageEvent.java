package com.example.dribbbleapiservicedemo.event;

import com.example.dribbbleapiservicedemo.model.User;

/**
 * Created by 青松 on 2016/7/21.
 */
public class MessageEvent {

    public final User user;

    public MessageEvent(User user) {
        this.user = user;
    }

}
