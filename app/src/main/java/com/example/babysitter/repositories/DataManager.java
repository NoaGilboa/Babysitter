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
    private final BabysitterService babysitterService;
    private final ParentService parentService;
    private final EventService eventService;
    private final UserService userService;
    private String superapp = "2024b.yarden.cherry";
    private static String currentUserEmail = "";

    public DataManager() {
        RetrofitClient database = RetrofitClient.getInstance();
        this.babysitterService = database.getClient().create(BabysitterService.class);
        this.parentService = database.getClient().create(ParentService.class);
        this.eventService = database.getClient().create(EventService.class);
        this.userService = database.getClient().create(UserService.class);
    }


    public void logout(OnLogoutListener listener) {
        setCurrentUserEmail("");
        listener.onLogoutSuccess();
    }

    public void createUser(String email, User user, OnUserCreationListener listenerCreate, OnDataSavedListener listenerSave, OnUserUpdateListener listenerUpdate) {
        NewUserBoundary newUser = createNewUserBoundary(email, Role.SUPERAPP_USER, email, user.getPassword());
        userService.createUser(newUser).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(@NonNull Call<UserBoundary> call, @NonNull Response<UserBoundary> response) {
                if (response.isSuccessful()) {
                    UserBoundary userBoundary = response.body();
                    if (userBoundary != null) {
                        CreatedBy createdBy = new CreatedBy(new UserId(superapp, email));
                        ObjectBoundary userData = user.toBoundary();
                        userData.setCreatedBy(createdBy);
                        userData.setAlias(user.getPassword());
                        listenerCreate.onUserCreated(email);
                        new Handler().postDelayed(() -> saveUserData(user, userData, userBoundary, listenerSave, listenerUpdate), 4000);
                    } else {
                        listenerCreate.onFailure(new Exception("Failed to create user: response body is null"));
                    }
                } else {
                    logError(response, "createUser");
                    listenerCreate.onFailure(new Exception("Failed to create user: " + getErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserBoundary> call, @NonNull Throwable t) {
                listenerCreate.onFailure(new Exception("Failed to create user: " + t.getMessage()));
                Log.e("DataManager", "Error in createUser: " + t.getMessage());
            }
        });
    }

    private void saveUserData(User user, ObjectBoundary userData, UserBoundary userBoundary, OnDataSavedListener listenerSave, OnUserUpdateListener listenerUpdate) {
        userService.saveUserData(userData).enqueue(new Callback<ObjectBoundary>() {
            @Override
            public void onResponse(@NonNull Call<ObjectBoundary> call, @NonNull Response<ObjectBoundary> response) {
                if (response.isSuccessful()) {
                    listenerSave.onSuccess();
                    userBoundary.setUsername(response.body().getObjectId().getId());
                    userBoundary.setRole(Role.MINIAPP_USER);
                    userData.getObjectDetails().put("uid", response.body().getObjectId().getId());
                    userData.getObjectId().setSuperapp(response.body().getObjectId().getSuperapp());
                    userData.getObjectId().setId(response.body().getObjectId().getId());
                    Log.d("DataManager", "UserData: " + userData);
                    updateObject(userData, listenerUpdate);
                    updateUser(userBoundary, listenerUpdate);
                } else {
                    logError(response, "saveUserData");
                    listenerSave.onFailure(new Exception("Failed to save user data"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ObjectBoundary> call, @NonNull Throwable t) {
                listenerSave.onFailure(new Exception("Failed to save user data: " + t.getMessage()));
            }
        });
    }

    private void updateUser(UserBoundary userBoundary, OnUserUpdateListener listenerUpdate) {
        userService.updateUser(userBoundary.getUserId().getSuperapp(), userBoundary.getUserId().getEmail(), userBoundary).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    listenerUpdate.onSuccess();
                } else {
                    logError(response, "updateUser");
                    listenerUpdate.onFailure(new Exception("Failed to update user: " + getErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                listenerUpdate.onFailure(new Exception("Failed to update user: " + t.getMessage()));
            }
        });
    }

    private void updateObject(ObjectBoundary objectBoundary, OnUserUpdateListener listenerUpdate) {
        userService.updateObject(objectBoundary.getObjectId().getId(), objectBoundary.getObjectId().getSuperapp(), objectBoundary.getObjectId().getSuperapp(), objectBoundary.getCreatedBy().getUserId().getEmail(), objectBoundary)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            listenerUpdate.onSuccess();
                        } else {
                            logError(response, "updateUser");
                            listenerUpdate.onFailure(new Exception("Failed to update user: " + getErrorMessage(response)));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        listenerUpdate.onFailure(new Exception("Failed to update user: " + t.getMessage()));
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
                    setSuperapp(user.getUserId().getSuperapp());
                    fetchUserPassword(user, password, listener);
                } else {
                    listener.onFailure(new Exception("User not found"));
                }
            }

            @Override
            public void onFailure(Call<UserBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Network error during user fetch: " + t.getMessage()));
            }
        });
    }

    private void setSuperapp(String superapp) {
        this.superapp = superapp;
    }

    private void fetchUserPassword(UserBoundary user, String password, OnLoginListener listener) {
        userService.getObjectById(user.getUsername(), superapp, user.getUserId().getSuperapp(), user.getUserId().getEmail()).enqueue(new Callback<ObjectBoundary>() {
            @Override
            public void onResponse(Call<ObjectBoundary> call, Response<ObjectBoundary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ObjectBoundary object = response.body();
                    if (object.getCreatedBy().getUserId().getEmail().equals(user.getUserId().getEmail()) && object.getAlias().equals(password)) {
                        if (object.getType().equals(Babysitter.class.getSimpleName())) {
                            Babysitter babysitter = new Gson().fromJson(new Gson().toJson(object.getObjectDetails()), Babysitter.class);
                            listener.onSuccess(babysitter);
                        } else if (object.getType().equals(Parent.class.getSimpleName())) {
                            listener.onFailure(new Exception("User not found"));

//                            Parent parent = new Gson().fromJson(new Gson().toJson(object.getObjectDetails()), Parent.class);
//                            listener.onSuccess(parent);
                        }
                    } else {
                        listener.onFailure(new Exception("Incorrect password"));
                    }
                } else {
                    listener.onFailure(new Exception("Password fetch failed"));
                }
            }

            @Override
            public void onFailure(Call<ObjectBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Network error during password fetch: " + t.getMessage()));
            }
        });
    }

    public void loadAllBabysitters(OnBabysittersLoadedListener listener) {
        babysitterService.loadAllBabysitters(Babysitter.class.getSimpleName(), superapp, getCurrentUserEmail()).enqueue(new Callback<List<ObjectBoundary>>() {
            @Override
            public void onResponse(Call<List<ObjectBoundary>> call, Response<List<ObjectBoundary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Babysitter> babysitters = convertObjectBoundaryToBabysitters(response.body());
                    listener.onBabysittersLoaded(babysitters);
                } else {
                    listener.onFailure(new Exception("Failed to load babysitters"));
                }
            }

            @Override
            public void onFailure(Call<List<ObjectBoundary>> call, Throwable t) {
                listener.onFailure(new Exception("Failed to load babysitters: " + t.getMessage()));
            }
        });
    }

    private List<Babysitter> convertObjectBoundaryToBabysitters(List<ObjectBoundary> objects) {
        List<Babysitter> babysitters = new ArrayList<>();

        for (ObjectBoundary object : objects) {
            Babysitter babysitter = new Gson().fromJson(new Gson().toJson(object.getObjectDetails()), Babysitter.class);
            babysitters.add(babysitter);
        }

        return babysitters;
    }

    public void sortBabysittersByDistance(OnBabysittersLoadedListener listener) {
        userService.getUserById(superapp, currentUserEmail).enqueue(new Callback<UserBoundary>() {
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
                listener.onFailure(new Exception("Failed to fetch user: " + t.getMessage()));
            }
        });
    }

    private void fetchUserLocation(UserBoundary user, OnBabysittersLoadedListener listener) {
        userService.getObjectById(user.getUsername(), superapp, user.getUserId().getSuperapp(), user.getUserId().getEmail()).enqueue(new Callback<ObjectBoundary>() {
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
                listener.onFailure(new Exception("Failed to fetch user location: " + t.getMessage()));
            }
        });
    }

    private void fetchBabysittersByDistance(UserBoundary user, double latitude, double longitude, OnBabysittersLoadedListener listener) {
        MiniAppCommandBoundary command = createCommand(
                "GetAllObjectsByTypeAndLocationAndActive",
                user,
                "type", Babysitter.class.getSimpleName(),
                "latitude", String.valueOf(latitude),
                "longitude", String.valueOf(longitude));
        babysitterService.loadAllBabysittersByDistance(Babysitter.class.getSimpleName(), command)
                .enqueue(new Callback<List<Object>>() {
                    @Override
                    public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Babysitter> babysitters = convertObjectsToBabysitters(response.body());
                            listener.onBabysittersLoaded(babysitters);
                        } else {
                            logError(response, "fetchBabysittersByDistance");
                            listener.onFailure(new Exception("Failed to load babysitters"));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Object>> call, Throwable t) {
                        listener.onFailure(new Exception("Failed to load babysitters: " + t.getMessage()));
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
        CommandId commandId = new CommandId(user.getUserId().getSuperapp(), args[1], "1");
        miniappCommand.setCommandId(commandId);
        miniappCommand.setCommand(command);
        miniappCommand.setInvokedBy(new CreatedBy(new UserId(user.getUserId().getSuperapp(), user.getUserId().getEmail())));
        miniappCommand.setTargetObject(new TargetObject(new ObjectId(user.getUserId().getSuperapp(), user.getUsername())));
        miniappCommand.setCommandAttributes(new HashMap<>());

        if (args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                miniappCommand.getCommandAttributes().put(args[i], args[i + 1]);
                Log.d("DataManager", "Command attribute: " + args[i] + " = " + args[i + 1]);
            }
        } else {
            throw new IllegalArgumentException("Args should be key-value pairs");
        }
        Log.d("DataManager", "Command: " + miniappCommand);
        return miniappCommand;
    }

    public void createEvent(String message, String date, String babysitterId, OnDataSavedListener listenerSave) {
        updateUserRole(currentUserEmail, Role.SUPERAPP_USER, new OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                userService.getUserById(superapp, currentUserEmail).enqueue(new Callback<UserBoundary>() {
                    @Override
                    public void onResponse(Call<UserBoundary> call, Response<UserBoundary> userResponse) {
                        if (userResponse.isSuccessful() && userResponse.body() != null) {
                            String parentId = userResponse.body().getUsername();
                            BabysittingEvent babysittingEvent = createBabysittingEvent(message, date, babysitterId, parentId);
                            ObjectBoundary objectBoundary = babysittingEvent.toBoundary();
                            objectBoundary.getCreatedBy().getUserId().setSuperapp(superapp);
                            Log.d("DataManager", "Event: " + objectBoundary);
                            eventService.createEvent(objectBoundary).enqueue(new Callback<ObjectBoundary>() {
                                @Override
                                public void onResponse(@NonNull Call<ObjectBoundary> call, @NonNull Response<ObjectBoundary> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.d("DataManager", "Event saved successfully: " + response.body());
                                        listenerSave.onSuccess();
                                        BabysittingEvent savedEvent = babysittingEvent.toBabysittingEvent(new Gson().toJson(response.body()));
                                        updateEvent(response.body().getObjectId().getId(), savedEvent, new OnUserUpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d("DataManager", "Message id updated successfully");

                                            }

                                            @Override
                                            public void onFailure(Exception exception) {
                                                Log.e("DataManager", "Failed to update Message: " + exception.getMessage());

                                            }
                                        });
                                        updateUserRole(currentUserEmail, Role.MINIAPP_USER, new OnUserUpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d("DataManager", "User role updated to MINIAPP_USER successfully");
                                            }

                                            @Override
                                            public void onFailure(Exception exception) {
                                                Log.e("DataManager", "Failed to update user role to MINIAPP_USER: " + exception.getMessage());
                                            }
                                        });
                                        Log.d("DataManager", "Event saved successfully");
                                    } else {
                                        logError(response, "createEvent");
                                        listenerSave.onFailure(new Exception("Failed to save event data"));
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ObjectBoundary> call, @NonNull Throwable t) {
                                    listenerSave.onFailure(new Exception("Failed to save event data: " + t.getMessage()));
                                    Log.e("DataManager", "Failed to save event data: " + t.getMessage());
                                }
                            });
                        } else {
                            listenerSave.onFailure(new Exception("Failed to fetch parent ID"));
                            Log.e("DataManager", "Failed to fetch parent ID");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserBoundary> call, Throwable t) {
                        listenerSave.onFailure(new Exception("Network error during parent ID fetch: " + t.getMessage()));
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("DataManager", "Failed to update user role to SUPERAPP_USER: " + exception.getMessage());
            }
        });
    }

    private BabysittingEvent createBabysittingEvent(String message, String date, String babysitterId, String parentId) {
        BabysittingEvent babysittingEvent = new BabysittingEvent();

        babysittingEvent.setMessageId(message);
        babysittingEvent.setMessageText(message);
        babysittingEvent.setBabysitterUid(babysitterId);
        babysittingEvent.setSelectedDate(date);
        babysittingEvent.setMailParent(currentUserEmail);
        babysittingEvent.setStatus(false);
        babysittingEvent.setParentUid(parentId);

        return babysittingEvent;
    }

    public void loadAllEvents(int page, int size, OnEventsLoadedListener listener) {
        updateUserRole(currentUserEmail, Role.MINIAPP_USER, new OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                userService.getUserById(superapp, currentUserEmail).
                        enqueue(new Callback<UserBoundary>() {
                            @Override
                            public void onResponse
                                    (Call<UserBoundary> call, Response<UserBoundary> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    MiniAppCommandBoundary command = createCommand(
                                            "GetAllObjectsByTypeAndAliasAndActive", response.body(),
                                            "type", BabysittingEvent.class.getSimpleName(),
                                            "alias", response.body().getUsername(),
                                            "page", String.valueOf(page),
                                            "size", String.valueOf(size));

                                    eventService.loadAllBabysittingEvents(BabysittingEvent.class.getSimpleName(), command)
                                            .enqueue(new Callback<List<Object>>() {
                                                @Override
                                                public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        List<BabysittingEvent> events = convertObjectsToEvents(response.body());
                                                        listener.onEventsLoaded(events);
                                                    } else {
                                                        listener.onFailure(new Exception("Failed to load messages"));
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<List<Object>> call, Throwable t) {
                                                    listener.onFailure(new Exception("Failed to load messages: " + t.getMessage()));
                                                }
                                            });

                                } else {
                                    logError(response, "createEvent");
                                    listener.onFailure(new Exception("Failed to save event data"));
                                }
                            }

                            @Override
                            public void onFailure(Call<UserBoundary> call, Throwable t) {
                                listener.onFailure(new Exception("Network error during parent ID fetch: " + t.getMessage()));
                            }
                        });
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("DataManager", "Failed to update user role to SUPERAPP_USER: " + exception.getMessage());
            }
        });
    }

    public void loadAllEventsSorted(String sort, int page, int size, OnEventsLoadedListener listener) {
        updateUserRole(currentUserEmail, Role.MINIAPP_USER, new OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                userService.getUserById(superapp, currentUserEmail).
                        enqueue(new Callback<UserBoundary>() {
                            @Override
                            public void onResponse
                                    (Call<UserBoundary> call, Response<UserBoundary> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    MiniAppCommandBoundary command = createCommand(
                                            "GetAllObjectsByTypeAndAliasAndActiveSorted", response.body(),
                                            "type", BabysittingEvent.class.getSimpleName(),
                                            "alias", response.body().getUsername(),
                                            "sort", sort,
                                            "page", String.valueOf(page),
                                            "size", String.valueOf(size));

                                    eventService.loadAllBabysittingEvents(BabysittingEvent.class.getSimpleName(), command)
                                            .enqueue(new Callback<List<Object>>() {
                                                @Override
                                                public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        List<BabysittingEvent> events = convertObjectsToEvents(response.body());
                                                        listener.onEventsLoaded(events);
                                                    } else {
                                                        listener.onFailure(new Exception("Failed to load messages"));
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<List<Object>> call, Throwable t) {
                                                    listener.onFailure(new Exception("Failed to load messages: " + t.getMessage()));
                                                }
                                            });

                                } else {
                                    logError(response, "createEvent");
                                    listener.onFailure(new Exception("Failed to save event data"));
                                }
                            }
                            @Override
                            public void onFailure(Call<UserBoundary> call, Throwable t) {
                                listener.onFailure(new Exception("Network error during parent ID fetch: " + t.getMessage()));
                            }
                        });
            }
            @Override
            public void onFailure(Exception exception) {
                Log.e("DataManager", "Failed to update user role to SUPERAPP_USER: " + exception.getMessage());
            }
        });
    }


    private List<BabysittingEvent> convertObjectsToEvents(List<Object> objects) {
        List<BabysittingEvent> events = new ArrayList<>();
        String json = new Gson().toJson(objects);
        ArrayList<ObjectBoundary> allObjects = new Gson().fromJson(json, new TypeToken<ArrayList<ObjectBoundary>>() {
        }.getType());

        for (Object object : allObjects) {
            ObjectBoundary objectBoundary = new Gson().fromJson(new Gson().toJson(object), ObjectBoundary.class);
            BabysittingEvent event = new Gson().fromJson(new Gson().toJson(objectBoundary.getObjectDetails()), BabysittingEvent.class);
            events.add(event);
            Log.d("DataManager", "Event: " + event);
        }

        return events;
    }

    private void updateUserRole(String email, Role role, OnUserUpdateListener listener) {
        userService.getUserById(superapp, email).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(@NonNull Call<UserBoundary> call, @NonNull Response<UserBoundary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserBoundary userBoundary = response.body();
                    userBoundary.setRole(role);
                    userService.updateUser(userBoundary.getUserId().getSuperapp(), email, userBoundary).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                listener.onSuccess();
                            } else {
                                listener.onFailure(new Exception("Failed to update user role: " + getErrorMessage(response)));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            listener.onFailure(new Exception("Failed to update user role: " + t.getMessage()));
                        }
                    });
                } else {
                    listener.onFailure(new Exception("Failed to fetch user for role update: " + getErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserBoundary> call, @NonNull Throwable t) {
                listener.onFailure(new Exception("Network error during role update: " + t.getMessage()));
            }
        });
    }

    public void getParent(String parentId, OnParentLoadedListener listener) {
        userService.getObjectById(parentId, superapp, superapp, currentUserEmail)
                .enqueue(new Callback<ObjectBoundary>() {
                    @Override
                    public void onResponse(Call<ObjectBoundary> call, Response<ObjectBoundary> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Parent parent = new Gson().fromJson(new Gson().toJson(response.body().getObjectDetails()), Parent.class);
                            listener.onParentLoaded(parent);
                        } else {
                            listener.onFailure(new Exception("Failed to fetch parent data"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ObjectBoundary> call, Throwable t) {
                        listener.onFailure(new Exception("Failed to fetch parent data: " + t.getMessage()));
                    }
                });
    }

    private NewUserBoundary createNewUserBoundary(String email, Role role, String
            userName, String avatar) {
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail(email);
        user.setRole(role);
        user.setUsername(userName);
        user.setAvatar(avatar);
        return user;
    }

    public void setCurrentUserEmail(String email) {
        currentUserEmail = email;
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

    private void logError(Response<?> response, String methodName) {
        try {
            Log.e("DataManager", "Error in " + methodName + ": " + response.errorBody().string() + " | HTTP Status Code: " + response.code());
        } catch (Exception e) {
            Log.e("DataManager", "Error in " + methodName + ": Could not read error body", e);
        }
    }

    public void updateEvent(String id, BabysittingEvent babysittingEvent, OnUserUpdateListener listenerUpdate) {
        babysittingEvent.setMessageId(id);
        ObjectBoundary objectBoundary = babysittingEvent.toBoundary();
        objectBoundary.getObjectId().setSuperapp(superapp);
        objectBoundary.getCreatedBy().getUserId().setEmail(currentUserEmail);

        // Step 1: Update the user role to SUPERAPP_USER
        updateUserRole(objectBoundary.getCreatedBy().getUserId().getEmail(), Role.SUPERAPP_USER, new OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                // Step 2: Update the event object
                updateObject(objectBoundary, new OnUserUpdateListener() {
                    @Override
                    public void onSuccess() {
                        // Step 3: Update the user role back to MINIAPP_USER
                        updateUserRole(objectBoundary.getCreatedBy().getUserId().getEmail(), Role.MINIAPP_USER, new OnUserUpdateListener() {
                            @Override
                            public void onSuccess() {
                                // Notify the original listener that the whole operation was successful
                                listenerUpdate.onSuccess();
                            }
                            @Override
                            public void onFailure(Exception exception) {
                                // Notify the original listener about the failure in the final step
                                listenerUpdate.onFailure(exception);
                            }
                        });
                    }
                    @Override
                    public void onFailure(Exception exception) {
                        // Notify the original listener about the failure in the event update step
                        listenerUpdate.onFailure(exception);
                    }
                });
            }
            @Override
            public void onFailure(Exception exception) {
                // Notify the original listener about the failure in the initial user role update step
                listenerUpdate.onFailure(exception);
            }
        });
    }


    public interface OnLogoutListener {
        void onLogoutSuccess();

        void onLogoutFailure(Exception exception);
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

    public interface OnLoginListener {
        void onSuccess(User user);

        void onFailure(Exception exception);
    }

    public interface OnBabysittersLoadedListener {
        void onBabysittersLoaded(List<Babysitter> babysitters);

        void onFailure(Exception exception);
    }

    public interface OnEventsLoadedListener {
        void onEventsLoaded(List<BabysittingEvent> events);

        void onFailure(Exception exception);
    }

    public interface OnBabysittersSortedListener {
        void onSorted(List<Babysitter> sortedBabysitters);

        void onFailure(Exception exception);
    }

    public interface OnParentLoadedListener {
        void onParentLoaded(Parent parent);

        void onFailure(Exception exception);
    }
}
