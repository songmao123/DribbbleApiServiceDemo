package com.example.dribbbleapiservicedemo.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dribbbleapiservicedemo.GlobalApplication;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.adapter.QuickCommentsAdapter;
import com.example.dribbbleapiservicedemo.databinding.ActivityShotDetailBinding;
import com.example.dribbbleapiservicedemo.model.Comment;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.model.User;
import com.example.dribbbleapiservicedemo.retrofit.DribbbleApiServiceFactory;
import com.example.dribbbleapiservicedemo.utils.CommonUtils;
import com.example.dribbbleapiservicedemo.utils.Constants;
import com.example.dribbbleapiservicedemo.view.ReplayCommentDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ShotDetailActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener,
        View.OnClickListener, ReplayCommentDialog.OnCommentReplayListener, QuickCommentsAdapter.OnUserClickListener {

    public static final String SHOT_DATA = "shot_data";
    private ActivityShotDetailBinding mBinding;
    private QuickCommentsAdapter mCommentsAdapter;
    private List<Comment> mComments = new ArrayList<>();
    private ReplayCommentDialog mReplayDialog;
    private Subscription mSubscribe;
    private CompositeSubscription mCompositeSubscription;
    private Shot mShot;
    private String oauthAccessToken;
    private int startPage = 1;
    private boolean isLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shot_detail);

        getIntentParams();
        initEvents();
        initRecyclerView();
        getCommentDatas();
        checkIfILikeShot();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mShot = intent.getParcelableExtra(SHOT_DATA);
        }
    }

    private void initEvents() {
        mCompositeSubscription = new CompositeSubscription();
        oauthAccessToken = mPreferencesHelper.getString(Constants.OAUTH_ACCESS_TOKE);
        mBinding.expandableTv.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.setShot(mShot);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mBinding.avatarIv.setOnClickListener(this);
        mBinding.nameTv.setOnClickListener(this);
        mBinding.likeLl.setOnClickListener(this);
        mBinding.likeCountTv.setOnClickListener(this);
        mBinding.collapseTv.setOnClickListener(this);

        mBinding.collapseTv.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = mBinding.expandableTv.getLineCount();
                mBinding.collapseTv.setVisibility(lineCount >= 3 ? View.VISIBLE : View.GONE);
            }
        });

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReplayDialog == null) {
                    mReplayDialog = new ReplayCommentDialog(ShotDetailActivity.this, ShotDetailActivity.this);
                    mReplayDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mBinding.fab.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.fab.show(true);
                                }
                            }, 500);
                        }
                    });
                }
                mReplayDialog.show();
                mBinding.fab.hide(true);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = mBinding.contentContainer.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mCommentsAdapter = new QuickCommentsAdapter(R.layout.item_comment, mComments, this);
        mCommentsAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mCommentsAdapter.isFirstOnly(true);
        mCommentsAdapter.setOnLoadMoreListener(this);
        mCommentsAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mCommentsAdapter.setLoadingView(getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) recyclerView.getParent(), false));
        recyclerView.setAdapter(mCommentsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > Constants.FAB_SCROLL_OFFSET) {
                    if (dy > 0) {
                        mBinding.fab.hide(true);
                    } else {
                        mBinding.fab.show(true);
                    }
                }
            }
        });

        mBinding.fab.show(false);
        mBinding.fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.fab.show(true);
            }
        }, 2000);
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
        mCompositeSubscription.add(mSubscribe);
    }

    @Override
    public void onReplay(View view, String replayBody) {
        if (TextUtils.isEmpty(oauthAccessToken)) {
            Intent intent = new Intent(this, OAuthWebActivity.class);
            startActivity(intent);
            Toast.makeText(ShotDetailActivity.this, "Please Login First!", Toast.LENGTH_SHORT).show();
            return;
        }
        mReplayDialog.dismiss();
        Subscription subscribe = DribbbleApiServiceFactory.createDribbbleService(null, oauthAccessToken)
                .postComment(mShot.id, replayBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Comment>() {
                    @Override
                    public void call(Comment comment) {
                        if (comment != null) {
                            int commentUserId = comment.user.id;
                            int userId = GlobalApplication.getInstance().getUserInfo().id;
                            if (commentUserId == userId) {
                                Toast.makeText(ShotDetailActivity.this, "Comment Success!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        int code = ((HttpException) throwable).code();
                        if (code == 403) {
                            Toast.makeText(ShotDetailActivity.this, "Sorry, You Have No Authority To Comment This Shot!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mCompositeSubscription.add(subscribe);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatarIv:
            case R.id.nameTv:
                Pair<View, String> imagePair = new Pair<>((View) mBinding.avatarIv, "image");
                Pair<View, String> textPair = new Pair<>((View) mBinding.nameTv, "text");
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imagePair, textPair);
                Intent intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_INFO, mShot.user);
                ActivityCompat.startActivity(this, intent, compat.toBundle());
                break;
            case R.id.likeCountTv:
                if (mShot != null) {
                    Intent likeIntent = new Intent(ShotDetailActivity.this, ShotLikeUserActivity.class);
                    likeIntent.putExtra(ShotLikeUserActivity.SHOT_INFO, mShot);
                    startActivity(likeIntent);
                }
                break;
            case R.id.collapse_tv:
                mBinding.expandableTv.toggle();
                mBinding.collapseTv.setText(mBinding.expandableTv.isExpanded()
                        ? R.string.expand : R.string.collapse);
                break;
            case R.id.like_ll:
                oauthAccessToken = mPreferencesHelper.getString(Constants.OAUTH_ACCESS_TOKE);
                if (TextUtils.isEmpty(oauthAccessToken)) {
                    CommonUtils.startOauthWebActivity(getApplicationContext());
                    Toast.makeText(ShotDetailActivity.this, "Please Login First!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ObjectAnimator scaleX = ObjectAnimator.ofFloat(mBinding.likeIv, "scaleX", 1.3f, 0.8f, 1.0f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(mBinding.likeIv, "scaleY", 1.3f, 0.8f, 1.0f);
                AnimatorSet set = new AnimatorSet ();
                set.playTogether(scaleX, scaleY);
                set.start();
                set.setDuration(500);
                if (isLike) {
                    requestUnLikeShot();
                } else {
                    requestLikeShot();
                }
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) { }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isLike) {
                            mBinding.likeIv.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                        } else {
                            mBinding.likeIv.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                        }
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) { }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                break;
        }
    }

    private void requestLikeShot() {
        Subscription subscribe = DribbbleApiServiceFactory.createDribbbleService(null, oauthAccessToken)
                .likeShot(mShot.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Shot>() {
                    @Override
                    public void call(Shot shot) {
                        if (shot != null && shot.id != 0) {
                            isLike = true;
                            Toast.makeText(ShotDetailActivity.this, "Request Like Success!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        int code = ((HttpException) throwable).code();
                        Toast.makeText(ShotDetailActivity.this, "Status Code: " + code, Toast.LENGTH_SHORT).show();
                        if (code != 201) {
                            mBinding.likeIv.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                        }
                    }
                });
        mCompositeSubscription.add(subscribe);
    }

    private void requestUnLikeShot() {
        DribbbleApiServiceFactory.createDribbbleService(null, oauthAccessToken)
                .unLikeShot(mShot.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Shot>() {
                    @Override
                    public void call(Shot shot) {
                        isLike = false;
                        Toast.makeText(ShotDetailActivity.this, "Request UnLike Success!", Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        int code = ((HttpException) throwable).code();
                        Toast.makeText(ShotDetailActivity.this, "Status Code: " + code, Toast.LENGTH_SHORT).show();
                        mBinding.likeIv.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                    }
                });
    }

    private void checkIfILikeShot() {
        Subscription subscribe = DribbbleApiServiceFactory.createDribbbleService(null, oauthAccessToken)
                .checkIfIIikeShot(mShot.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Shot>() {
                    @Override
                    public void call(Shot shot) {
                        if (shot != null && shot.id != 0) {
                            isLike = true;
                            mBinding.likeIv.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        int code = ((HttpException) throwable).code();
                        if (code == 404) {
                            isLike = false;
                            mBinding.likeIv.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                            Toast.makeText(ShotDetailActivity.this, "Status Code: " + code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mCompositeSubscription.add(subscribe);
    }

    @Override
    public void onUserClicked(View view, User user) {
        Pair<View, String> imagePair = new Pair<>(view.findViewById(R.id.avatar_iv), "image");
        Pair<View, String> textPair = new Pair<>(view.findViewById(R.id.name_tv), "text");
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imagePair, textPair);
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra(UserInfoActivity.USER_INFO, user);
        ActivityCompat.startActivity(this, intent, compat.toBundle());
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
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
