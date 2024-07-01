package com.example.babysitter.repositories;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.babysitter.externalModels.boundaries.MiniAppCommandBoundary;
import com.example.babysitter.externalModels.boundaries.NewUserBoundary;
import com.example.babysitter.externalModels.boundaries.ObjectBoundary;
import com.example.babysitter.externalModels.boundaries.UserBoundary;
import com.example.babysitter.externalModels.utils.CommandId;
import com.example.babysitter.externalModels.utils.CreatedBy;
import com.example.babysitter.externalModels.utils.ObjectId;
import com.example.babysitter.externalModels.utils.Role;
import com.example.babysitter.externalModels.utils.TargetObject;
import com.example.babysitter.externalModels.utils.UserId;
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
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
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
    private final String superapp = "2024b.yarden.cherry";
    private static String currentUserEmail = "";

    public DataManager() {
        this.database = RetrofitClient.getInstance();
        this.babysitterService = database.getClient().create(BabysitterService.class);
        this.parentService = database.getClient().create(ParentService.class);
        this.eventService = database.getClient().create(EventService.class);
        this.userService = database.getClient().create(UserService.class);

    }

    public void logout(OnLogoutListener listener) {
        // Clear the current user email
        setCurrentUserEmail("");

        // Notify the server about the logout if needed (optional)
        // This step depends on your backend implementation.
        // If your backend requires notification of logout, add a corresponding API call here.

        listener.onLogoutSuccess();
    }

    public interface OnLogoutListener {
        void onLogoutSuccess();
        void onLogoutFailure(Exception exception);
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
                    //superapp = userData.getCreatedBy().getUserId().getSuperapp();
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
                                        userService.updateUser(userData.getCreatedBy().getUserId().getSuperapp(), email, userBoundary).enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                if (response.isSuccessful()) {
                                                    listenerUpdate.onSuccess();
                                                } else {
                                                    listenerUpdate.onFailure(new Exception("Failed to update user: " + getErrorMessage(response)));
                                                    logError(response, "updateUser");

                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                listenerUpdate.onFailure(new Exception("Failed to update user: " + t.getMessage()));
                                                logError(response, "updateUser");
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

        userService.getUserById(superapp, email).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(Call<UserBoundary> call, Response<UserBoundary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserBoundary user = response.body();

                    userService.getAllObjectsByPassword(password, 5, 0, superapp, email).enqueue(new Callback<List<ObjectBoundary>>() {
                        @Override
                        public void onResponse(Call<List<ObjectBoundary>> call, Response<List<ObjectBoundary>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<ObjectBoundary> allObjects = response.body();
                                for (ObjectBoundary object : allObjects) {
                                    if (object.getCreatedBy().getUserId().getEmail().equals(email) && object.getAlias().equals(password)) {
                                        if (object.getType().equals(Babysitter.class.getName())) {
                                            Babysitter babysitter = new Babysitter();
                                            babysitter = babysitter.toBabysitter(new Gson().toJson(object, ObjectBoundary.class));
                                            listener.onSuccess(babysitter);
                                            return;
                                        } else if (object.getType().equals(Parent.class.getName())) {
                                            Parent parent = new Parent();
                                            parent = parent.toParent(new Gson().toJson(object, ObjectBoundary.class));
                                            listener.onSuccess(parent);
                                            return;
                                        }
                                    }
                                }
                                listener.onFailure(new Exception("Incorrect password"));
                            } else {
                                listener.onFailure(new Exception("Password fetch failed"));
                            }
                        }

                        @Override
                        public void onFailure(Call<List<ObjectBoundary>> call, Throwable t) {
                            listener.onFailure(new Exception("Network error during password fetch"));
                        }
                    });
                } else {
                    listener.onFailure(new Exception("User not found"));
                }
            }

            @Override
            public void onFailure(Call<UserBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Network error during user fetch"));
            }
        });
    }


    public void loadAllBabysitters(OnBabysittersLoadedListener listener) {
        Call<List<ObjectBoundary>> call = babysitterService.loadAllBabysitters(Babysitter.class.getName(), superapp, getCurrentUserEmail());
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

    public void sortBabysittersByDistance(OnBabysittersLoadedListener listener) {
        Call<UserBoundary> userCall = userService.getUserById(superapp, currentUserEmail);
        userCall.enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(Call<UserBoundary> call, Response<UserBoundary> userResponse) {
                if (userResponse.isSuccessful() && userResponse.body() != null) {
                    UserBoundary user = userResponse.body();
                    fetchUserLocation(user, listener);
                } else {
                    listener.onFailure(new Exception("Failed to fetch user"));
                }
            }

            @Override
            public void onFailure(Call<UserBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Failed to fetch user"));
            }
        });
    }

    private void fetchUserLocation(UserBoundary user, OnBabysittersLoadedListener listener) {
        Call<ObjectBoundary> objectCall = userService.getObjectById(user.getUsername(), superapp, user.getUserId().getSuperapp(), user.getUserId().getEmail());
        objectCall.enqueue(new Callback<ObjectBoundary>() {
            @Override
            public void onResponse(Call<ObjectBoundary> call, Response<ObjectBoundary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ObjectBoundary objectBoundary = response.body();
                    double latitude = objectBoundary.getLocation().getLat();
                    double longitude = objectBoundary.getLocation().getLng();
                    fetchBabysittersByDistance(user, latitude, longitude, listener);
                } else {
                    listener.onFailure(new Exception("Failed to fetch user location"));
                }
            }

            @Override
            public void onFailure(Call<ObjectBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Failed to fetch user location"));
            }
        });
    }

    private void fetchBabysittersByDistance(UserBoundary user, double latitude, double longitude, OnBabysittersLoadedListener listener) {
        MiniAppCommandBoundary command = createCommand(
                "GetAllObjectsByTypeAndLocationAndActive",
                user,
                "type", Babysitter.class.getName(),
                "latitude", String.valueOf(latitude),
                "longitude", String.valueOf(longitude));

        babysitterService.loadAllBabysittersByDistance(Babysitter.class.getName(), command)
                .enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Object> objects = response.body();
                    List<Babysitter> babysitters = convertObjectsToBabysitters(objects);
                    listener.onBabysittersLoaded(babysitters);
                } else {
                    logError(response, "fetchBabysittersByDistance");
                    listener.onFailure(new Exception("Failed to load babysitters"));
                }
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                listener.onFailure(new Exception(t));
            }
        });
    }

    private List<Babysitter> convertObjectsToBabysitters(List<Object> objects) {
        List<Babysitter> babysitters = new ArrayList<>();
        String json = new Gson().toJson(objects);
        ArrayList<ObjectBoundary> allObjects = new Gson().fromJson(json, new TypeToken<ArrayList<ObjectBoundary>>() {
        }.getType());

        for (Object object : allObjects) {
            ObjectBoundary objectBoundary = new Gson().fromJson(new Gson().toJson(object), ObjectBoundary.class);
            Babysitter babysitter = new Gson().fromJson(new Gson().toJson(objectBoundary.getObjectDetails()), Babysitter.class);
            babysitters.add(babysitter);
        }

        return babysitters;
    }

    public MiniAppCommandBoundary createCommand(String command, UserBoundary user, String... args) {
        MiniAppCommandBoundary miniappCommand = new MiniAppCommandBoundary();
        miniappCommand.setCommandAttributes(new HashMap<>());
        CommandId commandObj = new CommandId();
        commandObj.setId("1");
        commandObj.setSuperapp(user.getUserId().getSuperapp());
        commandObj.setMiniapp(args[1]);
        miniappCommand.setCommandId(commandObj);
        miniappCommand.setCommand(command);
        CreatedBy invokedBy = new CreatedBy();
        invokedBy.setUserId(new UserId());
        invokedBy.getUserId().setSuperapp(user.getUserId().getSuperapp());
        invokedBy.getUserId().setEmail(user.getUserId().getEmail());
        miniappCommand.setInvokedBy(invokedBy);

        TargetObject target = new TargetObject();
        target.setObjectId(new ObjectId());
        target.getObjectId().setSuperapp(user.getUserId().getSuperapp());
        target.getObjectId().setId(user.getUsername());
        miniappCommand.setTargetObject(target);

        // Process args as key-value pairs
        if (args.length % 2 == 0) { // Ensure args are in pairs
            for (int i = 0; i < args.length; i += 2) {
                miniappCommand.getCommandAttributes().put(args[i], args[i + 1]);
            }
        } else {
            throw new IllegalArgumentException("Args should be key-value pairs");
        }
        Log.d("DataManager", "Command: " + miniappCommand);
        return miniappCommand;
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
