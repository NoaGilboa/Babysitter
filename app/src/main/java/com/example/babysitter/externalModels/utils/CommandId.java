package com.example.babysitter.externalModels.utils;

public class CommandId {

    private String superapp;
    private String miniapp;
    private String id;

    public CommandId(String superapp, String miniapp, String id) {
        this.superapp = superapp;
        this.miniapp = miniapp;
        this.id = id;
    }

    public String getSuperapp() {
        return superapp;
    }

    public void setSuperapp(String superapp) {
        this.superapp = superapp;
    }

    public String getMiniapp() {
        return miniapp;
    }

    public void setMiniapp(String miniapp) {
        this.miniapp = miniapp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommandId:/n " + "{superapp=" + superapp + "/n" + ", miniapp=" + miniapp + "/n, id=" + id + "}";
    }

}
