package com.example.kornel.alphaui.gpsworkout;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.kornel.alphaui.mainactivity.MainActivityLog;
import com.example.kornel.alphaui.utils.DateUtils;
import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.utils.Utils;
import com.example.kornel.alphaui.weather.WeatherInfoCompressed;
import com.google.firebase.database.Exclude;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkoutSummary implements Parcelable {
    private static final String TAG = "WorkoutSummary";

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

    private String workoutKey;

    private long dateMilliseconds;
    private String workoutName;
    private long duration;      // ms
    private double distance;    // m
    private double avgPace;     // [min/km]
    private double maxPace;     // [min/km]
    private double avgSpeed;    // [km/h]
    private double maxSpeed;    // [km/h]
    private String status;
    private String picUrl;
    private boolean privacy;
    private ArrayList<LatLon> path;
    private ArrayList<Lap> laps;
    private WeatherInfoCompressed weatherInfoCompressed;

    public WorkoutSummary() {
        // Default constructor required for calls to DataSnapshot.getValue(WorkoutSummary.class)
    }

    // Constructor for LocationTrackingService
    public WorkoutSummary(Date date, String workoutName, long duration, double distance, double avgPace, double maxPace, double avgSpeed, double maxSpeed, ArrayList<LatLon> path, ArrayList<Lap> laps) {
        this.dateMilliseconds = date.getTime();
        this.workoutName = workoutName;
        this.duration = duration;
        this.distance = distance;
        this.avgPace = avgPace;
        this.maxPace = maxPace;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.path = path;
        this.laps = laps;
    }


    // Constructor for IndoorWorkoutService
    public WorkoutSummary(Date date, String workoutName, long duration) {
        this.dateMilliseconds = date.getTime();
        this.workoutName = workoutName;
        this.duration = duration;

        status = null;
        picUrl = null;
        path = null;
        laps = null;
        weatherInfoCompressed = null;
    }

    private String[] getDateSeparated() {
        String now = new SimpleDateFormat(DATE_FORMAT).format(getWorkoutDate());

        String[] separated = now.split(" ");
        String dayName = separated[0].substring(0, separated[0].length() - 1);
        dayName = DateUtils.convertDayName(dayName);
        String day = separated[1];
        String month = separated[2];
        month = DateUtils.convertMonthToFullName(month);
        String year = separated[3];
        String time = separated[4];

        return new String[]{dayName, day, month, year, time};
    }

    @Exclude
    public String getFullDateStringPlWithTime() {
        String[] separated = getDateSeparated();
        String dayName = separated[0];
        String day = separated[1];
        String month = separated[2];
        String year = separated[3];
        String time = separated[4];
        time = getTimeHourMin();

        if (day.startsWith("0")) {
            day = day.substring(1);
        }

        String date = dayName + ", " + day + " " + month + " " + year + " " + time;
        Log.d(TAG, "getFullDateStringPlWithTime: " + date);
        return date;
    }

    @Exclude
    public String getDateStringPlWithTime() {
        String[] separated = getDateSeparated();
        String dayName = separated[0];
        String day = separated[1];
        String month = separated[2];
        String year = separated[3];
        String time = separated[4];
        time = getTimeHourMin();

        if (day.startsWith("0")) {
            day = day.substring(1);
        }

        String date = day + " " + month + " " + year + " | " + time;
        Log.d(TAG, "getFullDateStringPlWithTime: " + date);
        return date;
    }

    @Exclude
    public String getDateStringPl() {
        String[] separated = getDateSeparated();
        String dayName = separated[0];
        String day = separated[1];
        String month = separated[2];
        String year = separated[3];
        String time = separated[4];
        time = getTimeHourMin();

        if (day.startsWith("0")) {
            day = day.substring(1);
        }

        String date = day + " " + month + " " + year;
        Log.d(TAG, "getDateStringPl: " + date);
        return date;
    }

    @Exclude
    public String getTimeHourMin() {
        String now = new SimpleDateFormat(DATE_FORMAT).format(getWorkoutDate());

        String[] separated = now.split(" ");
        String time = separated[4];

        String[] separatedTime = time.split(":");
        String hours = separatedTime[0];
        String mins = separatedTime[1];
        String secs = separatedTime[2];

        String timeHourMin = hours + ":" + mins;
        Log.d(TAG, "getTimeHourMin: " + timeHourMin);
        return timeHourMin;
    }

    //region Firebase getters

    public long getDateMilliseconds() {
        return dateMilliseconds;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public long getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public double getAvgPace() {
        return avgPace;
    }

    public double getMaxPace() {
        return maxPace;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getStatus() {
        return status;
    }

    public boolean getPrivacy() {
        return privacy;
    }

    public ArrayList<LatLon> getPath() {
        return path;
    }

    public ArrayList<Lap> getLaps() {
        return laps;
    }

    public WeatherInfoCompressed getWeatherInfoCompressed() {
        return weatherInfoCompressed;
    }

    //endregion

    @Exclude
    public Date getWorkoutDate() {
        return new Date(dateMilliseconds);
    }

    @Exclude
    public String getWorkoutKey() {
        return workoutKey;
    }

    @Exclude
    public String getDurationString() {
        return Utils.getDurationString(duration);
    }

    @Exclude
    public String getDistanceKmString() {
        return Utils.distanceMetersToKm(distance);
    }

    @Exclude
    public String getAvgPaceString() {
        return Utils.paceToString(avgPace);
    }

    @Exclude
    public String getMaxPaceString() {
        return Utils.paceToString(maxPace);
    }

    @Exclude
    public String getAvgSpeedString() {
        return String.valueOf(avgSpeed);
    }

    @Exclude
    public String getMaxSpeedString() {
        return String.valueOf(maxSpeed);
    }

    public void setWeatherInfoCompressed(WeatherInfoCompressed weatherInfoCompressed) {
        this.weatherInfoCompressed = weatherInfoCompressed;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrivacy(boolean sec) {
        this.privacy = sec;
    }

    public void setWorkoutKey(String workoutKey) {
        this.workoutKey = workoutKey;
    }

    @Override
    public String toString() {
        return "WorkoutSummary{" +
                "workoutKey='" + workoutKey + '\'' +
                ", dateMilliseconds=" + dateMilliseconds +
                ", workoutName='" + workoutName + '\'' +
                ", duration=" + duration +
                ", distance=" + distance +
                ", avgPace=" + avgPace +
                ", maxPace=" + maxPace +
                ", avgSpeed=" + avgSpeed +
                ", maxSpeed=" + maxSpeed +
                ", status='" + status + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", privacy=" + privacy +
                ", path=" + path +
                ", laps=" + laps +
                ", weatherInfoCompressed=" + weatherInfoCompressed +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("dateMilliseconds", dateMilliseconds);
        result.put("workoutName", workoutName);
        result.put("duration", duration);
        result.put("distance", distance);
        result.put("avgPace", avgPace);
        result.put("maxPace", maxPace);
        result.put("avgSpeed", avgSpeed);
        result.put("maxSpeed", maxSpeed);
        result.put("status", status);
        result.put("picUrl", picUrl);
        result.put("privacy", privacy);
        result.put("path", path);
        result.put("laps", laps);
        result.put("weatherInfoCompressed", weatherInfoCompressed);

        return result;
    }

    // Parcelling Part

    public WorkoutSummary(Parcel in) {
        // this.date = new Date(in.readLong());
        this.workoutKey = in.readString();
        this.dateMilliseconds = in.readLong();
        this.workoutName = in.readString();
        this.duration = in.readLong();
        this.distance = in.readDouble();
        this.avgPace = in.readDouble();
        this.maxPace = in.readDouble();
        this.avgSpeed = in.readDouble();
        this.maxSpeed = in.readDouble();
        this.status = in.readString();
        this.picUrl = in.readString();
        this.privacy = in.readByte() != 0;
        path = new ArrayList<>();
        in.readList(this.path, LatLon.class.getClassLoader());
        laps = new ArrayList<>();
        in.readList(this.laps, Lap.class.getClassLoader());
        this.weatherInfoCompressed = in.readParcelable(WeatherInfoCompressed.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dest.writeLong(this.date.getTime());
        dest.writeString(this.workoutKey);
        dest.writeLong(this.dateMilliseconds);
        dest.writeString(this.workoutName);
        dest.writeLong(this.duration);
        dest.writeDouble(this.distance);
        dest.writeDouble(this.avgPace);
        dest.writeDouble(this.maxPace);
        dest.writeDouble(this.avgSpeed);
        dest.writeDouble(this.maxSpeed);
        dest.writeString(this.status);
        dest.writeString(this.picUrl);
        dest.writeByte((byte) (privacy ? 1 : 0));
        dest.writeList(this.path);
        dest.writeList(this.laps);
        dest.writeParcelable(this.weatherInfoCompressed, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WorkoutSummary createFromParcel(Parcel in) {
            return new WorkoutSummary(in);
        }

        public WorkoutSummary[] newArray(int size) {
            return new WorkoutSummary[size];
        }
    };
}
