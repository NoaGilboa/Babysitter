package com.example.babysitter.externalModels.boundaries;


import com.example.babysitter.externalModels.utils.Role;
import com.example.babysitter.externalModels.utils.UserId;

public class UserBoundary {

    private UserId userId;
    private Role role;
    private String username;
    private String avatar;

    public UserBoundary() {
    }


    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserBoundary{" + "userId=" + userId + ", role=" + role + ", username='" + username + '\'' + ", avatar='"
                + avatar + '\'' + '}';
    }
}
