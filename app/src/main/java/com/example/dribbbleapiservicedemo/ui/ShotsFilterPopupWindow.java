package com.example.dribbbleapiservicedemo.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.utils.DensityUtil;

/**
 * Created by 青松 on 2016/7/22.
 */
public class ShotsFilterPopupWindow extends PopupWindow implements View.OnClickListener {

    private int xOffset = 0;
    private OnFilterItemClickListener listener;

    public interface OnFilterItemClickListener {
        void onFilterItemClicked(int position);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShotsFilterPopupWindow(Context context, OnFilterItemClickListener l) {
        super(context);
        this.listener = l;
        xOffset = DensityUtil.getScreenWidth() - DensityUtil.dip2px(135);

        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_shots_filter_pop, null);
        TextView popular_tv = (TextView) contentView.findViewById(R.id.popular_tv);
        TextView recent_tv = (TextView) contentView.findViewById(R.id.recent_tv);
        TextView comment_tv = (TextView) contentView.findViewById(R.id.comment_tv);
        TextView view_tv = (TextView) contentView.findViewById(R.id.view_tv);
        popular_tv.setOnClickListener(this);
        recent_tv.setOnClickListener(this);
        comment_tv.setOnClickListener(this);
        view_tv.setOnClickListener(this);

        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setElevation(10);
        setBackgroundDrawable(new ColorDrawable());
        setAnimationStyle(R.style.Filter_Pop_Anim);
        setOutsideTouchable(true);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showPop(View anchor) {
        this.showAsDropDown(anchor, xOffset, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popular_tv:
                listener.onFilterItemClicked(0);
                break;
            case R.id.recent_tv:
                listener.onFilterItemClicked(1);
                break;
            case R.id.comment_tv:
                listener.onFilterItemClicked(2);
                break;
            case R.id.view_tv:
                listener.onFilterItemClicked(3);
                break;
        }
    }
}
