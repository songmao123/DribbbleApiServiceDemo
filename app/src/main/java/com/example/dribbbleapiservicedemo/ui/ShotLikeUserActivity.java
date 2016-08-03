package com.example.dribbbleapiservicedemo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
<<<<<<< HEAD
=======
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
>>>>>>> master
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.adapter.QuickShotLikeUserAdapter;
import com.example.dribbbleapiservicedemo.databinding.ActivityShotLikeUserBinding;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.model.ShotLikeUser;
import com.example.dribbbleapiservicedemo.retrofit.DribbbleApiServiceFactory;
import com.example.dribbbleapiservicedemo.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

<<<<<<< HEAD
public class ShotLikeUserActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {
=======
public class ShotLikeUserActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnRecyclerViewItemClickListener {
>>>>>>> master

    public static final String SHOT_INFO = "shot";
    private ActivityShotLikeUserBinding mBinding;
    private List<ShotLikeUser> mShotLikeUsers = new ArrayList<>();
    private Shot mShot;
    private int startPage = 1;
    private QuickShotLikeUserAdapter mQuickAdapter;
    private Subscription mSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shot_like_user);

        getIntentParams();
        initEvents();
        initRecyclerView();
        getShotLikeUsers();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mShot = intent.getParcelableExtra(SHOT_INFO);
        }
    }

    private void initEvents() {
        if (mShot != null) {
            mBinding.setShot(mShot);
        }
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Shot Like User Lists");

        mBinding.shotLikeUserContainer.shotAuthorTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShotLikeUserActivity.this, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_INFO, mShot.user);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = mBinding.shotLikeUserContainer.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mQuickAdapter = new QuickShotLikeUserAdapter(R.layout.item_shot_like_user, mShotLikeUsers);
        mQuickAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mQuickAdapter.isFirstOnly(true);
        mQuickAdapter.setOnLoadMoreListener(this);
<<<<<<< HEAD
=======
        mQuickAdapter.setOnRecyclerViewItemClickListener(this);
>>>>>>> master
        mQuickAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mQuickAdapter.setLoadingView(getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) recyclerView.getParent(), false));
        recyclerView.setAdapter(mQuickAdapter);
    }

    @Override
<<<<<<< HEAD
=======
    public void onItemClick(View view, int i) {
        Pair<View, String> imagePair = new Pair<>(view.findViewById(R.id.avatar_iv), "image");
        Pair<View, String> textPair = new Pair<>(view.findViewById(R.id.name_tv), "text");
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imagePair, textPair);
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra(UserInfoActivity.USER_INFO, mShotLikeUsers.get(i).user);
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    @Override
>>>>>>> master
    public void onLoadMoreRequested() {
        startPage++;
        getShotLikeUsers();
    }

    private void getShotLikeUsers() {
        mSubscribe = DribbbleApiServiceFactory.createDribbbleService(null, Constants.DRIBBBLE_ACCESS_TOKEN)
            .getShotLikeUsers(mShot.id, startPage, Constants.PER_PAGE_COUNT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<ShotLikeUser>>() {
                @Override
                public void call(List<ShotLikeUser> shotLikeUsers) {
                    mBinding.shotLikeUserContainer.loadingContainer.setVisibility(View.GONE);
                    if (startPage == 1) {
                        mShotLikeUsers.clear();
                        mShotLikeUsers.addAll(shotLikeUsers);
                        mQuickAdapter.notifyDataSetChanged();
                    } else {
                        if (shotLikeUsers == null || shotLikeUsers.size() < 1) {
                            mQuickAdapter.notifyDataChangedAfterLoadMore(false);
                            View view = getLayoutInflater().inflate(R.layout.layout_no_more_data,
                                    (ViewGroup) mBinding.shotLikeUserContainer.recyclerView.getParent(), false);
                            mQuickAdapter.addFooterView(view);
                        } else {
                            mQuickAdapter.notifyDataChangedAfterLoadMore(shotLikeUsers, true);
                        }
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    if (throwable instanceof TimeoutException) {
                        Toast.makeText(ShotLikeUserActivity.this, "collect server time out ...", Toast.LENGTH_SHORT).show();
                        getShotLikeUsers();
                    }
                }
            });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
        }
    }
<<<<<<< HEAD
=======

>>>>>>> master
}
