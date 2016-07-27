package com.example.dribbbleapiservicedemo.adapter;

import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.model.Comment;
import com.example.dribbbleapiservicedemo.model.User;
import com.example.dribbbleapiservicedemo.utils.CommonUtils;
import com.example.dribbbleapiservicedemo.utils.DateUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by 青松 on 2016/7/14.
 */
public class QuickCommentsAdapter extends BaseQuickAdapter<Comment> {

    private OnUserClickListener listener;

    public QuickCommentsAdapter(int layoutResId, List<Comment> data, OnUserClickListener l) {
        super(layoutResId, data);
        this.listener = l;
    }

    public interface OnUserClickListener {
        void onUserClicked(View view, User user);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Comment comment) {
        ImageView avatarIv = helper.getView(R.id.avatar_iv);
        TextView nameTv = helper.getView(R.id.name_tv);
        TextView contentTv = helper.getView(R.id.comment_content_tv);
        Glide.with(mContext).load(comment.user.avatar_url).bitmapTransform(new CropCircleTransformation(mContext))
                .placeholder(R.drawable.ic_account_circle_grey)
                .error(R.drawable.ic_account_circle_grey).into(avatarIv);

        helper.setText(R.id.name_tv, comment.user.name);
        Spanned spanned = Html.fromHtml(comment.body.replace("https://", "sqsong://"));
        helper.setText(R.id.comment_content_tv, CommonUtils.noTrailingwhiteLines(spanned));
        helper.setText(R.id.timeTv, DateUtils.getNormalFormatTime(comment.created_at));
        helper.setText(R.id.likeCountTv, comment.likes_count);

        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onUserClicked(helper.getConvertView(), comment.user);
                }
            }
        };
        avatarIv.setOnClickListener(clickListener);
        nameTv.setOnClickListener(clickListener);
        contentTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
