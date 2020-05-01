package com.example.smartcomplaint;

public class SmartComplaint {
    private String name;
    private String id;
    private String title;
    private String text;
    private String admin;
    private String photoUrl;

    public SmartComplaint() {
    }

    public SmartComplaint(String title, String admin, String text, String name, String id, String photoUrl) {
        this.title = title;
        this.admin = admin;
        this.text = text;
        this.photoUrl = photoUrl;
        this.name = name;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}