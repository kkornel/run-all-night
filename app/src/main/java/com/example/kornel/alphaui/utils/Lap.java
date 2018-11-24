package com.example.kornel.alphaui.utils;

import android.os.Parcel;
import android.os.Parcelable;


public class Lap implements Parcelable {
    private LatLon latLon;
    private long time;

    public Lap() {

    }

    public Lap(LatLon latLon, long time) {
        this.latLon = latLon;
        this.time = time;
    }

    public LatLon getLatLon() {
        return latLon;
    }

    public long getTime() {
        return time;
    }

    public long getLatLonTimeStamp() {
        return latLon.getTimeStamp();
    }

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
        return "Lap{" + "latLng=" + latLon +", time=" + time +'}';
    }

    // Parcelling part

    public Lap(Parcel in) {
        this.latLon = in.readParcelable(LatLon.class.getClassLoader());
        this.time = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.latLon, flags);
        dest.writeLong(this.time);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LatLon createFromParcel(Parcel in) {
            return new LatLon(in);
        }

        public LatLon[] newArray(int size) {
            return new LatLon[size];
        }
    };
}
