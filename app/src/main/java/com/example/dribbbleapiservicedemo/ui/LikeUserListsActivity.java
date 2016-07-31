package com.example.dribbbleapiservicedemo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.adapter.QuickFollowerAdapter;
import com.example.dribbbleapiservicedemo.databinding.ActivityLikeUserListsBinding;
import com.example.dribbbleapiservicedemo.model.FollowerUser;
import com.example.dribbbleapiservicedemo.model.User;
import com.example.dribbbleapiservicedemo.retrofit.DribbbleApiServiceFactory;
import com.example.dribbbleapiservicedemo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LikeUserListsActivity extends AppCompatActivity implements BaseQuickAdapter.RequestLoadMoreListener,
        View.OnClickListener, BaseQuickAdapter.OnRecyclerViewItemClickListener {

    private ActivityLikeUserListsBinding mBinding;
    private QuickFollowerAdapter mQuickAdapter;
    private List<FollowerUser> mFollowerUsers = new ArrayList<>();
    private CompositeSubscription mCompositeSubscription;
    private User mUser;
    private int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_like_user_lists);

        getIntentParams();
        initEvents();
        initRecyclerView();
        getFollowers();
    }


    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mUser = intent.getParcelableExtra("user_info");
        }
    }

    private void initEvents() {
        mBinding.setUser(mUser);

        mCompositeSubscription = new CompositeSubscription();
        mBinding.appBarLayout.setBackgroundResource(android.R.color.transparent);
        mBinding.titleBar.backIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                ActivityCompat.finishAfterTransition(this);
                break;
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = mBinding.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mQuickAdapter = new QuickFollowerAdapter(R.layout.item_shot_like_user, mFollowerUsers);
        mQuickAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mQuickAdapter.isFirstOnly(true);
        mQuickAdapter.setOnLoadMoreListener(this);
        mQuickAdapter.setOnRecyclerViewItemClickListener(this);
        mQuickAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mQuickAdapter.setLoadingView(getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) recyclerView.getParent(), false));
        recyclerView.setAdapter(mQuickAdapter);
    }

    @Override
    public void onItemClick(View view, int i) {
        Pair<View, String> imagePair = new Pair<>(view.findViewById(R.id.avatar_iv), "image");
        Pair<View, String> textPair = new Pair<>(view.findViewById(R.id.name_tv), "text");
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imagePair, textPair);
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra(UserInfoActivity.USER_INFO, mFollowerUsers.get(i).follower);
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    @Override
    public void onLoadMoreRequested() {
        pageIndex++;
        getFollowers();
    }

    private void getFollowers() {
        if (mUser == null) {
            return;
        }
        Subscription subscribe = DribbbleApiServiceFactory.createDribbbleService(null, Constants.DRIBBBLE_ACCESS_TOKEN)
            .getFollowers(mUser.id, pageIndex, Constants.PER_PAGE_COUNT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<FollowerUser>>() {
                @Override
                public void call(List<FollowerUser> followerUsers) {
                    mBinding.loadingContainer.setVisibility(View.GONE);
                    if (pageIndex == 1) {
                        if (followerUsers == null || followerUsers.size() < 1) {
                            mBinding.noFollowersTv.setVisibility(View.VISIBLE);
                            mBinding.recyclerView.setVisibility(View.GONE);
                        }
                        mFollowerUsers.clear();
                        mFollowerUsers.addAll(followerUsers);
                        mQuickAdapter.notifyDataSetChanged();
                    } else {
                        if (followerUsers == null || followerUsers.size() < 1) {
                            mQuickAdapter.notifyDataChangedAfterLoadMore(false);
                            View view = getLayoutInflater().inflate(R.layout.layout_no_more_data,
                                    (ViewGroup) mBinding.recyclerView.getParent(), false);
                            mQuickAdapter.addFooterView(view);
                        } else {
                            mQuickAdapter.notifyDataChangedAfterLoadMore(followerUsers, true);
                        }
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
