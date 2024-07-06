package com.example.babysitter.services;

import com.example.babysitter.externalModels.boundaries.ObjectBoundary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ParentService {

    @GET("/superapp/objects/search/byType/{type}")
    Call<List<ObjectBoundary>> loadAllParents(@Path("type") String type,
                                                  @Query("userSuperapp") String userSuperapp,
                                                  @Query("userEmail") String userEmail);



}
