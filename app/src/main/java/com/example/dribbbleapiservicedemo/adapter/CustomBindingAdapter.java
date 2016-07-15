package com.example.dribbbleapiservicedemo.adapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dribbbleapiservicedemo.R;
import com.example.dribbbleapiservicedemo.utils.CircleTransform;
import com.squareup.picasso.Picasso;

/**
 * Created by sqsong on 16-7-10.
 */
public class CustomBindingAdapter {

    @BindingAdapter({"bind:imageUrl", "bind:type"})
    public static void loadImage(ImageView imageView, String url, int type) {
        if (type == 1) {
            try {
                Picasso.with(imageView.getContext()).load(url)
                        .transform(new CircleTransform()).placeholder(R.drawable.ic_account_circle_white)
                        .placeholder(R.drawable.ic_account_circle_white)
                        .error(R.drawable.ic_account_circle_white).into(imageView);
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.ic_account_circle_white);
            }
//            Glide.with(imageView.getContext()).load(url).
//                    bitmapTransform(new CropCircleTransformation(imageView.getContext()))
//                    .placeholder(R.drawable.ic_account_circle_white).into(imageView);
        } else {
            Glide.with(imageView.getContext()).load(url).placeholder(R.drawable.test).centerCrop().into(imageView);
        }

    }
}
