package com.example.kornel.alphaui.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Position implements Parcelable {
    private LatLon latLon;
    private long timeStamp;

    public Position() {

    }

    public Position(LatLon latLon, long timeStamp) {
        this.latLon = latLon;
        this.timeStamp = timeStamp;
    }

    public LatLon getLatLon() {
        return latLon;
    }

    public void setLatLon(LatLon latLon) {
        this.latLon = latLon;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Position{latLon=" + latLon + ", timeStamp=" + timeStamp + '}';
    }

    // Parcelling part

    public Position(Parcel in) {
        this.latLon = in.readParcelable(LatLon.class.getClassLoader());
        this.timeStamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.latLon, flags);
        dest.writeLong(this.timeStamp);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        public Position[] newArray(int size) {
            return new Position[size];
        }
    };
}
