package com.example.dribbbleapiservicedemo.adapter;

import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.model.Comment;
import com.example.dribbbleapiservicedemo.utils.CircleTransform;
import com.example.dribbbleapiservicedemo.utils.CommonUtils;
import com.example.dribbbleapiservicedemo.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by 青松 on 2016/7/14.
 */
public class QuickCommentsAdapter extends BaseQuickAdapter<Comment> {

    public QuickCommentsAdapter(int layoutResId, List<Comment> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Comment comment) {
        ImageView avatarIv = helper.getView(R.id.avatarIv);
        Glide.with(mContext).load(comment.user.avatar_url).bitmapTransform(new CropCircleTransformation(mContext))
                .placeholder(R.drawable.ic_account_circle_grey)
                .error(R.drawable.ic_account_circle_grey).into(avatarIv);

        helper.setText(R.id.nameTv, comment.user.name);
        Spanned spanned = Html.fromHtml(comment.body);
        helper.setText(R.id.commentContentTv, CommonUtils.noTrailingwhiteLines(spanned));
        helper.setText(R.id.timeTv, DateUtils.getNormalFormatTime(comment.created_at));
        helper.setText(R.id.likeCountTv, comment.likes_count);
    }

}
