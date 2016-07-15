package com.example.dribbbleapiservicedemo.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.model.ShotLikeUser;
import com.example.dribbbleapiservicedemo.utils.CommonUtils;
import com.example.dribbbleapiservicedemo.utils.DateUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by sqsong on 16-7-14.
 */
public class QuickShotLikeUserAdapter extends BaseQuickAdapter<ShotLikeUser> {

    public QuickShotLikeUserAdapter(int layoutResId, List<ShotLikeUser> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShotLikeUser shotLikeUser) {
        ImageView avatar_iv = helper.getView(R.id.avatar_iv);
        Glide.with(mContext).load(shotLikeUser.user.avatar_url).bitmapTransform(new CropCircleTransformation(mContext))
                .placeholder(R.drawable.ic_account_circle_grey)
                .error(R.drawable.ic_account_circle_grey).into(avatar_iv);

        helper.setText(R.id.name_tv, shotLikeUser.user.name);
        helper.setText(R.id.create_time_tv, DateUtils.getNormalFormatTime(shotLikeUser.created_at));
        if (!TextUtils.isEmpty(shotLikeUser.user.location)) {
            helper.setText(R.id.location_tv, shotLikeUser.user.location);
            helper.setVisible(R.id.location_tv, true);
        } else {
            helper.setVisible(R.id.location_tv, false);
        }

        if (!TextUtils.isEmpty(shotLikeUser.user.bio)) {
            helper.setText(R.id.bio_tv, CommonUtils.noTrailingwhiteLines(Html.fromHtml(shotLikeUser.user.bio)));
            helper.setVisible(R.id.bio_tv, true);
        } else {
            helper.setVisible(R.id.bio_tv, false);
        }

        helper.setText(R.id.likes_count_tv, shotLikeUser.user.likes_count + "");
        helper.setText(R.id.followers_count_tv, String.format(mContext.getString(R.string.user_follower_count),
                shotLikeUser.user.followers_count));
        helper.setText(R.id.shots_count_tv, String.format(mContext.getString(R.string.user_shots_count),
                        shotLikeUser.user.shots_count));

    }
}
