package com.example.kornel.alphaui;

import android.location.Location;
import android.location.LocationManager;

import com.example.kornel.alphaui.utils.LatLon;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

public class SharedLocationInfo {
    private String userUid;
    private LatLon latLon;
    private String message;

    public SharedLocationInfo() {

    }

    public SharedLocationInfo(LatLon latLon, String message) {
        this.latLon = latLon;
        this.message = message;
    }

    public SharedLocationInfo(String userUid, LatLon latLon, String message) {
        this(latLon, message);
        this.userUid = userUid;
    }

    public LatLon getLatLon() {
        return latLon;
    }

    public String getMessage() {
        return message;
    }

    @Exclude
    public String getUserUid() {
        return userUid;
    }

    @Exclude
    public LatLng getLatLng() {
        return latLon.toLatLng();
    }

    @Exclude
    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLon.getLatitude());
        location.setLongitude(latLon.getLongitude());
        return location;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
