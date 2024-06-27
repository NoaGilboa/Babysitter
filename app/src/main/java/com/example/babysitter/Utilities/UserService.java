package com.example.babysitter.Utilities;

import com.example.babysitter.ExternalModels.MiniAppCommandBoundary;
import com.example.babysitter.ExternalModels.NewUserBoundary;
import com.example.babysitter.ExternalModels.ObjectBoundary;
import com.example.babysitter.ExternalModels.UserBoundary;
import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @POST("/superapp/users")
    Call<UserBoundary> createUser(@Body NewUserBoundary user);

    @POST("/superapp/miniapp/{miniapp}")
    Call<Void> checkUserRole(@Path("miniapp") String miniapp, @Body MiniAppCommandBoundary boundaryCommand);

    @POST("/superapp/miniapp/{miniapp}")
    Call<Void> loginUser(@Path("miniapp") String miniapp, @Body MiniAppCommandBoundary boundaryCommand);

    @POST("/superapp/miniapp/{miniapp}")
    Call<ObjectBoundary> getCurrenUserID(@Path("miniapp") String miniapp, @Body MiniAppCommandBoundary boundaryCommand);

    @POST("/superapp/objects")
    Call<ObjectBoundary> saveUserData(@Body ObjectBoundary boundaryObject);

    @POST("/superapp/miniapp/{miniapp}")
    Call<ObjectBoundary> loadUserData(@Path("miniapp") String miniapp, @Body MiniAppCommandBoundary boundaryCommand);

//    @PUT("superapp/users/{superapp}/{email}")
//    Call<Void> updateUser(@Path("superapp") String superapp, @Path("email") String email, @Body UserBoundary user);
//
    @GET("superapp/users/login/{superapp}/{email}")
    Call<UserBoundary> getUserById(@Path("superapp") String superapp, @Path("email") String email);

    @GET("superapp/objects")
    Call<List<ObjectBoundary>> getAllObjects();

    @GET("/superapp/objects/search/byType/{password}")
        Call<List<ObjectBoundary>> getAllObjectsByPassword(@Path("password") String password);
}
