package io.xsun.simpletreehole.android.service;

import java.util.List;

import io.xsun.simpletreehole.android.data.Post;
import io.xsun.simpletreehole.android.data.Result;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostService {
    @GET("/posts")
    Call<List<Post>> postList(@Query("offset") int offset, @Query("length") int length);

    @POST("/posts")
    Call<Result> newPost(@Body Post post);

    @GET("/posts/{postId}/comments")
    Call<List<Post>> commentListOfPost(@Path("postId") int postId, @Query("offset") int offset, @Query("length") int length);

    @POST("/posts/{postId}/comments")
    Call<Result> newComment(@Path("postId") int postId, @Body Post post);
}
