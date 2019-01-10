package com.example.kornel.alphaui.weather;

import android.graphics.Bitmap;
import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.kornel.alphaui.utils.DateUtils;

public class WeatherInfo implements Parcelable {
    public static final String FAHRENHEIT = "\u00b0F";
    public static final String CELSIUS = "\u00b0C";
    public static final String MPH = " mph";
    public static final String KMH = " km/h";
    public static final String PERCENT = "%";
    public static final String HECTOPASCAL = " hPa";

    /*
     * information in object "channel"
     */
    private String mLastBuildDate = "";
    /*
     * information in object "location"
     */
    private String mLocationCity = "";
    private String mLocationRegion = "";
    private String mLocationCountry = "";
    /*
     * information in object "wind"
     */
    private int mWindSpeedMph = 0;
    private int mWindSpeedKmh = 0;
    /*
     * information in object "atmosphere"
     */
    private int mAtmosphereHumidity = 0;
    private int mAtmospherePressure = 0;
    /*
     * information in object "item"
     */
    private String mConditionTitle = "";
    private String mPubDate = "";
    /*
     * information in object "condition"
     */
    private int mCurrentCode = 0;
    private String mCurrentConditionDate = "";
    private int mCurrentTempC = 0;
    private int mCurrentTempF = 0;
    private String mCurrentText = "";
    private String mCurrentConditionIconURL = "";
    private Bitmap mCurrentConditionIcon = null;
    /*
     * information in the first object "forecast[0] - today"
     */
    private int mTodayCode = 0;
    private String mTodayConditionDate = "";
    private String mTodayDay = "";
    private int mTodayHighF = 0;
    private int mTodayHighC = 0;
    private int mTodayLowF = 0;
    private int mTodayLowC = 0;
    private String mTodayText = "";
    private String mTodayConditionIconURL = "";
    private Bitmap mTodayConditionIcon = null;

    private String mDescription;

    private Address mAddress = null;

    public WeatherInfo() {

    }

    public String getTimeFormatted() {
        String[] splitted = mCurrentConditionDate.split(" ");
        String time = splitted[4];
        String hour = time.split(":")[0];
        String min = time.split(":")[1];
        String ampm = splitted[5];
        if (ampm.equals("PM")) {
            hour = DateUtils.convertPmTime(hour);
        }
        return hour + ":" + min;
    }

    public String getCurrentConditionDatePl() {
        // 'Sun, 18 Nov 2018 05:00 AM CET'
        String[] separated = mCurrentConditionDate.split(" ");
        // 'Sun,'
        String dayName = separated[0].substring(0, separated[0].length() - 1);
        dayName = DateUtils.convertDayName(dayName);
        String day = separated[1];
        String month = separated[2];
        month = DateUtils.convertMonthToNumber(month);
        String year = separated[3];

        String pl = dayName + ", " + day + "." + month + "." + year;

        return pl;
    }

    protected void setCurrentCode(int currentCode) {
        mCurrentCode = currentCode;
        mCurrentConditionIconURL = "http://l.yimg.com/a/i/us/we/52/" + currentCode + ".gif";
    }

    protected void setTodayCode(int forecastCode) {
        mTodayCode = forecastCode;
        mTodayConditionIconURL = "http://l.yimg.com/a/i/us/we/52/" + forecastCode + ".gif";
    }

    public String getLastBuildDate() {
        return mLastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        mLastBuildDate = lastBuildDate;
    }

    public String getLocationCity() {
        return mLocationCity;
    }

    public void setLocationCity(String locationCity) {
        mLocationCity = locationCity;
    }

    public String getLocationRegion() {
        return mLocationRegion;
    }

    public void setLocationRegion(String locationRegion) {
        mLocationRegion = locationRegion;
    }

    public String getLocationCountry() {
        return mLocationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        mLocationCountry = locationCountry;
    }

    public int getWindSpeedMph() {
        return mWindSpeedMph;
    }

    public void setWindSpeedMph(int windSpeedMph) {
        mWindSpeedMph = windSpeedMph;
    }

    public int getWindSpeedKmh() {
        return mWindSpeedKmh;
    }

    public void setWindSpeedKmh(int windSpeedKmh) {
        mWindSpeedKmh = windSpeedKmh;
    }

