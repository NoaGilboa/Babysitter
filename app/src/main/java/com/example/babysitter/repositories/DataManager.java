package com.example.babysitter.repositories;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.babysitter.externalModels.utils.CreatedBy;
import com.example.babysitter.externalModels.boundaries.NewUserBoundary;
import com.example.babysitter.externalModels.boundaries.ObjectBoundary;
import com.example.babysitter.externalModels.utils.Role;
import com.example.babysitter.externalModels.boundaries.UserBoundary;
import com.example.babysitter.models.Babysitter;
import com.example.babysitter.models.BabysittingEvent;
import com.example.babysitter.models.Parent;
import com.example.babysitter.models.User;
import com.example.babysitter.services.BabysitterService;
import com.example.babysitter.services.EventService;
import com.example.babysitter.services.ParentService;
import com.example.babysitter.services.RetrofitClient;
import com.example.babysitter.services.UserService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataManager {
    private RetrofitClient database;
    private BabysitterService babysitterService;
    private ParentService parentService;
    private EventService eventService;
    private UserService userService;
    private static String superapp = "2024b.yarden.cherry";
    private static String currentUserEmail = "";

    public DataManager() {
        this.database = RetrofitClient.getInstance();
        this.babysitterService = database.getClient().create(BabysitterService.class);
        this.parentService = database.getClient().create(ParentService.class);
        this.eventService = database.getClient().create(EventService.class);
        this.userService = database.getClient().create(UserService.class);

    }

    public void createUser(String email, User user, OnUserCreationListener listenerCreate, OnDataSavedListener listenerSave, OnUserUpdateListener listenerUpdate) {
        NewUserBoundary newUser = new NewUserBoundary();
        newUser.setEmail(email);
        newUser.setRole(Role.SUPERAPP_USER);
        newUser.setUsername(email);
        newUser.setAvatar(user.getPassword());

        userService.createUser(newUser).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(@NonNull Call<UserBoundary> call, @NonNull Response<UserBoundary> response) {
                if (response.isSuccessful()) {
                    UserBoundary userBoundary = response.body();
                    CreatedBy userId = new CreatedBy();
                    userId.setUserId(userBoundary.getUserId());
                    ObjectBoundary userData = user.toBoundary();
                    setSuperapp(userData.getCreatedBy().getUserId().getSuperapp());
                    userData.setCreatedBy(userId);
                    userData.setAlias(user.getPassword());
                    listenerCreate.onUserCreated(email);

                    // Delay the user data creation by 4 seconds
                    new Handler().postDelayed(() -> {
                        userService.saveUserData(userData).enqueue(new Callback<ObjectBoundary>() {
                            @Override
                            public void onResponse(@NonNull Call<ObjectBoundary> call, @NonNull Response<ObjectBoundary> response) {
                                if (response.isSuccessful()) {
                                    listenerSave.onSuccess();
                                    ObjectBoundary objectBoundary = response.body();
                                    userBoundary.setUsername(objectBoundary.getObjectId().getId());
                                    userBoundary.setRole(Role.MINIAPP_USER);

                                    // Update user after another 4-second delay
                                    new Handler().postDelayed(() -> {
                                        userService.updateUser(superapp, email, userBoundary).enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                if (response.isSuccessful()) {
                                                    listenerUpdate.onSuccess();
                                                } else {
                                                    listenerUpdate.onFailure(new Exception("Failed to update user: " + getErrorMessage(response)));
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                listenerUpdate.onFailure(new Exception("Failed to update user: " + t.getMessage()));
                                            }
                                        });
                                    }, 10000); // 4-second delay
                                } else {
                                    logError(response, "saveUserData");
                                    listenerSave.onFailure(new Exception("Failed to save user data"));
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ObjectBoundary> call, Throwable t) {
                                listenerSave.onFailure(new Exception("Failed to save user data: " + t.getMessage()));
                            }
                        });
                    }, 4000); // 4-second delay
                } else {
                    logError(response, "createUser");
                    listenerCreate.onFailure(new Exception("Failed to create user: " + getErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserBoundary> call, Throwable t) {
                listenerCreate.onFailure(new Exception("Failed to create user: " + t.getMessage()));
                Log.e("DataManager", "Error in createUser: " + t.getMessage());
            }
        });
    }

    public void loginUser(String email, String password, OnLoginListener listener) {
        setCurrentUserEmail(email);
        Call<UserBoundary> userCall = userService.getUserById(getSuperapp(), email);
        userCall.enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(Call<UserBoundary> call, Response<UserBoundary> userResponse) {
                if (userResponse.isSuccessful() && userResponse.body() != null) {
                    UserBoundary user = userResponse.body();
                    Call<List<ObjectBoundary>> objectsCall = userService.getAllObjectsByPassword(password, 5, 0, superapp, email);
                    objectsCall.enqueue(new Callback<List<ObjectBoundary>>() {
                        @Override
                        public void onResponse(Call<List<ObjectBoundary>> call, Response<List<ObjectBoundary>> objectsResponse) {
                            if (objectsResponse.isSuccessful()) {
                                List<ObjectBoundary> allObjects = objectsResponse.body();
                                for (ObjectBoundary object : allObjects) {
                                    if (object.getCreatedBy().getUserId().getEmail().equals(email) && object.getAlias().equals(password)) {
                                        if (object.getType().equals(Babysitter.class.getName())) {
                                            Babysitter babysitter = new Babysitter();
                                            babysitter = babysitter.toBabysitter(new Gson().toJson(object, ObjectBoundary.class));
                                            listener.onSuccess(babysitter);
                                        } else if (object.getType().equals(Parent.class.getName())) {
                                            Parent parent = new Parent();
                                            parent = parent.toParent(new Gson().toJson(object, ObjectBoundary.class));
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

    public void loadAllBabysitters(OnBabysittersLoadedListener listener) {
        Call<List<ObjectBoundary>> call = babysitterService.loadAllBabysitters(Babysitter.class.getName(), getSuperapp(), getCurrentUserEmail());
        call.enqueue(new Callback<List<ObjectBoundary>>() {
            @Override
            public void onResponse(Call<List<ObjectBoundary>> call, Response<List<ObjectBoundary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ObjectBoundary> allObjects = response.body();
                    List<Babysitter> babysitters = new ArrayList<>();
                    for (ObjectBoundary object : allObjects) {
                        if (object.getType().equals(Babysitter.class.getName())) {
                            Babysitter babysitter = new Gson().fromJson(new Gson().toJson(object.getObjectDetails()), Babysitter.class);
                            babysitters.add(babysitter);
                        }
                    }
                    listener.onBabysittersLoaded(babysitters);
                } else {
                    listener.onFailure(new Exception("Failed to load babysitters"));
                }
            }

            @Override
            public void onFailure(Call<List<ObjectBoundary>> call, Throwable t) {
                listener.onFailure(new Exception(t));
            }
        });
    }

//    public void sortBabysittersByDistance(ArrayList<Babysitter> babysitters, OnBabysittersSortedListener listener) {
//
//        Parent parent;
//        if (parent != null && parent.getLatitude() != 0 && parent.getLongitude() != 0) {
//            Collections.sort(babysitters, (b1, b2) -> {
//                double dist1 = calculateDistance(parent.getLatitude(), parent.getLongitude(), b1.getLatitude(), b1.getLongitude());
//                double dist2 = calculateDistance(parent.getLatitude(), parent.getLongitude(), b2.getLatitude(), b2.getLongitude());
//                return Double.compare(dist1, dist2);
//            });
//            listener.onSorted(new ArrayList<>(babysitters)); // Ensure a new list instance is passed
//        } else {
//            Log.e("SortDistance", "Invalid or missing parent location data.");
//            listener.onFailure(new Exception("Invalid or missing parent location data"));
//        }
//    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public interface OnEventsLoadedListener {
        void onEventsLoaded(List<BabysittingEvent> events);

        void onFailure(Exception exception);
    }

    public interface OnBabysittersSortedListener {
        void onSorted(List<Babysitter> sortedBabysitters);

        void onFailure(Exception exception);
    }

    public interface OnBabysittersLoadedListener {
        void onBabysittersLoaded(List<Babysitter> babysitters);

        void onFailure(Exception exception);
    }

    public void setSuperapp(String superapp) {
        this.superapp = superapp;
    }

    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
    }

    public String getSuperapp() {
        return superapp;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
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

    private void logError(Response<?> response, String methodName) {
        try {
            Log.e("DataManager", "Error in " + methodName + ": " + response.errorBody().string() + " | HTTP Status Code: " + response.code());
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

    public interface OnUserUpdateListener {
        void onSuccess();
        void onFailure(Exception exception);
    }



}
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


//public void sortBabysittersByDistance(ArrayList<Babysitter> babysitters, OnBabysittersLoadedListener listener) {
//    MiniAppCommandBoundary miniappCommand = new MiniAppCommandBoundary();
//    miniappCommand.setCommandAttributes(new HashMap<>());
//    miniappCommand.setCommand("GetAllObjectsByTypeAndLocation");
//    CreatedBy invokedBy = new CreatedBy();
//    invokedBy.setUserId(new UserId());
//    invokedBy.getUserId().setSuperapp(superapp);
//    invokedBy.getUserId().setEmail(currentUserEmail);
//    miniappCommand.setInvokedBy(invokedBy);
//    TargetObject target = new TargetObject();
//    target.setObjectId(new ObjectId());
//    target.getObjectId().setSuperapp(superapp);
//    target.getObjectId().setId();
//    miniappCommand.setTargetObject(target);
//    Call<List<Object>> call = babysitterService.loadAllBabysittersByDistance(Babysitter.class.getName(), miniappCommand);
//    call.enqueue(new Callback<List<Object>>() {
//        @Override
//        public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
//            if (response.isSuccessful() && response.body() != null) {
//                List<Object> Objects = response.body();
//                List<Babysitter> babysitters = new ArrayList<>();
//                String json = new Gson().toJson(Objects);
//                ArrayList<ObjectBoundary> allObjects = new Gson().fromJson(json, new TypeToken<ArrayList<ObjectBoundary>>() {
//                }.getType());
//                for (Object object : allObjects) {
//                    ObjectBoundary objectBoundary = new Gson().fromJson(json, new TypeToken<ObjectBoundary>() {
//                    }.getType());
//                    Babysitter babysitter = new Gson().fromJson(new Gson().toJson(objectBoundary.getObjectDetails()), Babysitter.class);
//                    babysitters.add(babysitter);
//                }
//                listener.onBabysittersLoaded(babysitters);
//            } else {
//                listener.onFailure(new Exception("Failed to load babysitters"));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<List<Object>> call, Throwable t) {
//            listener.onFailure(new Exception(t));
//        }
//    });
//}
