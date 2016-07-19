package com.example.dribbbleapiservicedemo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.databinding.ActivityWebViewBinding;
import com.thefinestartist.finestwebview.FinestWebView;

public class WebViewActivity extends AppCompatActivity {

    private ActivityWebViewBinding mBinding;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);

        getIntentParams();
        initEvents();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getDataString().replace("sqsong://", "https://");
        }
        Log.e("sqsong", "Url ---> " + mUrl);
    }

    private void initEvents() {
        setSupportActionBar(mBinding.toolbar);
        int userId = -1;
        try {
            String idStr = mUrl.substring(mUrl.lastIndexOf("/") + 1);
            userId = Integer.parseInt(idStr);
            Log.e("sqsong", "userId ---> " + userId);
        } catch (Exception e){
            e.printStackTrace();
        }
        if (userId != -1) {
            Intent intent = new Intent(this, UserInfoActivity.class);
            intent.putExtra(UserInfoActivity.USER_ID, userId);
            startActivity(intent);
        } else {
            new FinestWebView.Builder(this)
                    .theme(R.style.AppTheme)
                    .toolbarScrollFlags(0)
                    .titleColorRes(R.color.finestWhite)
                    .titleDefault("Person Home Page")
                    .iconDefaultColorRes(R.color.finestWhite)
                    .disableIconMenu(true)
                    .show(mUrl);
        }
        finish();
    }
}
