package com.example.dribbbleapiservicedemo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.adapter.QuickCommentsAdapter;
import com.example.dribbbleapiservicedemo.databinding.ActivityShotDetailBinding;
import com.example.dribbbleapiservicedemo.model.Comment;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.retrofit.DribbbleApiServiceFactory;
import com.example.dribbbleapiservicedemo.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ShotDetailActivity extends AppCompatActivity implements BaseQuickAdapter.RequestLoadMoreListener, View.OnClickListener {

    private ActivityShotDetailBinding mBinding;
    public static final String SHOT_DATA = "shot_data";
    private List<Comment> mComments = new ArrayList<>();
    private Shot mShot;
    private int startPage = 1;
    private QuickCommentsAdapter mCommentsAdapter;
    private Subscription mSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shot_detail);

        getIntentParams();
        initEvents();
        initRecyclerView();
        getCommentDatas();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mShot = intent.getParcelableExtra(SHOT_DATA);
        }
    }

    private void initEvents() {
        mBinding.setShot(mShot);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.avatarIv.setOnClickListener(this);
        mBinding.nameTv.setOnClickListener(this);

        mBinding.likeCountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShot != null) {
                    Intent intent = new Intent(ShotDetailActivity.this, ShotLikeUserActivity.class);
                    intent.putExtra(ShotLikeUserActivity.SHOT_INFO, mShot);
                    startActivity(intent);
                }
            }
        });

        mBinding.collapseTv.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = mBinding.expandableTv.getLineCount();
                mBinding.collapseTv.setVisibility(lineCount >= 3 ? View.VISIBLE : View.GONE);
            }
        });

        mBinding.collapseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.expandableTv.toggle();
                mBinding.collapseTv.setText(mBinding.expandableTv.isExpanded()
                        ? R.string.expand : R.string.collapse);
            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = mBinding.contentContainer.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mCommentsAdapter = new QuickCommentsAdapter(R.layout.item_comment, mComments);
        mCommentsAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mCommentsAdapter.isFirstOnly(true);
        mCommentsAdapter.setOnLoadMoreListener(this);
        mCommentsAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mCommentsAdapter.setLoadingView(getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) recyclerView.getParent(), false));
        recyclerView.setAdapter(mCommentsAdapter);
    }

    @Override
    public void onLoadMoreRequested() {
        startPage++;
        getCommentDatas();
    }

    private void getCommentDatas() {
        mSubscribe = DribbbleApiServiceFactory.createDribbbleService(null, Constants.DRIBBBLE_ACCESS_TOKEN)
            .getComments(mShot.id, startPage, Constants.PER_PAGE_COUNT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Comment>>() {
                @Override
                public void call(List<Comment> comments) {
                    mBinding.contentContainer.loadingContainer.setVisibility(View.GONE);
                    if (startPage == 1) {
                        mComments.clear();
                        mComments.addAll(comments);
                        mCommentsAdapter.notifyDataSetChanged();
                    } else {
                        if (comments == null || comments.size() < 1) {
                            mCommentsAdapter.notifyDataChangedAfterLoadMore(false);
                            View view = getLayoutInflater().inflate(R.layout.layout_no_more_data,
                                    (ViewGroup) mBinding.contentContainer.recyclerView.getParent(), false);
                            mCommentsAdapter.addFooterView(view);
                        } else {
                            mCommentsAdapter.notifyDataChangedAfterLoadMore(comments, true);
                        }
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    if (throwable instanceof TimeoutException) {
                        Toast.makeText(ShotDetailActivity.this, "collect server time out ...", Toast.LENGTH_SHORT).show();
                        getCommentDatas();
                    }
                }
            });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatarIv:
            case R.id.nameTv:
                Intent intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_INFO, mShot.user);
                startActivity(intent);
                break;
        }
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

}
