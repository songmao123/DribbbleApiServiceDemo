package com.example.dribbbleapiservicedemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Shot implements Parcelable {

    public int id;
    public String title;
    public String description;
    public int width;
    public int height;
    public Image images;
    public int views_count;
    public int likes_count;
    public int comments_count;
    public String created_at;
    public String updated_at;
    public User user;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description == null ? "" : this.description);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeParcelable(this.images, flags);
        dest.writeInt(this.views_count);
        dest.writeInt(this.likes_count);
        dest.writeInt(this.comments_count);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeParcelable(this.user, flags);
    }

    public Shot() {
    }

    protected Shot(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.images = in.readParcelable(Image.class.getClassLoader());
        this.views_count = in.readInt();
        this.likes_count = in.readInt();
        this.comments_count = in.readInt();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Shot> CREATOR = new Creator<Shot>() {
        @Override
        public Shot createFromParcel(Parcel source) {
            return new Shot(source);
        }

        @Override
        public Shot[] newArray(int size) {
            return new Shot[size];
        }
    };
}
