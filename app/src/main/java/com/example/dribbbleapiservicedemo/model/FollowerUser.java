package com.example.dribbbleapiservicedemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2016/7/14.
 */
public class FollowerUser implements Parcelable {

    public long id;
    public String created_at;
    public User follower;

    protected FollowerUser(Parcel in) {
        this.id = in.readLong();
        this.created_at = in.readString();
        this.follower = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.created_at);
        dest.writeParcelable(this.follower, flags);
    }

    public static final Creator<FollowerUser> CREATOR = new Creator<FollowerUser>() {
        @Override
        public FollowerUser createFromParcel(Parcel in) {
            return new FollowerUser(in);
        }

        @Override
        public FollowerUser[] newArray(int size) {
            return new FollowerUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