    public int getAtmosphereHumidity() {
        return mAtmosphereHumidity;
    }

    public void setAtmosphereHumidity(int atmosphereHumidity) {
        mAtmosphereHumidity = atmosphereHumidity;
    }

    public int getAtmospherePressure() {
        return mAtmospherePressure;
    }

    public void setAtmospherePressure(int atmospherePressure) {
        mAtmospherePressure = atmospherePressure;
    }

    public String getConditionTitle() {
        return mConditionTitle;
    }

    public void setConditionTitle(String conditionTitle) {
        mConditionTitle = conditionTitle;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public void setPubDate(String pubDate) {
        mPubDate = pubDate;
    }

    public int getCurrentCode() {
        return mCurrentCode;
    }

    public String getCurrentConditionDate() {
        return mCurrentConditionDate;
    }

    public void setCurrentConditionDate(String currentConditionDate) {
        mCurrentConditionDate = currentConditionDate;
    }

    public int getCurrentTempF() {
        return mCurrentTempF;
    }

    public void setCurrentTempF(int currentTempF) {
        mCurrentTempF = currentTempF;
    }

    public int getCurrentTempC() {
        return mCurrentTempC;
    }

    public void setCurrentTempC(int currentTempC) {
        mCurrentTempC = currentTempC;
    }

    public String getCurrentText() {
        return mCurrentText;
    }

    public void setCurrentText(String currentText) {
        mCurrentText = currentText;
    }

    public String getCurrentConditionIconURL() {
        return mCurrentConditionIconURL;
    }

    public void setCurrentConditionIconURL(String currentConditionIconURL) {
        mCurrentConditionIconURL = currentConditionIconURL;
    }

    public Bitmap getCurrentConditionIcon() {
        return mCurrentConditionIcon;
    }

    public void setCurrentConditionIcon(Bitmap currentConditionIcon) {
        mCurrentConditionIcon = currentConditionIcon;
    }

    public int getTodayCode() {
        return mTodayCode;
    }

    public String getTodayConditionDate() {
        return mTodayConditionDate;
    }

    public void setTodayConditionDate(String todayConditionDate) {
        mTodayConditionDate = todayConditionDate;
    }

    public String getTodayDay() {
        return mTodayDay;
    }

    public void setTodayDay(String todayDay) {
        mTodayDay = todayDay;
    }

    public int getTodayHighF() {
        return mTodayHighF;
    }

    public void setTodayHighF(int todayHighF) {
        mTodayHighF = todayHighF;
    }

    public int getTodayLowF() {
        return mTodayLowF;
    }

    public void setTodayLowF(int todayLowF) {
        mTodayLowF = todayLowF;
    }

    public int getTodayHighC() {
        return mTodayHighC;
    }

    public void setTodayHighC(int todayHighC) {
        mTodayHighC = todayHighC;
    }

    public int getTodayLowC() {
        return mTodayLowC;
    }

    public void setTodayLowC(int todayLowC) {
        mTodayLowC = todayLowC;
    }

    public String getTodayText() {
        return mTodayText;
    }

    public void setTodayText(String todayText) {
        mTodayText = todayText;
    }

    public String getTodayConditionIconURL() {
        return mTodayConditionIconURL;
    }

    public void setTodayConditionIconURL(String todayConditionIconURL) {
        mTodayConditionIconURL = todayConditionIconURL;
    }

    public Bitmap getTodayConditionIcon() {
        return mTodayConditionIcon;
    }

    public void setTodayConditionIcon(Bitmap todayConditionIcon) {
        mTodayConditionIcon = todayConditionIcon;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Address getAddress() {
        return mAddress;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    // Parcelling part
    public WeatherInfo(Parcel in) {
        mLastBuildDate = in.readString();
        mLocationCity = in.readString();
        mLocationRegion = in.readString();
        mLocationCountry = in.readString();
        mWindSpeedMph = in.readInt();
        mWindSpeedKmh = in.readInt();
        mAtmosphereHumidity = in.readInt();
        mAtmospherePressure = in.readInt();
        mConditionTitle = in.readString();
        mPubDate = in.readString();
        mCurrentCode = in.readInt();
        mCurrentConditionDate = in.readString();
        mAtmospherePressure = in.readInt();
        mCurrentText = in.readString();
        mCurrentConditionIconURL = in.readString();
        mCurrentConditionIcon = in.readParcelable(Bitmap.class.getClassLoader());
        mTodayCode = in.readInt();
        mTodayConditionDate = in.readString();
        mTodayDay = in.readString();
        mTodayHighF = in.readInt();
        mTodayLowF = in.readInt();
        mTodayHighC = in.readInt();
        mTodayLowC = in.readInt();
        mTodayText = in.readString();
        mTodayConditionIconURL = in.readString();
        mTodayConditionIcon = in.readParcelable(Bitmap.class.getClassLoader());
        mDescription = in.readString();
        mAddress = in.readParcelable(Address.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLastBuildDate);
        dest.writeString(mLocationCity);
        dest.writeString(mLocationRegion);
        dest.writeString(mLocationCountry);
        dest.writeInt(mWindSpeedMph);
        dest.writeInt(mWindSpeedKmh);
        dest.writeInt(mAtmosphereHumidity);
        dest.writeInt(mAtmospherePressure);
        dest.writeString(mConditionTitle);
        dest.writeString(mPubDate);
        dest.writeInt(mCurrentCode);
        dest.writeString(mCurrentConditionDate);
        dest.writeInt(mAtmospherePressure);
        dest.writeString(mCurrentText);
        dest.writeString(mCurrentConditionIconURL);
        dest.writeParcelable(mCurrentConditionIcon, flags);
        dest.writeInt(mTodayCode);
        dest.writeString(mTodayConditionDate);
        dest.writeString(mTodayDay);
        dest.writeInt(mTodayHighF);
        dest.writeInt(mTodayLowF);
        dest.writeInt(mTodayHighC);
        dest.writeInt(mTodayLowC);
        dest.writeString(mTodayText);
        dest.writeString(mTodayConditionIconURL);
        dest.writeParcelable(mTodayConditionIcon, flags);
        dest.writeString(mDescription);
        dest.writeParcelable(mAddress, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WeatherInfo createFromParcel(Parcel in) {
            return new WeatherInfo(in);
        }

        public WeatherInfo[] newArray(int size) {
            return new WeatherInfo[size];
        }
    };

    @Override
    public String toString() {
        return "WeatherInfo{\n" +
                "mLastBuildDate='" + mLastBuildDate + "\n" +
                ", mLocationCity='" + mLocationCity + "\n" +
                ", mLocationRegion='" + mLocationRegion + "\n" +
                ", mLocationCountry='" + mLocationCountry + "\n" +
                ", mWindSpeedMph=" + mWindSpeedMph + "\n" +
                ", mWindSpeedKmh=" + mWindSpeedKmh + "\n" +
                ", mAtmosphereHumidity=" + mAtmosphereHumidity + "\n" +
                ", mAtmospherePressure=" + mAtmospherePressure + "\n" +
                ", mConditionTitle='" + mConditionTitle + "\n" +
                ", mPubDate='" + mPubDate + "\n" +
                ", mCurrentCode=" + mCurrentCode + "\n" +
                ", mCurrentConditionDate='" + mCurrentConditionDate + "\n" +
                ", mCurrentTempF=" + mCurrentTempF + "\n" +
                ", mCurrentTempC=" + mCurrentTempC + "\n" +
                ", mCurrentText='" + mCurrentText + "\n" +
                ", mCurrentConditionIconURL='" + mCurrentConditionIconURL + "\n" +
                ", mCurrentConditionIcon=" + mCurrentConditionIcon + "\n" +
                ", mTodayCode=" + mTodayCode + "\n" +
                ", mTodayConditionDate='" + mTodayConditionDate + "\n" +
                ", mTodayDay='" + mTodayDay + "\n" +
                ", mTodayHighF=" + mTodayHighF + "\n" +
                ", mTodayLowF=" + mTodayLowF + "\n" +
                ", mTodayHighC=" + mTodayHighC + "\n" +
                ", mTodayLowC=" + mTodayLowC + "\n" +
                ", mTodayText='" + mTodayText + "\n" +
                ", mTodayConditionIconURL='" + mTodayConditionIconURL + "\n" +
                ", mTodayConditionIcon=" + mTodayConditionIcon + "\n" +
                ", mDescription='" + mDescription + "\n" +
                ", mAddress=" + mAddress +
                '}';
    }
}