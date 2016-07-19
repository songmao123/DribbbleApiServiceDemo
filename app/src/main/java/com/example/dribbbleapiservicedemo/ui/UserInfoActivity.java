package com.example.dribbbleapiservicedemo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.adapter.QuickShotsAdapter;
import com.example.dribbbleapiservicedemo.databinding.ActivityUserInfoBinding;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.model.User;
import com.example.dribbbleapiservicedemo.retrofit.DribbbleApiServiceFactory;
import com.example.dribbbleapiservicedemo.utils.Constants;
import com.example.dribbbleapiservicedemo.utils.DensityUtil;
import com.example.dribbbleapiservicedemo.utils.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserInfoActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnRecyclerViewItemClickListener, AppBarLayout.OnOffsetChangedListener {

    public static final String USER_INFO = "user_info";
    public static final String USER_ID = "user_id";
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 40;
    private RecyclerView mRecyclerView;

    private ActivityUserInfoBinding mBinding;
    private List<Shot> mShots = new ArrayList<>();
    private CompositeSubscription mCompositeSubscription;
    private QuickShotsAdapter mShotsAdapter;
    private Subscription mSubscription;
    private User mUser;

    private int startPage = 1;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;
    private int mUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);
        mCompositeSubscription = new CompositeSubscription();

        getIntentParams();
        initEvents();
        initMenuAnimation();
        getShotsDatas();
    }

    private void initMenuAnimation() {
        mBinding.fam.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(this, R.anim.show_from_bottom));
        mBinding.fam.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_to_bottom));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > Constants.FAB_SCROLL_OFFSET) {
                    if (dy > 0) {
                        mBinding.fam.hideMenu(true);
                    } else {
                        mBinding.fam.showMenu(true);
                    }
                }
            }
        });
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mUser = intent.getParcelableExtra(USER_INFO);
            mUserId = intent.getIntExtra(USER_ID, -1);
        }
        if (mUser != null) {
            mBinding.setUser(mUser);
            mUserId = mUser.id;
        } else {
            getUserInfos(mUserId);
        }
    }

    private void initEvents() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        mBinding.appBarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = mBinding.appBarLayout.getTotalScrollRange();

        mRecyclerView = mBinding.recyclerView;
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

        mBinding.backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(UserInfoActivity.this);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        Log.i("sqsong", "Percentage: " + percentage);
        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            mBinding.avatarIv.animate().scaleY(0).scaleX(0).setDuration(500).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;
            mBinding.avatarIv.animate().scaleY(1).scaleX(1).setDuration(500).start();
        }
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
        Shot shot = mShots.get(i);
        shot.user = mUser;
        intent.putExtra(ShotDetailActivity.SHOT_DATA, shot);
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    private void getShotsDatas() {
        mSubscription = DribbbleApiServiceFactory.createDribbbleService(null, Constants.DRIBBBLE_ACCESS_TOKEN)
                .getUserShots(mUserId, startPage, Constants.PER_PAGE_COUNT).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Shot>>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        mBinding.loadingContainer.setVisibility(View.GONE);
                        Log.i("sqsong", "Request Complete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("sqsong", "Error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Shot> shots) {
                        if (startPage == 1) {
                            if (shots == null || shots.size() < 1) {
                                mBinding.noShotsTips.setVisibility(View.VISIBLE);
                            }
                            mShots.clear();
                            mShots.addAll(shots);
                            mShotsAdapter.notifyDataSetChanged();
                        } else {
                            if (shots == null || shots.size() < 1) {
                                mShotsAdapter.notifyDataChangedAfterLoadMore(false);
                                View view = getLayoutInflater().inflate(R.layout.layout_no_more_data,
                                        (ViewGroup) mBinding.recyclerView.getParent(), false);
                                mShotsAdapter.addFooterView(view);
                            } else {
                                mShotsAdapter.notifyDataChangedAfterLoadMore(shots, true);
                            }
                        }
                    }
                });
        mCompositeSubscription.add(mSubscription);
    }

    private void getUserInfos(int userId) {
        Subscription subscribe = DribbbleApiServiceFactory.createDribbbleService(null, Constants.DRIBBBLE_ACCESS_TOKEN)
                .getUserInfo(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        if (user != null) {
                            mBinding.setUser(user);
                        }
                    }
                });
        mCompositeSubscription.add(subscribe);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

}
