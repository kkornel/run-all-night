package com.example.kornel.alphaui.utils;

import java.util.HashMap;

public class CurrentUserProfile {
    private static String userUid;
    private static String avatarUrl;
    private static String firstName;
    private static String surname;
    private static String fullName;
    private static String email;
    private static String lastWorkout;
    private static HashMap<String, Boolean> friends;
    private static HashMap<String, Boolean> sharedLocations;
    private static int totalWorkouts;
    private static long totalDuration;
    private static double totalDistance;

    private CurrentUserProfile() {

    }

    public static void setNewData(User user) {
        userUid = user.getUserUid();
        avatarUrl = user.getAvatarUrl();
        firstName = user.getFirstName();
        surname = user.getSurname();
        fullName = user.getFullName();
        email = user.getEmail();
        lastWorkout = user.getLastWorkout();
        friends = user.getFriends();
        sharedLocations = user.getSharedLocations();
        totalWorkouts = user.getTotalWorkouts();
        totalDuration = user.getTotalDuration();
        totalDistance = user.getTotalDistance();
    }

    public static HashMap<String, Boolean> getSharedLocations() {
        return sharedLocations;
    }

    public static void setSharedLocations(HashMap<String, Boolean> map) {
        sharedLocations = map;
    }

    public static String getUserUid() {
        return userUid;
    }

    public static String getAvatarUrl() {
        return avatarUrl;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getSurname() {
        return surname;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getEmail() {
        return email;
    }

    public static String getLastWorkout() {
        return lastWorkout;
    }

    public static HashMap<String, Boolean> getFriends() {
        return friends;
    }

    public static int getTotalWorkouts() {
        return totalWorkouts;
    }

    public static long getTotalDuration() {
        return totalDuration;
    }

    public static double getTotalDistance() {
        return totalDistance;
    }

    public static String logProfile() {
        return "CurrentUserProfile{" +
                "userUid='" + userUid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", lastWorkout='" + lastWorkout + '\'' +
                ", friends=" + friends +
                ", sharedLocations=" + sharedLocations +
                ", totalWorkouts=" + totalWorkouts +
                ", totalDistance=" + totalDistance +
                ", totalDuration=" + totalDuration +
                "avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
