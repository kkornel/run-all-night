package com.example.kornel.alphaui.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String avatarUrl;
    private String firstName;
    private String surname;
    private String email;
    private String lastWorkout;
    private List<String> friends;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String surname, String email) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.friends = new ArrayList<>();
    }

    public User(String firstName, String surname, String email, String lastWorkout) {
        this(firstName, surname, email);
        this.lastWorkout = lastWorkout;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getLastWorkout() {
        return lastWorkout;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastWorkout(String lastWorkout) {
        this.lastWorkout = lastWorkout;
    }
}
