package com.example.dribbbleapiservicedemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 青松 on 2016/7/18.
 */
public class ShotsDBHelper extends SQLiteOpenHelper {

    public ShotsDBHelper(Context context) {
        super(context, DBConstants.SHOTS_DB_NAME, null, DBConstants.SHOTS_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstants.SQL_CREATE_SHOT_IMAGE);
        db.execSQL(DBConstants.SQL_CREATE_SHOT_USER);
        db.execSQL(DBConstants.SQL_CREATE_SHOT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_NAME_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_NAME_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_NAME_SHOTS);
    }
}
