package com.example.dribbbleapiservicedemo.adapter;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.model.FollowerUser;
import com.example.dribbbleapiservicedemo.model.ShotLikeUser;
import com.example.dribbbleapiservicedemo.ui.UserInfoActivity;
import com.example.dribbbleapiservicedemo.utils.CommonUtils;
import com.example.dribbbleapiservicedemo.utils.DateUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by sqsong on 16-7-14.
 */
public class QuickFollowerAdapter extends BaseQuickAdapter<FollowerUser> {

    public QuickFollowerAdapter(int layoutResId, List<FollowerUser> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final FollowerUser shotLikeUser) {
        ImageView avatar_iv = helper.getView(R.id.avatar_iv);
        TextView bio_tv = helper.getView(R.id.bio_tv);
        Glide.with(mContext).load(shotLikeUser.follower.avatar_url).bitmapTransform(new CropCircleTransformation(mContext))
                .placeholder(R.drawable.ic_account_circle_grey)
                .error(R.drawable.ic_account_circle_grey).into(avatar_iv);

        helper.setText(R.id.name_tv, shotLikeUser.follower.name);
        helper.setText(R.id.create_time_tv, DateUtils.getNormalFormatTime(shotLikeUser.created_at));
        if (!TextUtils.isEmpty(shotLikeUser.follower.location)) {
            helper.setText(R.id.location_tv, shotLikeUser.follower.location);
            helper.setVisible(R.id.location_tv, true);
        } else {
            helper.setVisible(R.id.location_tv, false);
        }

        if (!TextUtils.isEmpty(shotLikeUser.follower.bio)) {
            helper.setText(R.id.bio_tv, CommonUtils.noTrailingwhiteLines(Html.fromHtml(shotLikeUser.follower.bio)));
            helper.setVisible(R.id.bio_tv, true);
            bio_tv.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            helper.setVisible(R.id.bio_tv, false);
        }

        helper.setText(R.id.followers_count_tv, String.format(mContext.getString(R.string.user_follower_count),
                shotLikeUser.follower.followers_count));
        helper.setText(R.id.shots_count_tv, String.format(mContext.getString(R.string.user_shots_count),
                        shotLikeUser.follower.shots_count));

//        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, UserInfoActivity.class);
//                intent.putExtra(UserInfoActivity.USER_INFO, shotLikeUser.follower);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//            }
//        });

    }
}
