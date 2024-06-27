package com.example.babysitter.ExternalModels;

public class UserId {

    private String superapp;
    private String email;

    public UserId() {
    }

    public String getEmail() {
        return this.email;
    }

    public UserId setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getSuperapp() {
        return superapp;
    }

    public void setSuperapp(String superAppName) {
        this.superapp = superAppName;
    }

    @Override
    public String toString() {
        return "UserId {superapp=" + superapp + ",/n email=" + email + "}";
    }

}