package com.example.dribbbleapiservicedemo.model;

import android.os.Parcel;
import android.os.Parcelable;


public class User implements Parcelable {

    public int id;
    public int likes_count;
    public int followers_count;
    public int shots_count;
    public String name;
    public String username;
    public String html_url;
    public String avatar_url;
    public String bio;
    public String shots_url;
    public String location;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.shots_count);
        dest.writeInt(this.likes_count);
        dest.writeInt(this.followers_count);
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.html_url);
        dest.writeString(this.avatar_url);
        dest.writeString(this.bio == null ? "" : this.bio);
        dest.writeString(this.shots_url);
        dest.writeString(this.location);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readInt();
        this.shots_count = in.readInt();
        this.likes_count = in.readInt();
        this.followers_count = in.readInt();
        this.name = in.readString();
        this.username = in.readString();
        this.html_url = in.readString();
        this.avatar_url = in.readString();
        this.bio = in.readString();
        this.shots_url = in.readString();
        this.location = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
