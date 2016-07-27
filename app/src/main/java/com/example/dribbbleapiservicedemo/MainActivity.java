package com.example.dribbbleapiservicedemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dribbbleapiservicedemo.databinding.ActivityMainBinding;
import com.example.dribbbleapiservicedemo.databinding.NavHeaderMainBinding;
import com.example.dribbbleapiservicedemo.event.FilterShotsEvent;
import com.example.dribbbleapiservicedemo.event.MessageEvent;
import com.example.dribbbleapiservicedemo.fragment.ProjectFragment;
import com.example.dribbbleapiservicedemo.fragment.ShotsFragment;
import com.example.dribbbleapiservicedemo.fragment.TeamFragment;
import com.example.dribbbleapiservicedemo.model.User;
import com.example.dribbbleapiservicedemo.ui.BaseActivity;
import com.example.dribbbleapiservicedemo.ui.OAuthWebActivity;
import com.example.dribbbleapiservicedemo.ui.ShotsFilterPopupWindow;
import com.example.dribbbleapiservicedemo.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, ShotsFilterPopupWindow.OnFilterItemClickListener {

    private ActivityMainBinding mBinding;
    private NavHeaderMainBinding mNavHeaderBinding;
    private List<Fragment> mFragments = new ArrayList<>();
    private User mUserInfo;
    private String oauthAccessToken;
    private ShotsFilterPopupWindow mFilterPop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initEvents();
        initFragments();
    }

    private void initEvents() {
        EventBus.getDefault().register(this);
        Toolbar toolbar = mBinding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        mBinding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = mBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(this);

        mNavHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.nav_header_main, mBinding.navView, false);
        mNavHeaderBinding.setUser(mUserInfo);
        mBinding.navView.addHeaderView(mNavHeaderBinding.navContainer);
        mNavHeaderBinding.accountImageIv.setOnClickListener(this);
    }

    private void initFragments() {
        mFragments.add(ShotsFragment.newInstance());
        mFragments.add(TeamFragment.newInstance());
        mFragments.add(ProjectFragment.newInstance());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, mFragments.get(0)).commit();
        mBinding.navView.setCheckedItem(R.id.nav_camera);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = mBinding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            if (mFilterPop == null) {
                mFilterPop = new ShotsFilterPopupWindow(this, this);
            }
            mFilterPop.showPop(mBinding.appBarMain.toolbar);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFilterItemClicked(int position) {
        EventBus.getDefault().post(new FilterShotsEvent(position));
        if (mFilterPop != null && mFilterPop.isShowing()) {
            mFilterPop.dismiss();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            showSelectedFragment(0);
            mBinding.appBarMain.toolbar.getMenu().clear();
            mBinding.appBarMain.toolbar.inflateMenu(R.menu.main);
        } else if (id == R.id.nav_gallery) {
            showSelectedFragment(1);
            mBinding.appBarMain.toolbar.getMenu().clear();
        } else if (id == R.id.nav_slideshow) {
            showSelectedFragment(2);
            mBinding.appBarMain.toolbar.getMenu().clear();
        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSelectedFragment(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            if (i != position) {
                transaction.hide(fragment);
            } else {
                if (!fragment.isAdded()) {
                    transaction.add(R.id.content, fragment);
                }
                transaction.show(fragment);
            }
        }
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accountImageIv:
                if (isLogin()) {
                    return;
                }
                Intent intent = new Intent(this, OAuthWebActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        mUserInfo = event.user;
        if (mUserInfo != null) {
            mNavHeaderBinding.setUser(mUserInfo);
            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLogin() {
        oauthAccessToken = mPreferencesHelper.getString(Constants.OAUTH_ACCESS_TOKE);
        return TextUtils.isEmpty(oauthAccessToken) ? false : true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreferencesHelper.put(Constants.OAUTH_ACCESS_TOKE, "");
        EventBus.getDefault().unregister(this);
    }

}
