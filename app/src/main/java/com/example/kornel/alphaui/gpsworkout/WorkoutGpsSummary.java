package com.example.kornel.alphaui.gpsworkout;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.kornel.alphaui.mainactivity.MainActivityLog;
import com.example.kornel.alphaui.utils.DateUtils;
import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutGpsSummary implements Parcelable {
    private static final String TAG = "WorkoutGpsSummary";

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

    private String dateString;
    private String workoutName;
    private String duration;
    private double distance;
    private double speed;
    private String pace;
    private ArrayList<LatLon> path;
    private ArrayList<Lap> laps;

    public WorkoutGpsSummary() {
        // Default constructor required for calls to DataSnapshot.getValue(WorkoutGpsSummary.class)
    }

    public WorkoutGpsSummary(String workoutName, String duration, double distance, String pace, double speed, ArrayList<LatLon> path, ArrayList<Lap> laps) {
        this.dateString = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        this.workoutName = workoutName;
        this.duration = duration;
        this.distance = distance;
        this.path = path;
        this.speed = speed;
        this.pace = pace;
        this.laps = laps;
    }

    @Exclude
    public String getDateStringPlWithTime() {
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

            MainActivityLog.d(elapsedDays + "days, " + elapsedHours + " hours, " + elapsedMinutes + " minutes, " +  elapsedSeconds + "seconds");

            if (elapsedDays > 0) {
                if (elapsedDays == 1) {
                    return "1 dzień temu";
                } else {
                    return String.valueOf(elapsedDays) + " dni temu";
                }
            } else {
                if (elapsedHours < 1) {
                    return String.valueOf(elapsedMinutes) + " minut temu";
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

    public String getWorkoutName() {
        return workoutName;
    }

    public String getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public String getPace() {
        return pace;
    }

    public ArrayList<LatLon> getPath() {
        return path;
    }

    public ArrayList<Lap> getLaps() {
        return laps;
    }

    @Override
    public String toString() {
        return "WorkoutGpsSummary{" +
                "dateString='" + dateString + '\'' +
                ", workoutName='" + workoutName + '\'' +
                ", duration='" + duration + '\'' +
                ", distance=" + distance +
                ", speed=" + speed +
                ", pace=" + pace +
                ", path=" + path +
                '}';
    }

    // Parcelable Part

    public WorkoutGpsSummary(Parcel in){
        // this.date = new Date(in.readLong());
        this.dateString = in.readString();
        this.workoutName = in.readString();
        this.duration = in.readString();
        this.distance = in.readDouble();
        this.pace = in.readString();
        this.speed = in.readDouble();
        path = new ArrayList<>();
        in.readList(this.path, LatLon.class.getClassLoader());
        laps = new ArrayList<>();
        in.readList(this.laps, Lap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dest.writeLong(this.date.getTime());
        dest.writeString(this.dateString);
        dest.writeString(this.workoutName);
        dest.writeString(this.duration);
        dest.writeDouble(this.distance);
        dest.writeString(this.pace);
        dest.writeDouble(this.speed);
        dest.writeList(this.path);
        dest.writeList(this.laps);
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
