package com.example.kornel.alphaui.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherInfoCompressed implements Parcelable {
    private int code = 0;
    private int tempC = 0;
    private String conditionIconURL = "";

    public WeatherInfoCompressed() {

    }

    public WeatherInfoCompressed(int code, int tempC, String conditionIconURL) {
        this.code = code;
        this.tempC = tempC;
        this.conditionIconURL = conditionIconURL;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTempC() {
        return tempC;
    }

    public void setTempC(int tempC) {
        this.tempC = tempC;
    }

    public String getConditionIconURL() {
        return conditionIconURL;
    }

    public void setConditionIconURL(String conditionIconURL) {
        this.conditionIconURL = conditionIconURL;
    }

    @Override
    public String toString() {
        return "WeatherInfoCompressed{" +
                "code=" + code +
                ", tempC=" + tempC +
                ", conditionIconURL='" + conditionIconURL + '\'' +
                '}';
    }

    // Parcelling part

    public WeatherInfoCompressed(Parcel in) {
        this.code = in.readInt();
        this.tempC = in.readInt();
        this.conditionIconURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeInt(this.tempC);
        dest.writeString(this.conditionIconURL);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WeatherInfoCompressed createFromParcel(Parcel in) {
            return new WeatherInfoCompressed(in);
        }

        public WeatherInfoCompressed[] newArray(int size) {
            return new WeatherInfoCompressed[size];
        }
    };
}
