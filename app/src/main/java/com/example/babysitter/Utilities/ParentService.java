package com.example.babysitter.Utilities;

import com.example.babysitter.ExternalModels.ObjectBoundary;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ParentService {


    @POST("/superapp/objects")
    Call<Void> saveParent(@Body ObjectBoundary parent);

}
