package com.example.babysitter.services;

import com.example.babysitter.externalModels.boundaries.NewUserBoundary;
import com.example.babysitter.externalModels.boundaries.ObjectBoundary;
import com.example.babysitter.externalModels.boundaries.UserBoundary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("/superapp/users")
    Call<UserBoundary> createUser(@Body NewUserBoundary user);

    @POST("/superapp/objects")
    Call<ObjectBoundary> saveUserData(@Body ObjectBoundary boundaryObject);

    @GET("superapp/users/login/{superapp}/{email}")
    Call<UserBoundary> getUserById(@Path("superapp") String superapp, @Path("email") String email);

    @GET("/superapp/objects/search/byAlias/{type}")
    Call<List<ObjectBoundary>> getAllObjectsByPassword(@Path("type") String type,
                                                       @Query("size") int size,
                                                       @Query("page") int page,
                                                       @Query("superapp") String superapp,
                                                       @Query("email") String email);


    @PUT("/superapp/users/{superapp}/{email}")
    Call<Void> updateUser(@Path("superapp") String superapp,@Path("email") String email, @Body UserBoundary update);

}

