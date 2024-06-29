package com.example.babysitter.externalModels.boundaries;


import com.example.babysitter.externalModels.utils.Role;

public class NewUserBoundary {

    private String email;
    private Role role;
    private String username;
    private String avatar;

    public NewUserBoundary() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    @Override
    public String toString() {
        return "NewUserBoundary{" + "email='" + email + '\'' + ", role=" + role + ", username='" + username + '\''
                + ", avatar='" + avatar + '\'' + '}';
    }
}
