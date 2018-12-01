package com.example.kornel.alphaui;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;

public class FriendWorkout implements Parcelable {
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

    // Parcelling Part

    public FriendWorkout(Parcel in){
        // this.date = new Date(in.readLong());
        this.friendName = in.readString();
        this.avatarUrl = in.readString();
        this.workout = in.readParcelable(WorkoutGpsSummary.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dest.writeLong(this.date.getTime());
        dest.writeString(this.friendName);
        dest.writeString(this.avatarUrl);
        dest.writeParcelable(this.workout, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FriendWorkout createFromParcel(Parcel in) {
            return new FriendWorkout(in);
        }

        public FriendWorkout[] newArray(int size) {
            return new FriendWorkout[size];
        }
    };
}
