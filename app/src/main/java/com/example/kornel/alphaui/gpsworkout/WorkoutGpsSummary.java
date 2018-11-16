package com.example.kornel.alphaui.gpsworkout;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.kornel.alphaui.utils.LatLon;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutGpsSummary implements Parcelable {
    private static final String TAG = "WorkoutGpsSummary";

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";
    private  SimpleDateFormat sdf;

    private String date;
    private String workoutName;
    private String duration;
    private double distance;
    private ArrayList<LatLon> path;

    public WorkoutGpsSummary() {
        // Default constructor required for calls to DataSnapshot.getValue(WorkoutGpsSummary.class)
        this.sdf = new SimpleDateFormat(DATE_FORMAT);
    }

    public WorkoutGpsSummary(String workoutName, String duration, double distance, ArrayList<LatLon> path) {
        this();
        this.date = this.sdf.format(new Date());
        // this.date = new Date();
        this.workoutName = workoutName;
        this.duration = duration;
        this.distance = distance;
        this.path = path;
    }

    public String getDateString() {
        return date;
    }

    public Date getDate() {
        try {
            Date date = this.sdf.parse(this.date);
            return date;
        } catch (ParseException ex) {
            Log.e(TAG, "getDate: " + ex.getMessage());
            return new Date();
        }
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

    public ArrayList<LatLon> getPath() {
        return path;
    }

    // Parcelable Part

    public WorkoutGpsSummary(Parcel in){
        // this.date = new Date(in.readLong());
        this.date = in.readString();
        this.workoutName = in.readString();
        this.duration = in.readString();
        this.distance = in.readDouble();
        path = new ArrayList<>();
        in.readList(this.path, LatLon.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dest.writeLong(this.date.getTime());
        dest.writeString(this.date);
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
