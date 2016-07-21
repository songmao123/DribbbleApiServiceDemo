package com.example.dribbbleapiservicedemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dribbbleapiservicedemo.adapter.QuickShotsAdapter;
import com.example.dribbbleapiservicedemo.databinding.ActivityMainBinding;
import com.example.dribbbleapiservicedemo.databinding.NavHeaderMainBinding;
import com.example.dribbbleapiservicedemo.db.ShotsDBManaber;
import com.example.dribbbleapiservicedemo.event.MessageEvent;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.model.User;
import com.example.dribbbleapiservicedemo.retrofit.DribbbleApiServiceFactory;
import com.example.dribbbleapiservicedemo.ui.BaseActivity;
import com.example.dribbbleapiservicedemo.ui.OAuthWebActivity;
import com.example.dribbbleapiservicedemo.ui.ShotDetailActivity;
import com.example.dribbbleapiservicedemo.utils.Constants;
import com.example.dribbbleapiservicedemo.utils.DensityUtil;
import com.example.dribbbleapiservicedemo.utils.GridItemDecoration;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnRecyclerViewItemClickListener {

    public static final String OAUTH_USER_INFO = "oauth_user_info";
    private ActivityMainBinding mBinding;
    private SwipeRefreshLayout mSwipLayout;
    private NavHeaderMainBinding mNavHeaderBinding;
    private List<Shot> mShots = new ArrayList<>();

    private QuickShotsAdapter mShotsAdapter;
    private String sort = Constants.SORT_POPULAR;
    private Subscription mSubscription;
    private User mUserInfo;
    private String oauthAccessToken;
    private int startPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initEvents();
        initRecyclerView();
        loadDataFromDB();
        getShotsDatas();
    }

    private void initRecyclerView() {
        mSwipLayout = mBinding.appBarMain.mainContent.swipLayout;
        RecyclerView mRecyclerView = mBinding.appBarMain.mainContent.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        GridItemDecoration decoration = new GridItemDecoration(2, DensityUtil.dip2px(Constants.ITEM_SPACING_DP), true);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(layoutManager);

        mShotsAdapter = new QuickShotsAdapter(R.layout.item_shots, mShots);
        mShotsAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mShotsAdapter.isFirstOnly(true);
        mShotsAdapter.setOnLoadMoreListener(this);
        mShotsAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mShotsAdapter.setOnRecyclerViewItemClickListener(this);
        mShotsAdapter.setLoadingView(getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) mRecyclerView.getParent(), false));
        mRecyclerView.setAdapter(mShotsAdapter);

        mSwipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startPage = 1;
                getShotsDatas();
            }
        });
    }

    private void initEvents() {
        EventBus.getDefault().register(this);
        Toolbar toolbar = mBinding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        mBinding.appBarMain.mainContent.progressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(this).build());

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            getSortShotsData(0);
        } else if (id == R.id.nav_gallery) {
            getSortShotsData(1);
        } else if (id == R.id.nav_slideshow) {
            getSortShotsData(2);
        } else if (id == R.id.nav_manage) {
            getSortShotsData(3);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getSortShotsData(int type) {
        switch (type) {
            case 0:
                sort = Constants.SORT_POPULAR;
                break;
            case 1:
                sort = Constants.SORT_RECENT;
                break;
            case 2:
                sort = Constants.SORT_COMMENTS;
                break;
            case 3:
                sort = Constants.SORT_VIEWS;
                break;
        }
        startPage = 1;
        mShots.clear();
        mShotsAdapter.notifyDataSetChanged();
        mBinding.appBarMain.mainContent.loadingContainer.setVisibility(View.VISIBLE);
        getShotsDatas();
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

    private void getShotsDatas() {
        mSubscription = getShotsFromNetObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Shot>>() {
                    @Override
                    public void onCompleted() {
                        mBinding.appBarMain.mainContent.loadingContainer.setVisibility(View.GONE);
                        mSwipLayout.setRefreshing(false);
                        Log.e("sqsong", "Request Complete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sqsong", "Error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Shot> shots) {
                        if (startPage == 1) {
                            mShots.clear();
                            mShots.addAll(shots);
                            mShotsAdapter.notifyDataSetChanged();
                            if (shots != null && shots.size() > 0) {
                                saveShotsToDB(shots);
                            }
                        } else {
                            mShotsAdapter.notifyDataChangedAfterLoadMore(shots, true);
                        }
                    }
                });
    }

    private void loadDataFromDB() {
        getShotsFromDBObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Shot>>() {
                    @Override
                    public void onStart() {
                        mBinding.appBarMain.mainContent.loadingContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                        mBinding.appBarMain.mainContent.loadingContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Shot> shots) {
                        mShots.clear();
                        mShots.addAll(shots);
                        mShotsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private Observable<List<Shot>> getShotsFromNetObservable() {
        return DribbbleApiServiceFactory.createDribbbleService(null, Constants.DRIBBBLE_ACCESS_TOKEN)
                .getShots(startPage, Constants.PER_PAGE_COUNT, sort);
    }

    private Observable<List<Shot>> getShotsFromDBObservable() {
        return Observable.just("").map(new Func1<String, List<Shot>>() {
            @Override
            public List<Shot> call(String s) {
                List<Shot> shots = ShotsDBManaber.getInstance(getApplicationContext()).queryAllShots();
                Log.e("sqsong", "Query Shots Size: " + shots.size());
                return shots;
            }
        });
    }

    private void saveShotsToDB(final List<Shot> shots) {
        ShotsDBManaber.getInstance(getApplicationContext()).deleteOldDatas();
        ShotsDBManaber.getInstance(getApplicationContext()).saveShotLists(shots);
    }

    @Override
    public void onLoadMoreRequested() {
        startPage++;
        getShotsDatas();
    }

    @Override
    public void onItemClick(View view, int i) {
        Pair<View, String> imagePair = new Pair<>(view.findViewById(R.id.imageView), "image");
        Pair<View, String> textPair = new Pair<>(view.findViewById(R.id.titleTv), "text");
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imagePair, textPair);
        Intent intent = new Intent(this, ShotDetailActivity.class);
        intent.putExtra(ShotDetailActivity.SHOT_DATA, mShots.get(i));
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    private boolean isLogin() {
        oauthAccessToken = mPreferencesHelper.getString(Constants.OAUTH_ACCESS_TOKE);
        return TextUtils.isEmpty(oauthAccessToken) ? false : true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreferencesHelper.put(Constants.OAUTH_ACCESS_TOKE, "");
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        EventBus.getDefault().unregister(this);
    }

}
