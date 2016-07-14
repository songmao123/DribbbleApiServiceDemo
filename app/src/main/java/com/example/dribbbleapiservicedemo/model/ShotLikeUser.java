package com.example.dribbbleapiservicedemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2016/7/14.
 */
public class ShotLikeUser implements Parcelable {

    private long id;
    private String created_at;
    private User user;

    protected ShotLikeUser(Parcel in) {
        this.id = in.readLong();
        this.created_at = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.created_at);
        dest.writeParcelable(this.user, flags);
    }

    public static final Creator<ShotLikeUser> CREATOR = new Creator<ShotLikeUser>() {
        @Override
        public ShotLikeUser createFromParcel(Parcel in) {
            return new ShotLikeUser(in);
        }

        @Override
        public ShotLikeUser[] newArray(int size) {
            return new ShotLikeUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
