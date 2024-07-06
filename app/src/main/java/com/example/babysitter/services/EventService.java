package com.example.babysitter.services;

import com.example.babysitter.externalModels.boundaries.MiniAppCommandBoundary;
import com.example.babysitter.externalModels.boundaries.ObjectBoundary;
import com.example.babysitter.models.BabysittingEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventService {

    @POST("/superapp/miniapp/{miniAppName}")
    Call<List<Object>> loadAllBabysittingEvents(@Path("miniAppName") String miniAppName,
                                                    @Body MiniAppCommandBoundary commandBoundary);

    @POST("/superapp/objects")
    Call<ObjectBoundary> createEvent(@Body ObjectBoundary boundaryObject);


}
