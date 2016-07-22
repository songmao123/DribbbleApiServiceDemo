package com.example.dribbbleapiservicedemo.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.adapter.QuickShotsAdapter;
import com.example.dribbbleapiservicedemo.databinding.FragmentShotsBinding;
import com.example.dribbbleapiservicedemo.db.ShotsDBManaber;
import com.example.dribbbleapiservicedemo.event.FilterShotsEvent;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.retrofit.DribbbleApiServiceFactory;
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

public class ShotsFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnRecyclerViewItemClickListener {

    private FragmentShotsBinding mBinding;
    private SwipeRefreshLayout mSwipLayout;
    private QuickShotsAdapter mShotsAdapter;
    private List<Shot> mShots = new ArrayList<>();
    private String sort = Constants.SORT_POPULAR;
    private Subscription mSubscription;

    private int startPage = 1;

    public ShotsFragment() {}

    public static ShotsFragment newInstance() {
        ShotsFragment fragment = new ShotsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shots, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initEvents();
        initRecyclerView();
        loadDataFromDB();
        getShotsDatas();
    }

    private void initEvents() {
        mBinding.mainContent.progressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(getActivity()).build());
    }

    private void initRecyclerView() {
        mSwipLayout = mBinding.mainContent.swipLayout;
        mSwipLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        RecyclerView mRecyclerView = mBinding.mainContent.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        GridItemDecoration decoration = new GridItemDecoration(2, DensityUtil.dip2px(Constants.ITEM_SPACING_DP), true);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(layoutManager);

        mShotsAdapter = new QuickShotsAdapter(R.layout.item_shots, mShots);
        mShotsAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mShotsAdapter.isFirstOnly(true);
        mShotsAdapter.setOnLoadMoreListener(this);
        mShotsAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mShotsAdapter.setOnRecyclerViewItemClickListener(this);
        mShotsAdapter.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.layout_loading_progress,
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

    private void loadDataFromDB() {
        getShotsFromDBObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Shot>>() {
                    @Override
                    public void onStart() {
                        mBinding.mainContent.loadingContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                        mBinding.mainContent.loadingContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) { }

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
                List<Shot> shots = ShotsDBManaber.getInstance(getContext()).queryAllShots();
                Log.e("sqsong", "Query Shots Size: " + shots.size());
                return shots;
            }
        });
    }

    private void saveShotsToDB(final List<Shot> shots) {
        ShotsDBManaber.getInstance(getContext()).deleteOldDatas();
        ShotsDBManaber.getInstance(getContext()).saveShotLists(shots);
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
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imagePair, textPair);
        Intent intent = new Intent(getActivity(), ShotDetailActivity.class);
        intent.putExtra(ShotDetailActivity.SHOT_DATA, mShots.get(i));
        ActivityCompat.startActivity(getActivity(), intent, compat.toBundle());
    }

    private void getShotsDatas() {
        mSubscription = getShotsFromNetObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<List<Shot>>() {
                @Override
                public void onCompleted() {
                    mBinding.mainContent.loadingContainer.setVisibility(View.GONE);
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

    @Subscribe
    public void onFilterEvent(FilterShotsEvent event) {
        int position = event.position;
        getSortShotsData(position);
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
        mBinding.mainContent.loadingContainer.setVisibility(View.VISIBLE);
        getShotsDatas();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }
}
