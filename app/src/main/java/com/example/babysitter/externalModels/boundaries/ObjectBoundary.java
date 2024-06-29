package com.example.babysitter.externalModels.boundaries;

import com.example.babysitter.externalModels.utils.CreatedBy;
import com.example.babysitter.externalModels.utils.Location;
import com.example.babysitter.externalModels.utils.ObjectId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ObjectBoundary {

    private ObjectId objectId;
    private String type;
    private String alias;
    private Location location;
    private Boolean active;
    private Date creationTimestamp;
    private CreatedBy createdBy;
    private Map<String, Object> objectDetails;

    public ObjectBoundary() {
        objectDetails = new HashMap<>();
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, Object> getObjectDetails() {
        return objectDetails;
    }

    public void setObjectDetails(Map<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ObjectBoundary {objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active=" + active
                + ", location= " + location + ", creationTimestamp=" + creationTimestamp + ", createdBy=" + createdBy
                + ", objectDetails=" + objectDetails + "}";
    }
}
