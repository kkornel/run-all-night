package com.example.kornel.alphaui.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LatLon implements Parcelable {
    private double latitude;
    private double longitude;

    private long timeStamp;

    public LatLon() {

    }

    public LatLon(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLon(double latitude, double longitude, long timeStamp) {
        this(latitude, longitude);
        this.timeStamp = timeStamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public static ArrayList<LatLng> latLonToLatLng(List<LatLon> latlons) {
        ArrayList<LatLng> arrayList = new ArrayList<>();
        for (LatLon latLon : latlons) {
            LatLng latLng = new LatLng(latLon.latitude, latLon.longitude);
            arrayList.add(latLng);
        }
        return arrayList;
    }

    @Override
    public String toString() {
        return "LatLon{lat=" + latitude + ", lon=" + longitude + ", timeStamp=" + timeStamp + '}';
    }

    // Parcelling part

    public LatLon(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.timeStamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeLong(this.timeStamp);
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
