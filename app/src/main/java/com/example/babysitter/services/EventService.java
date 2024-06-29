package com.example.babysitter.services;

import com.example.babysitter.models.BabysittingEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EventService {

    @GET("/superapp/objects/search/byType/{babysittingEvent}")
    Call<List<BabysittingEvent>> loadBabysittingEvents(@Path("BabysittingEvent") String babysittingEvent);
}
