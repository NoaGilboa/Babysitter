package com.example.babysitter.Models;

import com.example.babysitter.ExternalModels.CreatedBy;
import com.example.babysitter.ExternalModels.Location;
import com.example.babysitter.ExternalModels.ObjectBoundary;
import com.example.babysitter.ExternalModels.UserId;

import java.util.HashMap;
import java.util.Map;

public class BabysittingEvent {
    private String messageId;
    private String messageText;
    private String selectedDate;
    private String parentUid;
    private String babysitterUid;
    private Boolean status;
    private String mailParent;

    public BabysittingEvent() {
    }

    // Constructor with parameters
    public BabysittingEvent(String parentUid, String babysitterUid, String messageText, String selectedDate, String mailParent) {
        this.messageText = messageText;
        this.selectedDate = selectedDate;
        this.parentUid = parentUid;
        this.babysitterUid = babysitterUid;
        this.status = null;
        this.mailParent = mailParent;
    }

    public String getMessageId() {
        return messageId;
    }

    public BabysittingEvent setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getParentUid() {
        return parentUid;
    }

    public BabysittingEvent setParentUid(String parentUid) {
        this.parentUid = parentUid;
        return this;
    }

    public String getBabysitterUid() {
        return babysitterUid;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public BabysittingEvent setStatus(boolean status) {
        this.status = status;
        return this;
    }
    public String getMailParent() {
        return mailParent;
    }
    public void setMailParent(String mailParent) {
        this.mailParent = mailParent;
    }

    public ObjectBoundary toBoundary() {
        ObjectBoundary objectBoundary = new ObjectBoundary();
        objectBoundary.setType(this.getClass().toString());
        objectBoundary.setAlias(this.getMessageText());
        objectBoundary.setLocation(new Location(0,0));
        objectBoundary.setActive(true);
        CreatedBy user=new CreatedBy();
        user.setUserId((new UserId()).setEmail(this.getMailParent()));
        objectBoundary.setCreatedBy(user);
        Map<String, Object> details = new HashMap<>();
        details.put("messageId", this.messageId);
        details.put("messageText", this.messageText);
        details.put("selectedDate", this.selectedDate);
        details.put("parentUid", this.parentUid);
        details.put("babysitterUid", this.babysitterUid);
        details.put("status", this.status);
        objectBoundary.setObjectDetails(details);
        return objectBoundary;
    }
}
