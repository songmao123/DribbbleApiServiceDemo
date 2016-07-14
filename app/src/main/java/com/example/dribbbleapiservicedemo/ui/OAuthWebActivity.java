package com.example.dribbbleapiservicedemo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.dribbbleapiservicedemo.MainActivity;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.databinding.ActivityOauthWebBinding;
import com.example.dribbbleapiservicedemo.model.TokenData;
import com.example.dribbbleapiservicedemo.model.User;
import com.example.dribbbleapiservicedemo.retrofit.DribbbleApiServiceFactory;
import com.example.dribbbleapiservicedemo.utils.CompatUri;
import com.example.dribbbleapiservicedemo.utils.Constants;

import java.net.URLEncoder;
import java.util.Set;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class OAuthWebActivity extends BaseActivity {

    private ActivityOauthWebBinding mOauthBinding;
    private String authAccessToken;
    private Subscription mSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOauthBinding = DataBindingUtil.setContentView(this, R.layout.activity_oauth_web);

        initEvents();
    }

    private void initEvents() {
        setSupportActionBar(mOauthBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        WebSettings webSetting = mOauthBinding.webView.getSettings();
        webSetting.setSaveFormData(false);
        webSetting.setJavaScriptEnabled(true);

        mOauthBinding.webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                WebView wv = (WebView) v;
                if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) {
                    wv.goBack();
                    return true;
                }
                return false;
            }
        });

        mOauthBinding.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    mOauthBinding.progressValue.setText(newProgress + "%");
                    mOauthBinding.loadingProgress.setVisibility(View.VISIBLE);
                } else {
                    mOauthBinding.loadingProgress.setVisibility(View.GONE);
                }
                Log.e("sqsong", "WebView Load Progress: " + newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });

        mOauthBinding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                interceptUrlCompat(view, url);
                return true;
            }

            private void interceptUrlCompat(WebView view, String url) {
                if (isRedirectUriFound(url, Constants.DRIBBBLE_CLIENT_REDIRECT_URL)) {
                    Uri uri = Uri.parse(url);
                    String tokenCode = uri.getQueryParameter(Constants.PARAM_CODE);
                    if (TextUtils.isEmpty(tokenCode)) {
                        Log.e("sqsong", "TokenCode is null");
                        return;
                    }
                    getTokenAndUserInfo(tokenCode);
                    return;
                }
                Log.i("sqsong", "url---> " + url);
                view.loadUrl(url);
            }
        });

        String loadUrl = getAuthorizationUrl();
        mOauthBinding.webView.loadUrl(loadUrl);
    }

    private void getTokenAndUserInfo(final String tokenCode) {
        mSubscribe = getTokenObservable(tokenCode).flatMap(new Func1<TokenData, Observable<User>>() {
            @Override
            public Observable<User> call(TokenData tokenData) {
                authAccessToken = tokenData.access_token;
                mPreferencesHelper.put(Constants.OAUTH_ACCESS_TOKE, authAccessToken);
                return getUserInfoObservable(authAccessToken);
            }
        })
                .subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        Intent intent = new Intent();
                        intent.putExtra(MainActivity.OAUTH_USER_INFO, user);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }

    private Observable<TokenData> getTokenObservable(String tokenCode) {
        return DribbbleApiServiceFactory.createDribbbleService(Constants.DRIBBBLE_TOKEN_URL, Constants.DRIBBBLE_ACCESS_TOKEN)
                .getToken(Constants.DRIBBBLE_CLIENT_ID, Constants.DRIBBBLE_CLIENT_SECRET,
                        tokenCode, Constants.DRIBBBLE_CLIENT_REDIRECT_URL);
    }

    private Observable<User> getUserInfoObservable(String authAccessToken) {
        return DribbbleApiServiceFactory.createDribbbleService(null, authAccessToken).getAuthenticatedUser();
    }

    private String getAuthorizationUrl() {
        String url = Constants.AUTHORIZE_URL + "?client_id=" + Constants.DRIBBBLE_CLIENT_ID + "&redirect_uri=" + Constants.DRIBBBLE_CLIENT_REDIRECT_URL +
                "&response_type=code&scope=" + URLEncoder.encode("public write upload comment");
        Log.i("sqsong", "Authorization Url: " + url);
        return url;
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

    static boolean isRedirectUriFound(String uri, String redirectUri) {
        Uri u = null;
        Uri r = null;
        try {
            u = Uri.parse(uri);
            r = Uri.parse(redirectUri);
        } catch (NullPointerException e) {
            return false;
        }
        if (u == null || r == null) {
            return false;
        }
        boolean rOpaque = r.isOpaque();
        boolean uOpaque = u.isOpaque();
        if (rOpaque != uOpaque) {
            return false;
        }
        if (rOpaque) {
            return TextUtils.equals(uri, redirectUri);
        }
        if (!TextUtils.equals(r.getScheme(), u.getScheme())) {
            return false;
        }
        if (!TextUtils.equals(r.getAuthority(), u.getAuthority())) {
            return false;
        }
        if (r.getPort() != u.getPort()) {
            return false;
        }
        if (!TextUtils.isEmpty(r.getPath()) && !TextUtils.equals(r.getPath(), u.getPath())) {
            return false;
        }
        Set<String> paramKeys = CompatUri.getQueryParameterNames(r);
        for (String key : paramKeys) {
            if (!TextUtils.equals(r.getQueryParameter(key), u.getQueryParameter(key))) {
                return false;
            }
        }
        String frag = r.getFragment();
        if (!TextUtils.isEmpty(frag)
                && !TextUtils.equals(frag, u.getFragment())) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
        }
    }
}
