package com.example.dribbbleapiservicedemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dribbbleapiservicedemo.model.Image;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 青松 on 2016/7/18.
 */
public class ShotsDBManaber {

    private static ShotsDBManaber instance;
    private ShotsDBHelper mDBHelper;

    private ShotsDBManaber(Context context) {
        mDBHelper = new ShotsDBHelper(context);
    }

    public static ShotsDBManaber getInstance(Context context) {
        if (instance == null) {
            synchronized (ShotsDBManaber.class) {
                if (instance == null) {
                    instance = new ShotsDBManaber(context);
                }
            }
        }
        return instance;
    }

    public synchronized List<Shot> queryAllShots() {
        long queryStartTime = System.currentTimeMillis();
        List<Shot> shotList = new ArrayList<>();
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        try {
            Cursor cursor = database.query(DBConstants.TABLE_NAME_SHOTS, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                Shot shot = new Shot();
                shot.id = cursor.getInt(cursor.getColumnIndex(DBConstants.SHOT_ID));
                shot.title = cursor.getString(cursor.getColumnIndex(DBConstants.SHOT_TITLE));
                shot.description = cursor.getString(cursor.getColumnIndex(DBConstants.SHOT_DESCRIPTION));
                shot.views_count = cursor.getInt(cursor.getColumnIndex(DBConstants.SHOT_VIEWS_COUNT));
                shot.likes_count = cursor.getInt(cursor.getColumnIndex(DBConstants.SHOT_LIKES_COUNT));
                shot.comments_count = cursor.getInt(cursor.getColumnIndex(DBConstants.SHOT_COMMENTS_COUNT));
                shot.created_at = cursor.getString(cursor.getColumnIndex(DBConstants.SHOT_CREATE_AT));
                shot.updated_at = cursor.getString(cursor.getColumnIndex(DBConstants.SHOT_UPDATE_AT));
                shot.images = queryImage(database, cursor.getInt(cursor.getColumnIndex(DBConstants.SHOT_FK_IMAGE)));
                shot.user = queryUser(database, cursor.getInt(cursor.getColumnIndex(DBConstants.SHOT_FK_USER)));
                shotList.add(shot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        long queryEndTime = System.currentTimeMillis();
        Log.e("sqsong", "Query Time: " + (queryEndTime - queryStartTime) + "ms");
        return shotList;
    }

    public User queryUser(SQLiteDatabase database, int userId) {
        Cursor cursor = database.query(DBConstants.TABLE_NAME_USERS, null, "_id=?", new String[]{userId + ""}, null, null, null);
        User user = new User();
        while (cursor.moveToNext()) {
            user.id = cursor.getInt(cursor.getColumnIndex(DBConstants.USER_ID));
            user.likes_count = cursor.getInt(cursor.getColumnIndex(DBConstants.USER_LIKES_COUNT));
            user.followers_count = cursor.getInt(cursor.getColumnIndex(DBConstants.USER_FOLLOWERS_COUNT));
            user.shots_count = cursor.getInt(cursor.getColumnIndex(DBConstants.USER_SHOTS_COUNT));
            user.name = cursor.getString(cursor.getColumnIndex(DBConstants.USER_NAME));
            user.username = cursor.getString(cursor.getColumnIndex(DBConstants.USER_USERNAME));
            user.html_url = cursor.getString(cursor.getColumnIndex(DBConstants.USER_HTML_URL));
            user.avatar_url = cursor.getString(cursor.getColumnIndex(DBConstants.USER_AVATAR_URL));
            user.bio = cursor.getString(cursor.getColumnIndex(DBConstants.USER_BIO));
            user.shots_url = cursor.getString(cursor.getColumnIndex(DBConstants.USER_SHOTS_URL));
            user.location = cursor.getString(cursor.getColumnIndex(DBConstants.USER_LOCATION));
            break;
        }
        cursor.close();
        return user;
    }

    public Image queryImage(SQLiteDatabase database, int imageId) {
        Cursor cursor = database.query(DBConstants.TABLE_NAME_IMAGES, null, "_id=?", new String[]{imageId + ""}, null, null, null);
        Image image = new Image();
        while (cursor.moveToNext()) {
            image.hidpi = cursor.getString(cursor.getColumnIndex(DBConstants.IMAGE_HIDPI));
            image.normal = cursor.getString(cursor.getColumnIndex(DBConstants.IMAGE_NORMAL));
            image.teaser = cursor.getString(cursor.getColumnIndex(DBConstants.IMAGE_TEASER));
            break;
        }
        cursor.close();
        return image;
    }

    public synchronized void saveShotLists(List<Shot> shotLists) {
        long saveStartTime = System.currentTimeMillis();
        for (Shot shot : shotLists) {
            saveShot(shot);
        }
        long saveEndTime = System.currentTimeMillis();
        Log.e("sqsong", "Save Data Time: " + (saveEndTime - saveStartTime) + "ms");
    }

    public synchronized void saveShot(Shot shot) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBConstants.SHOT_ID, shot.id);
            values.put(DBConstants.SHOT_TITLE, shot.title);
            values.put(DBConstants.SHOT_DESCRIPTION, shot.description);
            values.put(DBConstants.SHOT_FK_IMAGE, saveImage(database, shot.images));
            values.put(DBConstants.SHOT_VIEWS_COUNT, shot.views_count);
            values.put(DBConstants.SHOT_LIKES_COUNT, shot.likes_count);
            values.put(DBConstants.SHOT_COMMENTS_COUNT, shot.comments_count);
            values.put(DBConstants.SHOT_CREATE_AT, shot.created_at);
            values.put(DBConstants.SHOT_UPDATE_AT, shot.updated_at);
            values.put(DBConstants.SHOT_FK_USER, saveUser(database, shot.user));
            database.insert(DBConstants.TABLE_NAME_SHOTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public synchronized long saveImage(SQLiteDatabase database, Image image) {
        ContentValues values = new ContentValues();
        values.put(DBConstants.IMAGE_HIDPI, image.hidpi);
        values.put(DBConstants.IMAGE_NORMAL, image.normal);
        values.put(DBConstants.IMAGE_TEASER, image.teaser);
        return database.insert(DBConstants.TABLE_NAME_IMAGES, null, values);
    }

    public synchronized long saveUser(SQLiteDatabase database, User user) {
        ContentValues values = new ContentValues();
        values.put(DBConstants.USER_ID, user.id);
        values.put(DBConstants.USER_LIKES_COUNT, user.likes_count);
        values.put(DBConstants.USER_FOLLOWERS_COUNT, user.followers_count);
        values.put(DBConstants.USER_SHOTS_COUNT, user.shots_count);
        values.put(DBConstants.USER_NAME, user.name);
        values.put(DBConstants.USER_USERNAME, user.username);
        values.put(DBConstants.USER_HTML_URL, user.html_url);
        values.put(DBConstants.USER_AVATAR_URL, user.avatar_url);
        values.put(DBConstants.USER_BIO, user.bio);
        values.put(DBConstants.USER_SHOTS_URL, user.shots_url);
        values.put(DBConstants.USER_LOCATION, user.location);
        return database.insert(DBConstants.TABLE_NAME_USERS, null, values);
    }

    public synchronized void deleteOldDatas() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.delete(DBConstants.TABLE_NAME_SHOTS, null, null);
        database.close();
    }
}
