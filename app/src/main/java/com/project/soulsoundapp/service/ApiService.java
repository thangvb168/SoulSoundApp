package com.project.soulsoundapp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.project.soulsoundapp.model.Category;
import com.project.soulsoundapp.model.Comment;
import com.project.soulsoundapp.model.Playlist;
import com.project.soulsoundapp.model.Song;
import com.project.soulsoundapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    class ApiResponse<T> {
        private final String message;
        private final int status;
        @SerializedName("metadata")
        private final T data;

        public ApiResponse(String message, int status, T data) {
            this.message = message;
            this.status = status;
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }

        public T getData() {
            return data;
        }
    }
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
//    http://146.190.90.159:8000/api/v2/songs
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://146.190.90.159:8000/api/v2/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

//    Routes
    @GET("users")
    Call<ApiResponse<List<User>>> getAllUsers();
    @FormUrlEncoded
    @POST("users/login")
    Call<ApiResponse<User>> loginApi(@Field("email") String email, @Field("password") String password);
//    Call<ApiResponse<User>> loginApi(@Query("email") String email, @Query("password") String password);
    @FormUrlEncoded
    @POST("users/register")
    Call<ApiResponse<User>> registerApi(@Field("email") String email, @Field("name") String fullName, @Field("password") String password);
    @FormUrlEncoded
    @POST("users/fwps")
    Call<ApiResponse<User>> forgotPassword(@Field("email") String email, @Field("code") String code);
    @FormUrlEncoded
    @POST("users/updatepw")
    Call<Void> updatePassword(@Field("email") String email, @Field("password") String password);
    @FormUrlEncoded
    @POST("users/update")
    Call<Void> handleFavourite(@Field("email") String email, @Field("songId") String songId, @Field("action") String action);

    @FormUrlEncoded
    @POST("users/checkemail")
    Call<ApiResponse<Boolean>> checkEmail(@Field("email") String email);
    @GET("songs")
    Call<ApiResponse<List<Song>>> getAllSongs();
    @GET("songs/hitsong")
    Call<ApiResponse<List<String>>> getHitSong();
    @GET("songs/{id}/comments")
    Call<ApiResponse<List<Comment>>> getCommentsBySongId(@Path("id") String id);

    @POST("songs/{id}/comments")
    Call<ApiResponse<List<Comment>>> sendCommentByIdSong(@Path("id") String id, @Body Comment comment);

    @GET("playlists")
    Call<ApiResponse<List<Playlist>>> getAllPlaylists();

    @GET("playlists/discover")
    Call<ApiResponse<List<String>>> getPlaylistDiscover();

    @GET("playlists/top100")
    Call<ApiResponse<List<String>>> getPlaylistTop100();

    @GET("categories")
    Call<ApiResponse<List<Category>>> getAllCategories();

}
