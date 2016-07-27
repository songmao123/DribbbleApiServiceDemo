package com.example.dribbbleapiservicedemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {

    public int id;
    public String body;
    public String created_at;
    public String updated_at;
    public String likes_count;
    public User user;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.body);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeString(this.likes_count);
        dest.writeParcelable(this.user, flags);
    }

    public Comment() {
    }

    protected Comment(Parcel in) {
        this.id = in.readInt();
        this.body = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.likes_count = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
