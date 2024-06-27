package com.example.babysitter.Utilities;

import androidx.annotation.NonNull;

import com.example.babysitter.ExternalModels.CreatedBy;
import com.example.babysitter.ExternalModels.NewUserBoundary;
import com.example.babysitter.ExternalModels.ObjectBoundary;
import com.example.babysitter.ExternalModels.Role;
import com.example.babysitter.ExternalModels.UserBoundary;
import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.BabysittingEvent;
import com.example.babysitter.Models.User;
import android.util.Log;

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
                    userData.setCreatedBy(userId);
                    listenerCreate.onUserCreated(email);
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
                } else {
                    logError(response, "createUser");
                    listenerCreate.onFailure(new Exception("Failed to create user"));
                }
            }

//                    userService.saveUserData(userData).enqueue(new Callback<ObjectBoundary>() {
//                        @Override
//                        public void onResponse(@NonNull Call<ObjectBoundary> call, @NonNull Response<ObjectBoundary> response) {
//                            if (response.isSuccessful()) {
//                                listenerSave.onSuccess();
//                            } else {
//                                logError(response, "saveUserData");
//                                listenerSave.onFailure(new Exception("Failed to save user data"));
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<ObjectBoundary> call, Throwable t) {
//                            listenerSave.onFailure(new Exception("Failed to save user"));
//                        }
//                    });
//                } else {
//                    logError(response, "createUser");
//                    listenerCreate.onFailure(new Exception("Failed to create user"));
//                }
//            }

            @Override
            public void onFailure(@NonNull Call<UserBoundary> call, Throwable t) {
                listenerCreate.onFailure(new Exception("Failed to create  "));
            }
        });
    }

    private void logError(Response<?> response, String methodName) {
        try {
            Log.e("DataManager", "Error in " + methodName + ": " + response.errorBody().string());
        } catch (Exception e) {
            Log.e("DataManager", "Error in " + methodName + ": Could not read error body", e);
        }
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


