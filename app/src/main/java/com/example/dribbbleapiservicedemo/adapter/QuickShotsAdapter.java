package com.example.dribbbleapiservicedemo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.utils.Constants;
import com.example.dribbbleapiservicedemo.utils.DensityUtil;

import java.util.List;

/**
 * Created by 青松 on 2016/7/11.
 */
public class QuickShotsAdapter extends BaseQuickAdapter<Shot> {

    private int imageHeight = 0;

    public QuickShotsAdapter(int layoutResId, List<Shot> data) {
        super(layoutResId, data);
        this.imageHeight = (DensityUtil.getScreenWidth() - 3 * (DensityUtil.dip2px(Constants.ITEM_SPACING_DP))) / 2;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Shot shot) {
        ImageView imageView = baseViewHolder.getView(R.id.imageView);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = imageHeight;

        baseViewHolder.setText(R.id.viewCountTv, shot.views_count);
        baseViewHolder.setText(R.id.titleTv, shot.title);
        baseViewHolder.setText(R.id.commentCountTv, shot.comments_count);
        baseViewHolder.setText(R.id.likeCountTv, shot.likes_count);
        Glide.with(mContext).load(shot.images.normal).centerCrop().crossFade().into(imageView);
    }
}
