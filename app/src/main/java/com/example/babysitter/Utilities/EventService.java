package com.example.babysitter.Utilities;

import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.BabysittingEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {

    @GET("/superapp/objects/search/byType/{babysittingEvent}")
    Call<List<BabysittingEvent>> loadBabysittingEvents(@Path("BabysittingEvent") String babysittingEvent);
}
