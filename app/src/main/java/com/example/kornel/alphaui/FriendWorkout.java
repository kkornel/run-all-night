package com.example.kornel.alphaui;

import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.GpsBasedWorkout;

public class FriendWorkout {
    private String friendName;
    private String avatarUrl;
    private WorkoutGpsSummary workout;

    public FriendWorkout() {
    }

    public FriendWorkout(String friendName, String avatarUrl, WorkoutGpsSummary workout) {
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

    public WorkoutGpsSummary getWorkout() {
        return workout;
    }

    public void setWorkout(WorkoutGpsSummary workout) {
        this.workout = workout;
    }
}
