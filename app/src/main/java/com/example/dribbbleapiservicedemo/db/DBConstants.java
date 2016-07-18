package com.example.dribbbleapiservicedemo.db;

/**
 * Created by 青松 on 2016/7/18.
 */
public class DBConstants {

    public static final String SHOTS_DB_NAME = "shot.db";
    public static final String TABLE_NAME_SHOTS = "shot_info";
    public static final String TABLE_NAME_IMAGES = "shot_image";
    public static final String TABLE_NAME_USERS = "shot_user";
    public static final int SHOTS_DB_VERSION = 1;

    public static final String SHOT_ID = "id";
    public static final String SHOT_TITLE = "title";
    public static final String SHOT_DESCRIPTION = "description";
    public static final String SHOT_FK_IMAGE = "image_id";
    public static final String SHOT_VIEWS_COUNT = "views_count";
    public static final String SHOT_LIKES_COUNT = "likes_count";
    public static final String SHOT_COMMENTS_COUNT = "comments_count";
    public static final String SHOT_CREATE_AT = "created_at";
    public static final String SHOT_UPDATE_AT = "updated_at";
    public static final String SHOT_FK_USER = "user_id";

    public static final String IMAGE_ID = "id";
    public static final String IMAGE_HIDPI = "hidpi";
    public static final String IMAGE_NORMAL = "normal";
    public static final String IMAGE_TEASER = "teaser";

    public static final String USER_ID = "id";
    public static final String USER_LIKES_COUNT = "likes_count";
    public static final String USER_FOLLOWERS_COUNT = "followers_count";
    public static final String USER_SHOTS_COUNT = "shots_count";
    public static final String USER_NAME = "name";
    public static final String USER_USERNAME = "username";
    public static final String USER_HTML_URL = "html_url";
    public static final String USER_AVATAR_URL = "avatar_url";
    public static final String USER_BIO = "bio";
    public static final String USER_SHOTS_URL = "shots_url";
    public static final String USER_LOCATION = "location";

    public static final String SQL_CREATE_SHOT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SHOTS
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SHOT_ID + " INTEGER,"
            + SHOT_TITLE + " TEXT,"
            + SHOT_DESCRIPTION + " TEXT,"
            + SHOT_FK_IMAGE + " LONG,"
            /*+ "FOREIGN KEY(" + SHOT_FK_IMAGE + ") REFERENCES " + TABLE_NAME_IMAGES + "(" + IMAGE_ID + "),"*/
            + SHOT_VIEWS_COUNT + " INTEGER,"
            + SHOT_LIKES_COUNT + " INTEGER,"
            + SHOT_COMMENTS_COUNT + " INTEGER,"
            + SHOT_CREATE_AT + " TEXT,"
            + SHOT_UPDATE_AT + " TEXT,"
            + SHOT_FK_USER + " LONG"
           /* + "FOREIGN KEY(" + SHOT_FK_USER + ") REFERENCES " + TABLE_NAME_USERS + "(" + USER_ID + ")"*/
            + ")";

    public static final String SQL_CREATE_SHOT_IMAGE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_IMAGES
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + IMAGE_HIDPI + " TEXT,"
            + IMAGE_NORMAL + " TEXT,"
            + IMAGE_TEASER + " TEXT"
            +")";

    public static final String SQL_CREATE_SHOT_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USERS
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USER_ID + " INTEGER,"
            + USER_LIKES_COUNT + " INTEGER,"
            + USER_FOLLOWERS_COUNT + " INTEGER,"
            + USER_SHOTS_COUNT + " INTEGER,"
            + USER_NAME + " TEXT,"
            + USER_USERNAME + " TEXT,"
            + USER_HTML_URL + " TEXT,"
            + USER_AVATAR_URL + " TEXT,"
            + USER_BIO + " TEXT,"
            + USER_SHOTS_URL + " TEXT,"
            + USER_LOCATION + " TEXT"
            +")";

}
