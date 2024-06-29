package com.example.babysitter.services;

import com.example.babysitter.externalModels.boundaries.ObjectBoundary;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ParentService {


    @POST("/superapp/objects")
    Call<Void> saveParent(@Body ObjectBoundary parent);

}
