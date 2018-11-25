package com.example.kornel.alphaui.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;


public class Lap implements Parcelable {
    private Position position;
    private long time;

    public Lap() {

    }

    public Lap(Position position, long time) {
        this.position = position;
        this.time = time;
    }

    public Position getPosition() {
        return position;
    }

    public long getTime() {
        return time;
    }

    @Exclude
    public LatLon getLatLon() {
        return this.position.getLatLon();
    }

    @Exclude
    public long getPositionTimeStamp() {
        return position.getTimeStamp();
    }

    @Exclude
    public String getTimeString() {
        int seconds = (int) (time / 1000);

        int hours = seconds / 3600;

        int minutes = seconds / 60;

        seconds = seconds % 60;

        String sec;
        String min;
        String hour;

        if (seconds < 10) {
            sec = "0" + seconds;
        } else {
            sec = String.valueOf(seconds);
        }

        if (minutes < 10) {
            min = "0" + minutes;
        } else {
            min = String.valueOf(minutes);
        }

        if (hours < 10) {
            hour = "0" + hours;
        } else {
            hour = String.valueOf(hours);
        }

        if (hours != 0) {
            return hour + ":" + min + ":" + sec;
        } else {
            return min + ":" + sec;
        }
    }

    @Override
    public String toString() {
        return "Lap{" + "position=" + position +", time=" + time +'}';
    }

    // Parcelling part

    public Lap(Parcel in) {
        this.position = in.readParcelable(Position.class.getClassLoader());
        this.time = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.position, flags);
        dest.writeLong(this.time);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Lap createFromParcel(Parcel in) {
            return new Lap(in);
        }

        public Lap[] newArray(int size) {
            return new Lap[size];
        }
    };
}
