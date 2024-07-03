package com.example.babysitter.models;

import com.example.babysitter.externalModels.utils.CreatedBy;
import com.example.babysitter.externalModels.utils.Location;
import com.example.babysitter.externalModels.boundaries.ObjectBoundary;
import com.example.babysitter.externalModels.utils.UserId;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Parent extends User {
    private int numberOfChildren;
    private String maritalStatus;

    public Parent() {
        super();
    }

    public Parent(String uid, String name, String phone, String mail, String address, String password, int numberOfChildren, double latitude, double longitude) {
        super(uid, name, phone, mail, address, password, latitude, longitude);
        this.numberOfChildren = numberOfChildren;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public Parent setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
        return this;
    }

    public ObjectBoundary toBoundary() {
        ObjectBoundary objectBoundary = new ObjectBoundary();
        objectBoundary.setType(this.getClass().getSimpleName());
        objectBoundary.setAlias(this.getPassword());
        objectBoundary.setLocation(new Location(this.getLatitude(),this.getLongitude()));
        objectBoundary.setActive(true);
        CreatedBy user=new CreatedBy();
        user.setUserId((new UserId()).setEmail(this.getMail()));
        objectBoundary.setCreatedBy(user);
        Map<String, Object> details = new HashMap<>();
        details.put("numberOfChildren", this.numberOfChildren);
        details.put("maritalStatus", this.maritalStatus);
        details.put("name", this.getName());
        details.put("phone", this.getPhone());
        details.put("uid", this.getUid());
        details.put("latitude", this.getLatitude());
        details.put("longitude", this.getLongitude());
        details.put("mail", this.getMail());
        details.put("address", this.getAddress());
        details.put("password", this.getPassword());
        objectBoundary.setObjectDetails(details);
        return objectBoundary;
    }
    public Parent toParent(String json){
        Parent parent= new Gson().fromJson(json,Parent.class);
        return parent;
    }

}
