package com.example.babysitter.Models;

import com.example.babysitter.ExternalModels.ObjectBoundary;

public abstract class User {
    private String uid;
    private String name;
    private String phone;
    private String mail;
    private String address;
    private String password;
    private double latitude;
    private double longitude;


    public User() {
    }

    // Constructor for common fields
    public User(String uid, String name, String phone, String mail, String address, String password, double latitude, double longitude) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.mail = mail;
        this.address = address;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLatitude() {
        return latitude;
    }

    public User setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public User setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public abstract ObjectBoundary toBoundary();
}
