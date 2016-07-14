package com.example.dribbbleapiservicedemo.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.model.ShotLikeUser;
import com.example.dribbbleapiservicedemo.utils.CircleTransform;
import com.example.dribbbleapiservicedemo.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        try {
            if (TextUtils.isEmpty(shotLikeUser.user.avatar_url)) {
                helper.setImageResource(R.id.avatar_iv, R.drawable.ic_account_circle_grey);
            } else {
                Picasso.with(mContext).load(shotLikeUser.user.avatar_url)
                        .transform(new CircleTransform()).placeholder(R.drawable.ic_account_circle_grey)
                        .error(R.drawable.ic_account_circle_grey).into(avatar_iv);
            }
        } catch (Exception e) {
            helper.setImageResource(R.id.avatar_iv, R.drawable.ic_account_circle_grey);
            e.printStackTrace();
        }

        helper.setText(R.id.name_tv, shotLikeUser.user.name);
        helper.setText(R.id.create_time_tv, DateUtils.getNormalFormatTime(shotLikeUser.created_at));
        if (!TextUtils.isEmpty(shotLikeUser.user.location)) {
            helper.setText(R.id.location_tv, shotLikeUser.user.location);
            helper.setVisible(R.id.location_tv, true);
        } else {
            helper.setVisible(R.id.location_tv, false);
        }

        if (!TextUtils.isEmpty(shotLikeUser.user.bio)) {
            helper.setText(R.id.bio_tv, shotLikeUser.user.bio);
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
