package com.example.kornel.alphaui.gpsworkout;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.kornel.alphaui.mainactivity.MainActivityLog;
import com.example.kornel.alphaui.utils.DateUtils;
import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.weather.WeatherInfoCompressed;
import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkoutGpsSummary implements Parcelable {
    private static final String TAG = "WorkoutGpsSummary";

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

    private String key;

    private String dateString;
    private String workoutName;
    private String duration;
    private String distance;
    private String avgPace;
    private String maxPace;
    private String avgSpeed;
    private String maxSpeed;
    private String status;
    private String picUrl;
    private String privacy;
    private boolean isPrivate;
    private ArrayList<LatLon> path;
    private ArrayList<Lap> laps;
    private WeatherInfoCompressed weatherInfoCompressed;

    public WorkoutGpsSummary() {
        // Default constructor required for calls to DataSnapshot.getValue(WorkoutGpsSummary.class)
    }

    public WorkoutGpsSummary(String workoutName, String duration, String distance, String avgPace, String maxPace, String avgSpeed, String maxSpeed, ArrayList<LatLon> path, ArrayList<Lap> laps) {
        this.dateString = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        this.workoutName = workoutName;
        this.duration = duration;
        this.distance = distance;
        this.avgPace = avgPace;
        this.maxPace = maxPace;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.path = path;
        this.laps = laps;
        // this.picUrl = null;
        // this.status = null;
        // this.getIsPrivate = false;
        // weatherInfoCompressed = null;
    }

    public WorkoutGpsSummary(String dateString, String workoutName, String duration, String distance, String avgPace, String maxPace, String avgSpeed, String maxSpeed, String status, String picUrl, boolean aIsPrivate, ArrayList<LatLon> path, ArrayList<Lap> laps, WeatherInfoCompressed weatherInfoCompressed) {
        this.dateString = dateString;
        this.workoutName = workoutName;
        this.duration = duration;
        this.distance = distance;
        this.avgPace = avgPace;
        this.maxPace = maxPace;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.status = status;
        this.picUrl = picUrl;
        // this.isPrivate = aIsPrivate;
        this.isPrivate = aIsPrivate;
        this.path = path;
        this.laps = laps;
        this.weatherInfoCompressed = weatherInfoCompressed;
    }

    @Exclude
    public String getFullDateStringPlWithTime() {
        String now = new SimpleDateFormat(DATE_FORMAT).format(getDate());
        String[] separated = now.split(" ");
        String dayName = separated[0].substring(0, separated[0].length() - 1);
        dayName = DateUtils.convertDayName(dayName);
        String day = separated[1];
        String month = separated[2];
        month = DateUtils.convertMonthToFullName(month);
        String year = separated[3];
        String time = separated[4];
        return dayName + ", " + day + " " + month + " " + year + " " + time;
    }

    @Exclude
    public String getDateStringPlWithTime() {
        String now = new SimpleDateFormat(DATE_FORMAT).format(getDate());
        String[] separated = now.split(" ");
        String day = separated[1];
        String month = separated[2];
        month = DateUtils.convertMonthToFullName(month);
        String year = separated[3];
        String time = separated[4];
        return day + " " + month + " " + year + " | " + time;
    }

    @Exclude
    public String getDateStringPl() {
        String now = new SimpleDateFormat(DATE_FORMAT).format(getDate());
        String[] separated = now.split(" ");
        String dayName = separated[0].substring(0, separated[0].length() - 1);
        dayName = DateUtils.convertDayName(dayName);
        String day = separated[1];
        String month = separated[2];
        month = DateUtils.convertMonthToNumber(month);
        String year = separated[3];
        return dayName + ", " + day + "." + month + "." + year;
    }

    public String gapBetweenWorkouts() {
        try {
            Date todayDate = new Date();
            Date lastWorkoutDate = new SimpleDateFormat(DATE_FORMAT).parse(dateString);
            long different = todayDate.getTime() - lastWorkoutDate.getTime();

            MainActivityLog.d("todayDate : " + todayDate);
            MainActivityLog.d("lastWorkoutDate : "+ lastWorkoutDate);
            MainActivityLog.d("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            MainActivityLog.d(elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes + " minutes, " +  elapsedSeconds + " seconds");

            if (elapsedDays > 0) {
                if (elapsedDays == 1) {
                    return "1 dzień temu";
                } else {
                    return String.valueOf(elapsedDays) + " dni temu";
                }
            } else {
                if (elapsedHours < 1) {
                    if (elapsedMinutes < 5) {
                        return "Chwilę temu";
                    } else {
                        return String.valueOf(elapsedMinutes) + " minut temu";
                    }
                } else if (elapsedHours == 1) {
                    return "1 godzinę temu";
                } else {
                    return String.valueOf(elapsedHours) + " godzin temu";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return getDateStringPl();
        }
    }

    public String getDateString() {
        return dateString;
    }

    @Exclude
    public Date getDate() {
        try {
            Date date = new SimpleDateFormat(DATE_FORMAT).parse(this.dateString);
            return date;
        } catch (ParseException ex) {
            Log.e(TAG, "getDate: " + ex.getMessage());
            return new Date();
        }
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getDuration() {
        return duration;
    }

    public String getDistance() {
        return distance;
    }

    public String getAvgPace() {
        return avgPace;
    }

    public String getMaxPace() {
        return maxPace;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getStatus() {
        return status;
    }

    @Exclude
    public boolean getIsPrivate() {
        return isPrivate;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String p) {
        this.privacy = p;
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

    public void setWeatherInfoCompressed(WeatherInfoCompressed weatherInfoCompressed) {
        this.weatherInfoCompressed = weatherInfoCompressed;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrivate(boolean aPrivate) {
        this.isPrivate = aPrivate;
    }

    @Override
    public String toString() {
        return "WorkoutGpsSummary{" +
                "dateString='" + dateString + '\'' +
                ", workoutName='" + workoutName + '\'' +
                ", duration='" + duration + '\'' +
                ", distance='" + distance + '\'' +
                ", avgPace='" + avgPace + '\'' +
                ", maxPace='" + maxPace + '\'' +
                ", avgSpeed='" + avgSpeed + '\'' +
                ", maxSpeed='" + maxSpeed + '\'' +
                ", status='" + status + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", getIsPrivate=" + isPrivate +
                ", path=" + path +
                ", laps=" + laps +
                ", weatherInfoCompressed=" + weatherInfoCompressed +
                '}';
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("dateString", dateString);
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

    public WorkoutGpsSummary(Parcel in){
        // this.date = new Date(in.readLong());
        this.key = in.readString();
        this.dateString = in.readString();
        this.workoutName = in.readString();
        this.duration = in.readString();
        this.distance = in.readString();
        this.avgPace = in.readString();
        this.maxPace = in.readString();
        this.avgSpeed = in.readString();
        this.maxSpeed = in.readString();
        this.status = in.readString();
        this.picUrl = in.readString();
        this.privacy = in.readString();
        // this.isPrivate = in.readByte() != 0;
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
        dest.writeString(this.key);
        dest.writeString(this.dateString);
        dest.writeString(this.workoutName);
        dest.writeString(this.duration);
        dest.writeString(this.distance);
        dest.writeString(this.avgPace);
        dest.writeString(this.maxPace);
        dest.writeString(this.avgSpeed);
        dest.writeString(this.maxSpeed);
        dest.writeString(this.status);
        dest.writeString(this.picUrl);
        dest.writeString(this.privacy);
        // dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeList(this.path);
        dest.writeList(this.laps);
        dest.writeParcelable(this.weatherInfoCompressed, flags);
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
