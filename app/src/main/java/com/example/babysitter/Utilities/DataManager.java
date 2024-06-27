package com.example.babysitter.Utilities;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.babysitter.ExternalModels.CreatedBy;
import com.example.babysitter.ExternalModels.NewUserBoundary;
import com.example.babysitter.ExternalModels.ObjectBoundary;
import com.example.babysitter.ExternalModels.Role;
import com.example.babysitter.ExternalModels.UserBoundary;
import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.Parent;
import com.example.babysitter.Models.User;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataManager {
    private RetrofitClient database;
    //private BabysitterService babysitterService;
    //private ParentService parentService;
    //private EventService eventService;
    private UserService userService;
    private String superapp=null;


    public DataManager() {
        this.database = RetrofitClient.getInstance();
        //this.babysitterService = database.getClient().create(BabysitterService.class);
        //this.parentService = database.getClient().create(ParentService.class);
        //this.eventService = database.getClient().create(EventService.class);
        this.userService = database.getClient().create(UserService.class);

    }

    public void createUser(String email, User user, OnUserCreationListener listenerCreate, OnDataSavedListener listenerSave) {
        NewUserBoundary NewUser = new NewUserBoundary();
        NewUser.setEmail(email);
        NewUser.setRole(Role.SUPERAPP_USER);
        NewUser.setUsername(email);
        NewUser.setAvatar("email");
        userService.createUser(NewUser).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(@NonNull Call<UserBoundary> call, @NonNull Response<UserBoundary> response) {
                if (response.isSuccessful()) {
                    UserBoundary userBoundary = response.body();
                    CreatedBy userId = new CreatedBy();
                    userId.setUserId(userBoundary.getUserId());
                    ObjectBoundary userData = user.toBoundary();
                    superapp = userData.getCreatedBy().getUserId().getSuperapp();
                    userData.setCreatedBy(userId);
                    listenerCreate.onUserCreated(email);
                    // Delay the user data creation by 10 seconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            userService.saveUserData(userData).enqueue(new Callback<ObjectBoundary>() {
                                @Override
                                public void onResponse(@NonNull Call<ObjectBoundary> call, @NonNull Response<ObjectBoundary> response) {
                                    if (response.isSuccessful()) {
                                        listenerSave.onSuccess();
                                    } else {
                                        logError(response, "saveUserData");
                                        listenerSave.onFailure(new Exception("Failed to save user data"));
                                    }
                                }
                                @Override
                                public void onFailure(@NonNull Call<ObjectBoundary> call, Throwable t) {
                                    listenerSave.onFailure(new Exception("Failed to save user"));
                                }
                            });
                        }
                    }, 10000); // 4-second delay
                } else {
                    logError(response, "createUser");
                    listenerCreate.onFailure(new Exception("Failed to create user: " + getErrorMessage(response)));

                }
            }

            @Override
            public void onFailure(@NonNull Call<UserBoundary> call, Throwable t) {
                listenerCreate.onFailure(new Exception("Failed "+   t.getMessage() ));
                Log.e("DataManager", "Error in createUser: " + t.getMessage());
            }
        });
    }

    public void loginUser(String email, String password, OnLoginListener listener) {
        Call<UserBoundary> userCall = userService.getUserById(superapp,email);
        userCall.enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(Call<UserBoundary> call, Response<UserBoundary> userResponse) {
                if (userResponse.isSuccessful() && userResponse.body() != null) {
                    UserBoundary user = userResponse.body();
                    Call<List<ObjectBoundary>> objectsCall = userService.getAllObjectsByPassword(password);
                    objectsCall.enqueue(new Callback<List<ObjectBoundary>>() {
                        @Override
                        public void onResponse(Call<List<ObjectBoundary>> call, Response<List<ObjectBoundary>> objectsResponse) {
                            if (objectsResponse.isSuccessful() && objectsResponse.body() != null) {
                                List<ObjectBoundary> allObjects = objectsResponse.body();
                                for (ObjectBoundary object : allObjects) {
                                    if (object.getCreatedBy().getUserId().getEmail().equals(email)) {
                                        if(object.getType().equals("Babysitter")){
                                            Babysitter babysitter = new Babysitter();
                                            babysitter.toBabysitter(new Gson().toJson(object,ObjectBoundary.class));
                                            listener.onSuccess(babysitter);
                                        } else if(object.getType().equals("Parent")){
                                            Parent parent = new Parent();
                                            parent.toParent(new Gson().toJson(object,ObjectBoundary.class));
                                            listener.onSuccess(parent);
                                        }
                                    }
                                }
                            } else {
                                listener.onFailure(new Exception("Failed to fetch objects"));
                            }
                        }
                        @Override
                        public void onFailure(Call<List<ObjectBoundary>> call, Throwable t) {
                            listener.onFailure(new Exception(t));
                        }
                    });
                } else {
                    listener.onFailure(new Exception("Failed to fetch user"));
                }
            }

            @Override
            public void onFailure(Call<UserBoundary> call, Throwable t) {
                listener.onFailure(new Exception(t));
            }
        });
    }



    private void logError(Response<?> response, String methodName) {
        try {
            Log.e("DataManager", "Error in " + methodName + ": " + response.errorBody().string() + " | HTTP Status Code: " + response.code());
        } catch (Exception e) {
            Log.e("DataManager", "Error in " + methodName + ": Could not read error body", e);
        }
    }

    private String getErrorMessage(Response<?> response) {
        try {
            return response.errorBody() != null ? response.errorBody().string() : "Unknown error";
        } catch (Exception e) {
            return "Could not read error body";
        }
    }

    public interface OnLoginListener {
        void onSuccess(User user);

        void onFailure(Exception exception);
    }

    public interface OnUserCreationListener {
        void onUserCreated(String email);

        void onFailure(Exception exception);
    }

    public interface OnDataSavedListener {
        void onSuccess();

        void onFailure(Exception exception);
    }

}
//            public void loginUser(String email, String password, Activity activity, OnLoginListener listener) {
//                mAuth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(activity, task -> {
//                            if (task.isSuccessful()) {
//                                String uid = mAuth.getCurrentUser().getUid();
//                                listener.onLoginSuccess(uid);
//                            } else {
//                                listener.onLoginFailure(task.getException());
//                            }
//                        });
//            }
//
//            public String getCurrentUserId() {
//                if (mAuth.getCurrentUser() != null) {
//                    return mAuth.getCurrentUser().getUid();
//                }
//                return null;
//            }
//
//
//
//    public void saveBabysitter(Babysitter babysitter) {
//        babysitterService.saveBabysitter(babysitter.toBoundary());
//    }
//
//    public void saveParent(Parent parent) {
//                parentService.saveParent(parent.toBoundary());
//    }
//

