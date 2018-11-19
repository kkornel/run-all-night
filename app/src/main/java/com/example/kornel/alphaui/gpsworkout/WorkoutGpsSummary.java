package com.example.kornel.alphaui.gpsworkout;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.kornel.alphaui.mainactivity.MainActivityLog;
import com.example.kornel.alphaui.utils.DateUtils;
import com.example.kornel.alphaui.utils.LatLon;
import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WorkoutGpsSummary implements Parcelable {
    private static final String TAG = "WorkoutGpsSummary";

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";
    // private SimpleDateFormat sdf;

    private String dateString;
    private String workoutName;
    private String duration;
    private double distance;
    private ArrayList<LatLon> path;

    public WorkoutGpsSummary() {
        // Default constructor required for calls to DataSnapshot.getValue(WorkoutGpsSummary.class)
        // this.sdf = new SimpleDateFormat(DATE_FORMAT);
        gapBetweenWorkouts();
    }

    public WorkoutGpsSummary(String workoutName, String duration, double distance, ArrayList<LatLon> path) {
        // this();
        gapBetweenWorkouts();
        this.dateString = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        // DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        // String date =
        //         null;
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        //     date = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        //             .withLocale(new Locale("pl"))
        //             .format(new Date().toInstant());
        // } else {
        //     date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        // }
        this.workoutName = workoutName;
        this.duration = duration;
        this.distance = distance;
        this.path = path;
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

    public void gapBetweenWorkouts() {
        try {
            Date endDate = new SimpleDateFormat(DATE_FORMAT).parse("Mon, 19 Nov 2018 12:14:11");
            Date startDate = new SimpleDateFormat(DATE_FORMAT).parse("Sun, 18 Nov 2018 05:24:51");
            long different = endDate.getTime() - startDate.getTime();

            MainActivityLog.d("startDate : " + startDate);
            MainActivityLog.d("endDate : "+ endDate);
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
        } catch (ParseException e) {
            e.printStackTrace();
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

    public ArrayList<LatLon> getPath() {
        return path;
    }

    // Parcelable Part

    public WorkoutGpsSummary(Parcel in){
        // this.date = new Date(in.readLong());
        this.dateString = in.readString();
        this.workoutName = in.readString();
        this.duration = in.readString();
        this.distance = in.readDouble();
        path = new ArrayList<>();
        in.readList(this.path, LatLon.class.getClassLoader());
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
        dest.writeList(this.path);
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
