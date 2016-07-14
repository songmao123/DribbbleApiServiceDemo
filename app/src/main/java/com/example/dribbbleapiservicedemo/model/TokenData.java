package com.example.dribbbleapiservicedemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2016/7/13.
 */
public class TokenData implements Parcelable {

    public String access_token;
    public String token_type;
    public String scope;

    protected TokenData(Parcel in) {
        this.access_token = in.readString();
        this.token_type = in.readString();
        this.scope = in.readString();
    }

    public static final Creator<TokenData> CREATOR = new Creator<TokenData>() {
        @Override
        public TokenData createFromParcel(Parcel in) {
            return new TokenData(in);
        }

        @Override
        public TokenData[] newArray(int size) {
            return new TokenData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.access_token);
        dest.writeString(this.token_type);
        dest.writeString(this.scope);
    }
}