//    public Call<List<Babysitter>> loadAllBabysitters(OnBabysittersLoadedListener listener) {
//        Call<List<Babysitter>> call = babysitterService.loadAllBabysitters();
//        call.enqueue(new Callback<List<Babysitter>>() {
//            @Override
//            public void onResponse(Call<List<Babysitter>> call, Response<List<Babysitter>> response) {
//                if (response.isSuccessful()) {
//                    listener.onBabysittersLoaded(response.body());
//                } else {
//                    listener.onFailure(new Exception("Failed to load babysitters"));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Babysitter>> call, Throwable t) {
//                listener.onFailure(new Exception(t));
//            }
//        });
//        return call;
//    }

//    public Call<List<Parent>> loadAllParents(OnParentsloadedListener listener) {
//        Call<List<Parent>> call = parentService.loadAllParents();
//        call.enqueue(new Callback<List<Parent>>() {
//            @Override
//            public void onResponse(Call<List<Parent>> call, Response<List<Parent>> response) {
//                if (response.isSuccessful()) {
//                    listener.onBabysittersLoaded(response.body());
//                } else {
//                    listener.onFailure(new Exception("Failed to load babysitters"));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Babysitter>> call, Throwable t) {
//                listener.onFailure(new Exception(t));
//            }
//        });
//        return call;
//    }

//    public void sortBabysittersByDistance(String userId, List<Babysitter> babysitters, OnBabysittersSortedListener listener) {
//        DatabaseReference parentRef = database.getReference("Users").child("Parent").child(userId);
//        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Parent parent = dataSnapshot.getValue(Parent.class);
//                if (parent != null && parent.getLatitude() != 0 && parent.getLongitude() != 0) {
//                    Collections.sort(babysitters, (b1, b2) -> {
//                        double dist1 = calculateDistance(parent.getLatitude(), parent.getLongitude(), b1.getLatitude(), b1.getLongitude());
//                        double dist2 = calculateDistance(parent.getLatitude(), parent.getLongitude(), b2.getLatitude(), b2.getLongitude());
//                        return Double.compare(dist1, dist2);
//                    });
//                    listener.onSorted(new ArrayList<>(babysitters)); // Ensure a new list instance is passed
//                } else {
//                    Log.e("SortDistance", "Invalid or missing parent location data.");
//                    listener.onFailure(new Exception("Invalid or missing parent location data"));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                listener.onFailure(databaseError.toException());
//            }
//        });
//    }
//    public void sortBabysittersByDistance(String userId, List<Babysitter> babysitters, OnBabysittersSortedListener listener) {
//        Parent parent =
//        Call<List<Babysitter>> call = babysitterService.loadAllBabysitters();
//        call.enqueue(new Callback<List<Babysitter>>()
//        {
//            @Override
//            public void onResponse
//            (Call < List < Babysitter >> call, Response < List < Babysitter >> response){
//            if (response.isSuccessful()) {
//                listener.onBabysittersLoaded(response.body());
//            } else {
//                listener.onFailure(new Exception("Failed to load babysitters"));
//            }
//    }
//
//        @Override
//        public void onFailure (Call < List < Babysitter >> call, Throwable t){
//        listener.onFailure(new Exception(t));
//    }
//    });
//        return call;
//}


//private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
//    final int R = 6371; // Radius of the Earth in kilometers
//    double latDistance = Math.toRadians(lat2 - lat1);
//    double lonDistance = Math.toRadians(lon2 - lon1);
//    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
//            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                    Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//    return R * c;
//}


