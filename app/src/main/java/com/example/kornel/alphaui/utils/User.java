package com.example.kornel.alphaui.utils;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class User {
    private String userUid;
    private String avatarUrl;
    private String firstName;
    private String surname;
    private String email;
    private String lastWorkout;
    private HashMap<String, Boolean> friends;
    private HashMap<String, Boolean> sharedLocations;
    private int totalWorkouts;
    private long totalDuration;
    private double totalDistance;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String surname, String email) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.friends = new HashMap<>();
    }

    public User(String firstName, String surname, String email, String lastWorkout) {
        this(firstName, surname, email);
        this.lastWorkout = lastWorkout;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public HashMap<String, Boolean> getFriends() {
        return friends;
    }

    public HashMap<String, Boolean> getSharedLocations() {
        return sharedLocations;
    }

    public void setSharedLocations(HashMap<String, Boolean> sharedLocations) {
        this.sharedLocations = sharedLocations;
    }

    public void setFriends(HashMap<String, Boolean> friends) {
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

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    @Exclude
    public String getFullName() {
        return this.firstName + " " + this.surname;
    }

    @Override
    public String toString() {
        return "User{" +
                "userUid='" + userUid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", lastWorkout='" + lastWorkout + '\'' +
                ", friends=" + friends +
                ", sharedLocations=" + sharedLocations +
                ", totalWorkouts=" + totalWorkouts +
                ", totalDuration=" + totalDuration +
                ", totalDistance=" + totalDistance +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
