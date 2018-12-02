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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkoutGpsSummary implements Parcelable {
    private static final String TAG = "WorkoutGpsSummary";

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

    private String key;

    // private String dateString;
    private long dateMilliseconds;
    private String workoutName;
    private String duration;
    private String distance;
    private String avgPace;
    private String maxPace;
    private String avgSpeed;
    private String maxSpeed;
    private String status;
    private String picUrl;
    private boolean privacy;
    private ArrayList<LatLon> path;
    private ArrayList<Lap> laps;
    private WeatherInfoCompressed weatherInfoCompressed;

    public WorkoutGpsSummary() {
        // Default constructor required for calls to DataSnapshot.getValue(WorkoutGpsSummary.class)
    }

    public WorkoutGpsSummary(String workoutName, String duration, String distance, String avgPace, String maxPace, String avgSpeed, String maxSpeed, ArrayList<LatLon> path, ArrayList<Lap> laps) {
        this.dateMilliseconds = new Date().getTime();
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

        return new String[] {dayName, day, month, year, time};
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

    public String gapBetweenWorkouts() {
        Date todayDate = new Date();
        Date workoutDate = getWorkoutDate();
        long different = todayDate.getTime() - workoutDate.getTime();

        MainActivityLog.d("todayDate : " + todayDate);
        MainActivityLog.d("workoutDate : " + workoutDate);
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

        MainActivityLog.d(elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes + " minutes, " + elapsedSeconds + " seconds");

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
    }

    //region Firebase getters

    public long getDateMilliseconds() {
        return dateMilliseconds;
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
    public String getKey() {
        return key;
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

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "WorkoutGpsSummary{" +
                "dateMilliseconds='" + dateMilliseconds + '\'' +
                ", workoutName='" + workoutName + '\'' +
                ", duration='" + duration + '\'' +
                ", distance='" + distance + '\'' +
                ", avgPace='" + avgPace + '\'' +
                ", maxPace='" + maxPace + '\'' +
                ", avgSpeed='" + avgSpeed + '\'' +
                ", maxSpeed='" + maxSpeed + '\'' +
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

    public WorkoutGpsSummary(Parcel in) {
        // this.date = new Date(in.readLong());
        this.key = in.readString();
        this.dateMilliseconds = in.readLong();
        this.workoutName = in.readString();
        this.duration = in.readString();
        this.distance = in.readString();
        this.avgPace = in.readString();
        this.maxPace = in.readString();
        this.avgSpeed = in.readString();
        this.maxSpeed = in.readString();
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
        dest.writeString(this.key);
        dest.writeLong(this.dateMilliseconds);
        dest.writeString(this.workoutName);
        dest.writeString(this.duration);
        dest.writeString(this.distance);
        dest.writeString(this.avgPace);
        dest.writeString(this.maxPace);
        dest.writeString(this.avgSpeed);
        dest.writeString(this.maxSpeed);
        dest.writeString(this.status);
        dest.writeString(this.picUrl);
        dest.writeByte((byte) (privacy ? 1 : 0));
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
