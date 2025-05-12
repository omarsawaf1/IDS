package com.example.gui;


public class UserModel {
    private String uid;
    private String email;
    private String displayName;

    public void User(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
    }

    // Getters
    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName + " (" + email + ")";
    }
}

