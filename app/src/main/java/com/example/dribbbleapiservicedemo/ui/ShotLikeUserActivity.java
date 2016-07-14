package com.example.dribbbleapiservicedemo.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;

import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.databinding.ActivityShotLikeUserBinding;

public class ShotLikeUserActivity extends BaseActivity {

    private ActivityShotLikeUserBinding mBingding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBingding = DataBindingUtil.setContentView(this, R.layout.activity_shot_like_user);

        getIntentParams();
        initEvents();
    }

    private void getIntentParams() {

    }

    private void initEvents() {
        setSupportActionBar(mBingding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
