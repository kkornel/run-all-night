package com.example.kornel.alphaui.gpsworkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class WorkoutGpsSummary implements Parcelable {
    private String workoutName;
    private String duration;
    private double distance;
    private ArrayList<LatLng> path;

    public WorkoutGpsSummary() {
        // Default constructor required for calls to DataSnapshot.getValue(WorkoutGpsSummary.class)
    }


    public WorkoutGpsSummary(String workoutName, String duration, double distance, ArrayList<LatLng> path) {
        this.workoutName = workoutName;
        this.duration = duration;
        this.distance = distance;
        this.path = path;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public ArrayList<LatLng> getPath() {
        return path;
    }

    // Parcelable Part

    public WorkoutGpsSummary(Parcel in){
        this.workoutName = in.readString();
        this.duration = in.readString();
        this.distance = in.readDouble();
        path = new ArrayList<>();
        in.readList(this.path, LatLng.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.workoutName);
        dest.writeString(this.duration);
        dest.writeDouble(this.distance);
        dest.writeList(this.path);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WorkoutGpsSummary createFromParcel(Parcel in) {
            return new WorkoutGpsSummary(in);
        }

        public WorkoutGpsSummary[] newArray(int size) {
            return new WorkoutGpsSummary[size];
        }
    };
}
