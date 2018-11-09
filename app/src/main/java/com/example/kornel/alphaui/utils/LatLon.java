package com.example.kornel.alphaui.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LatLon implements Parcelable {
    private double latitude;
    private double longitude;

    public LatLon() {
    }

    public LatLon(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    public static ArrayList<LatLng> latLonToLatLng(List<LatLon> latlons) {
        ArrayList<LatLng> arrayList = new ArrayList<>();
        for (LatLon latLon : latlons) {
            LatLng latLng = new LatLng(latLon.latitude, latLon.longitude);
            arrayList.add(latLng);
        }
        return arrayList;
    }

    // Parcelling part
    public LatLon(Parcel in){
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LatLon createFromParcel(Parcel in) {
            return new LatLon(in);
        }

        public LatLon[] newArray(int size) {
            return new LatLon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    @Override
    public String toString() {
        return "LatLon{" +
                "lat='" + latitude + '\'' +
                ", lon='" + longitude + '\'' +
                '}';
    }
}
