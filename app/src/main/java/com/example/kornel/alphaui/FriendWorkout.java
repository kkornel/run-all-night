package com.example.kornel.alphaui;

import com.example.kornel.alphaui.utils.GpsBasedWorkout;

public class FriendWorkout {
    private String friendName;
    private String avatarUrl;
    private GpsBasedWorkout workout;

    public FriendWorkout() {
    }

    public FriendWorkout(String friendName, String avatarUrl, GpsBasedWorkout workout) {
        this.friendName = friendName;
        this.avatarUrl = avatarUrl;
        this.workout = workout;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public GpsBasedWorkout getWorkout() {
        return workout;
    }

    public void setWorkout(GpsBasedWorkout workout) {
        this.workout = workout;
    }
}
