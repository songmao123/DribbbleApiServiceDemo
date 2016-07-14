package com.example.dribbbleapiservicedemo.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.example.dribbbleapiservicedemo.utils.PreferencesHelper;

/**
 * Created by 青松 on 2016/7/13.
 */
public class BaseActivity extends AppCompatActivity {

    public PreferencesHelper mPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferencesHelper = new PreferencesHelper(this);
    }

}
