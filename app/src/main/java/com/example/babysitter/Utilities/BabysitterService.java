package com.example.babysitter.Utilities;

import com.example.babysitter.ExternalModels.ObjectBoundary;
import com.example.babysitter.Models.Babysitter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BabysitterService {

    @POST("/superapp/objects")
    Call<Void> saveBabysitter(@Body ObjectBoundary babysitter);

    @GET("/superapp/objects/search/byType/{Babysitter}")
    Call<List<Babysitter>> loadAllBabysitters(@Path("Babysitter") String babysitter);

}
