package com.example.kornel.alphaui.utils;

import java.util.HashMap;

public class CurrentUserProfile {
    public static String userUid;
    public static String avatarUrl;
    public static String firstName;
    public static String surname;
    public static String fullName;
    public static String email;
    public static String lastWorkout;
    public static HashMap<String, Boolean> friends;

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
    }

    public static String logProfile() {
        return "CurrentUserProfile{" +
                "userUid='" + userUid + '\'' +
                "avatarUrl='" + avatarUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", lastWorkout='" + lastWorkout + '\'' +
                ", friends=" + friends +
                '}';
    }
}
