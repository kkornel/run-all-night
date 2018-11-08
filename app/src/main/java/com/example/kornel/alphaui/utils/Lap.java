package com.example.kornel.alphaui.utils;

import com.google.android.gms.maps.model.LatLng;

public class Lap {
    private LatLng latLng;
    private long timeStamp;
    private long time;

    public Lap(LatLng latLng, long timeStamp, long time) {
        this.latLng = latLng;
        this.timeStamp = timeStamp;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Lap{" + "latLng=" + latLng +", timeStamp=" + timeStamp +", time=" + time +'}';
    }
}
