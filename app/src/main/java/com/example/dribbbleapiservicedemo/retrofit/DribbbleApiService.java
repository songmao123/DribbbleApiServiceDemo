package com.example.dribbbleapiservicedemo.retrofit;

import com.example.dribbbleapiservicedemo.model.Comment;
<<<<<<< HEAD
=======
import com.example.dribbbleapiservicedemo.model.FollowerUser;
>>>>>>> master
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.model.ShotLikeUser;
import com.example.dribbbleapiservicedemo.model.TokenData;
import com.example.dribbbleapiservicedemo.model.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sqsong on 16-7-10.
 */
public interface DribbbleApiService {

    /** 获取shots； 根据sort排序，默认为：popularity*/
    @GET("shots")
    Observable<List<Shot>> getShots(@Query("page") int page, @Query("per_page") int perPage, @Query("sort") String sort);

    /** 获取用户已授权的token*/
    @POST("token")
    Observable<TokenData> getToken(@Query("client_id") String client_id, @Query("client_secret") String client_secret,
                                   @Query("code") String code, @Query("redirect_uri") String redirect_uri);

    /** 获取登陆用户信息*/
    @GET("user")
    Observable<User> getAuthenticatedUser();

    /** 获取单个用户信息*/
    @GET("users/{user_id}")
    Observable<User> getUserInfo(@Path("user_id") int userId);

    /** 获取某个shot的评论信息*/
    @GET("shots/{shot_id}/comments")
    Observable<List<Comment>> getComments(@Path("shot_id") int shotId, @Query("page") int page, @Query("per_page") int perPage);

    /** 评论某个shot*/
    @POST("shots/{shot_id}/comments")
    Observable<Comment> postComment(@Path("shot_id") int shotId, @Body String body);

    /** 获取某个人的所有shot*/
    @GET("users/{user_id}/shots")
    Observable<List<Shot>> getUserShots(@Path("user_id") int shotId, @Query("page") int page, @Query("per_page") int perPage);

    /** 获取某个shot的喜欢的人*/
    @GET("shots/{shot_id}/likes")
    Observable<List<ShotLikeUser>> getShotLikeUsers(@Path("shot_id") int shotId, @Query("page") int page, @Query("per_page") int perPage);

    /** 喜欢某个shot*/
    @GET("shots/{shot_id}/like")
    Observable<Shot> checkIfIIikeShot(@Path("shot_id") int shotId);

    /** 喜欢某个shot*/
    @POST("shots/{shot_id}/like")
    Observable<Shot> likeShot(@Path("shot_id") int shotId);

    /** 取消喜欢某个shot*/
    @DELETE("shots/{shot_id}/like")
    Observable<Shot> unLikeShot(@Path("shot_id") int shotId);

    /** 检测自己是否有关注该用户*/
    @GET("users/{user_id}/following/{target_user_id}")
    Observable<User> checkIfIFollowUser(@Path("user_id") int userId, @Path("target_user_id") int targetUserId);

    /** 关注某个用户*/
    @PUT("users/{user_id}/follow")
    Observable<User> followingUser(@Path("user_id") int userId);

    /** 取消关注某个用户*/
    @DELETE("users/{user_id}/follow")
    Observable<User> unFollowingUser(@Path("user_id") int userId);
<<<<<<< HEAD
=======

    /** 获取某个用户的followers*/
    @GET("users/{user_id}/followers")
    Observable<List<FollowerUser>> getFollowers(@Path("user_id") int shotId, @Query("page") int page, @Query("per_page") int perPage);
>>>>>>> master
}
