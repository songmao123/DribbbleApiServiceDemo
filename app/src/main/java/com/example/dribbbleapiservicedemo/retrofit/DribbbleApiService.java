package com.example.dribbbleapiservicedemo.retrofit;

import com.example.dribbbleapiservicedemo.model.Comment;
import com.example.dribbbleapiservicedemo.model.Shot;
import com.example.dribbbleapiservicedemo.model.ShotLikeUser;
import com.example.dribbbleapiservicedemo.model.TokenData;
import com.example.dribbbleapiservicedemo.model.User;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sqsong on 16-7-10.
 */
public interface DribbbleApiService {

    @GET("shots")
    Observable<List<Shot>> getShots(@Query("page") int page, @Query("per_page") int perPage, @Query("sort") String sort);

    @POST("token")
    Observable<TokenData> getToken(@Query("client_id") String client_id, @Query("client_secret") String client_secret,
                                   @Query("code") String code, @Query("redirect_uri") String redirect_uri);
    @GET("user")
    Observable<User> getAuthenticatedUser();

    @GET("shots/{shot_id}/comments")
    Observable<List<Comment>> getComments(@Path("shot_id") int shotId, @Query("page") int page, @Query("per_page") int perPage);

    @GET("shots/{shot_id}/likes")
    Observable<List<ShotLikeUser>> getShotLikeUsers(@Path("shot_id") int shotId, @Query("page") int page, @Query("per_page") int perPage);

    @GET("users/{user_id}/shots")
    Observable<List<Shot>> getUserShots(@Path("user_id") int shotId, @Query("page") int page, @Query("per_page") int perPage);
}
