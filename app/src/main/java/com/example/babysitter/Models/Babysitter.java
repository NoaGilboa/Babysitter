package com.example.babysitter.Models;

import com.example.babysitter.ExternalModels.CreatedBy;
import com.example.babysitter.ExternalModels.Location;
import com.example.babysitter.ExternalModels.ObjectBoundary;
import com.example.babysitter.ExternalModels.UserId;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Babysitter extends User {
    private String dateOfBirth;
    private boolean smoke;
    private String maritalStatus;
    private String description;
    private double hourlyWage;
    private double experience;

    public Babysitter() {
        super();
    }

    public Babysitter(String uid, String name, String phone, String mail, String address, String password,
                      String dateOfBirth, boolean smoke, String maritalStatus, String description, double hourlyWage, double experience, double latitude, double longitude) {
        super(uid, name, phone, mail, address, password, latitude, longitude);
        this.dateOfBirth = dateOfBirth;
        this.smoke = smoke;
        this.maritalStatus = maritalStatus;
        this.description = description;
        this.hourlyWage = hourlyWage;
        this.experience = experience;
    }

    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        LocalDate dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Babysitter setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }


    public boolean isSmoke() {
        return smoke;
    }

    public Babysitter setSmoke(boolean smoke) {
        this.smoke = smoke;
        return this;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public Babysitter setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Babysitter setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public Babysitter setHourlyWage(double hourlyWage) {
        this.hourlyWage = hourlyWage;
        return this;
    }

    public double getExperience() {
        return experience;
    }

    public Babysitter setExperience(double experience) {
        this.experience = experience;
        return this;
    }

    public ObjectBoundary toBoundary() {
        ObjectBoundary objectBoundary = new ObjectBoundary();
        objectBoundary.setType(this.getClass().toString());
        objectBoundary.setAlias(this.getName());
        objectBoundary.setLocation(new Location(this.getLatitude(),this.getLongitude()));
        objectBoundary.setActive(true);
        CreatedBy user=new CreatedBy();
        user.setUserId((new UserId()).setEmail(this.getMail()));
        objectBoundary.setCreatedBy(user);
        Map<String, Object> details = new HashMap<>();
        details.put("smoke", this.smoke);
        details.put("maritalStatus", this.maritalStatus);
        details.put("description", this.description);
        details.put("hourlyWage", this.hourlyWage);
        details.put("experience", this.experience);
        details.put("name", this.getName());
        details.put("phone", this.getPhone());
        details.put("dateOfBirth", this.dateOfBirth);
        details.put("uid", this.getUid());
        details.put("latitude", this.getLatitude());
        details.put("longitude", this.getLongitude());
        details.put("mail", this.getMail());
        details.put("address", this.getAddress());
        details.put("password", this.getPassword());
        objectBoundary.setObjectDetails(details);
        return objectBoundary;
    }

    public Babysitter toBabysitter(String json){
        Babysitter babysitter= new Gson().fromJson(json,Babysitter.class);
        return babysitter;
    }
}

