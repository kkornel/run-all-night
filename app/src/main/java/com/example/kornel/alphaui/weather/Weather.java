package com.example.kornel.alphaui.weather;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Weather implements LocationUtils.MyLocationResult {
    public enum ErrorType {
        CONNECTION_FAILED,
        NO_LOCATION_FOUND,
        PARSING_FAILED,
        NO_LOCATION_PERMISSION,
        UNKNOWN
    }

    public enum TEMP_UNIT {
        FAHRENHEIT,
        CELSIUS
    }

    public enum SPEED_UNIT {
        MPH,
        KMH
    }

    private static final String YQL_WEATHER_ENDPOINT_AUTHORITY = "query.yahooapis.com";
    private static final String YQL_WEATHER_ENDPOINT_PATH = "/v1/public/yql";

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5 * 1000;

    private static Weather mInstance = new Weather();

    private ErrorType mErrorType = null;


    private Context mContext;
    private WeatherInfoListener mWeatherInfoResult;
    private boolean mNeedDownloadIcons;

    private TEMP_UNIT mTempUnit = TEMP_UNIT.CELSIUS;
    private SPEED_UNIT mSpeedUnit = SPEED_UNIT.KMH;

    private int mConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;


    // INSTANCE

    public static Weather getInstance() {
        return mInstance;
    }

    public static Weather getInstance(boolean isDebuggable) {
        WeatherLog.setDebuggable(isDebuggable);
        return mInstance;
    }


    public void queryWeatherByLatLon(final Context context, final Double lat, final Double lon,
                                     final WeatherInfoListener result) {
        WeatherLog.d("query yahoo weather by lat lon");
        mContext = context;
        if (!NetworkUtils.isConnected(context)) {
            mErrorType = ErrorType.CONNECTION_FAILED;
            return;
        }
        mWeatherInfoResult = result;
        final WeatherQueryByLatLonTask task = new WeatherQueryByLatLonTask();
        task.execute(new Double[]{lat, lon});
    }

    public void queryWeatherByGPS(final Activity activity, final Context context,
                                  final WeatherInfoListener result) {
        WeatherLog.d("query yahoo weather by gps");
        if (!NetworkUtils.isConnected(context)) {
            mErrorType = ErrorType.CONNECTION_FAILED;
            return;
        }
        mContext = context;
        mWeatherInfoResult = result;
        (new LocationUtils()).findUserLocation(activity, context, this);
    }

    @Override
    public void gotLocation(Location location, LocationUtils.LocationErrorType errorType) {
        if (location == null) {
            if (errorType == LocationUtils.LocationErrorType.FIND_LOCATION_NOT_PERMITTED ||
                    errorType == LocationUtils.LocationErrorType.LOCATION_SERVICE_IS_NOT_AVAILABLE) {
                mErrorType = ErrorType.NO_LOCATION_PERMISSION;
            } else {
                mErrorType = ErrorType.NO_LOCATION_FOUND;
            }
            mWeatherInfoResult.gotWeatherInfo(null, mErrorType);
            return;
        }
        final Double lat = location.getLatitude();
        final Double lon = location.getLongitude();
        final WeatherQueryByLatLonTask task = new WeatherQueryByLatLonTask();
        task.execute(new Double[]{lat, lon});
    }

    public static int turnFtoC(int tempF) {
        return (int) ((tempF - 32) * 5.0f / 9);
    }

    public static int turnCtoF(int tempC) {
        return (int) (tempC * 9.0f / 5 + 32);
    }

    public static int turnMphtoKmh(int mph) {
        return (int) (mph * 1.609344);
    }

    public static int turnKmhtoMph(int kmh) {
        return (int) (kmh * 0.6213711922);
    }

    private String getWeatherJsonString(Context context, String placeName) {
        WeatherLog.d("query yahoo weather with placeName : " + placeName);

        String qResult = "";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority(YQL_WEATHER_ENDPOINT_AUTHORITY);
        builder.path(YQL_WEATHER_ENDPOINT_PATH);
        builder.appendQueryParameter("q", "select * from weather.forecast where woeid in " +
                "(select woeid from geo.places(1) where text=\"" +
                placeName +
                "\")");

        String queryUrl = builder.build().toString() + "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        // String queryUrl = builder.build().toString() + "&u=c&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        WeatherLog.d("query url : " + queryUrl);

        try {
            URL url = new URL(queryUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(mConnectionTimeout);
            urlConnection.connect();
            switch (urlConnection.getResponseCode()) {
                case HttpURLConnection.HTTP_BAD_GATEWAY:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_GATEWAY");
                case HttpURLConnection.HTTP_BAD_METHOD:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_METHOD");
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_REQUEST");
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_CLIENT_TIMEOUT");
                case HttpURLConnection.HTTP_CONFLICT:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_CONFLICT");
                case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_ENTITY_TOO_LARGE");
                case HttpURLConnection.HTTP_FORBIDDEN:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_FORBIDDEN");
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_GATEWAY_TIMEOUT");
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    mErrorType = ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_UNAVAILABLE");
                default:
                    break;
            }
            InputStream content = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String currLine = "";
            try {
                while ((currLine = buffer.readLine()) != null) {
                    qResult += currLine;
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            qResult = "";
            mErrorType = ErrorType.CONNECTION_FAILED;
        }
        WeatherLog.d("qResult: " + qResult);
        return qResult;
    }

    private JSONObject convertStringToJson(String src) {
        try {
            JSONObject obj = new JSONObject(src);
            return obj;
        } catch (JSONException e) {
            WeatherLog.printStack(e);
            mErrorType = ErrorType.PARSING_FAILED;
            return null;
        }
    }

    private WeatherInfo parseJsonToWeatherInfo(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        try {
            WeatherInfo weatherInfo = new WeatherInfo();

            JSONObject query = jsonObject.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");

            JSONObject channel = results.getJSONObject("channel");
            String lastBuildDate = channel.getString("lastBuildDate");
            weatherInfo.setLastBuildDate(lastBuildDate);

            JSONObject location = channel.getJSONObject("location");
            String city = location.getString("city");
            weatherInfo.setLocationCity(city);
            String region = location.getString("region");
            weatherInfo.setLocationRegion(region);
            String country = location.getString("country");
            weatherInfo.setLocationCountry(country);

            JSONObject wind = channel.getJSONObject("wind");
            String windSpeed = wind.getString("speed");
            weatherInfo.setWindSpeedMph(Integer.valueOf(windSpeed));
            weatherInfo.setWindSpeedKmh(turnMphtoKmh(Integer.valueOf(windSpeed)));

            JSONObject atmosphere = channel.getJSONObject("atmosphere");
            String humidity = atmosphere.getString("humidity");
            weatherInfo.setAtmosphereHumidity(Integer.valueOf(humidity));
            String pressure = atmosphere.getString("pressure");
            weatherInfo.setAtmospherePressure((Double.valueOf(pressure)).intValue());

            JSONObject item = channel.getJSONObject("item");
            String title = item.getString("title");
            weatherInfo.setConditionTitle(title);
            String pubDate = item.getString("pubDate");
            weatherInfo.setPubDate(pubDate);

            // Current
            JSONObject condition = item.getJSONObject("condition");
            String code = condition.getString("code");
            weatherInfo.setCurrentCode(Integer.valueOf(code));
            String date = condition.getString("date");
            weatherInfo.setCurrentConditionDate(date);
            String temp = condition.getString("temp");
            weatherInfo.setCurrentTempF(Integer.valueOf(temp));
            weatherInfo.setCurrentTempC(turnFtoC(Integer.valueOf(temp)));
            String text = condition.getString("text");
            weatherInfo.setCurrentText(text);

            // For today
            JSONArray forecast = item.getJSONArray("forecast");
            JSONObject today = forecast.getJSONObject(0);
            String todayCode = today.getString("code");
            weatherInfo.setTodayCode(Integer.valueOf(todayCode));
            String todayDate = today.getString("date");
            weatherInfo.setTodayConditionDate(todayDate);
            String todayDay = today.getString("day");
            weatherInfo.setTodayDay(todayDay);
            String todayHigh = today.getString("high");
            weatherInfo.setTodayHighF(Integer.valueOf(todayHigh));
            weatherInfo.setTodayHighC(turnFtoC(Integer.valueOf(todayHigh)));
            String todayLow = today.getString("low");
            weatherInfo.setTodayLowF(Integer.valueOf(todayLow));
            weatherInfo.setTodayLowC(turnFtoC(Integer.valueOf(todayLow)));
            String todayText = today.getString("text");
            weatherInfo.setTodayText(todayText);

            String description = item.getString("description");
            weatherInfo.setDescription(description);

            WeatherLog.d(weatherInfo.toString());

            return weatherInfo;
        } catch (JSONException e) {
            WeatherLog.printStack(e);
            mErrorType = ErrorType.PARSING_FAILED;
            return null;
        }
    }

    public static String addressToPlaceName(final Address address) {
        String result = "";
        if (address.getLocality() != null) {
            result += address.getLocality();
            result += " ";
        }
        if (address.getAdminArea() != null) {
            result += address.getAdminArea();
            result += " ";
        }
        if (address.getCountryName() != null) {
            result += address.getCountryName();
            result += " ";
        }
        return result;
    }

    public void setTempUnit(TEMP_UNIT tempUnit) {
        mTempUnit = tempUnit;
    }

    public TEMP_UNIT getTempUnit() {
        return mTempUnit;
    }

    public void setSpeedUnit(SPEED_UNIT speedUnit) {
        mSpeedUnit = speedUnit;
    }

    public SPEED_UNIT getSpeedUnit() {
        return mSpeedUnit;
    }

    public void setNeedDownloadIcons(final boolean needDownloadIcons) {
        mNeedDownloadIcons = needDownloadIcons;
    }

    private class WeatherQueryByLatLonTask extends AsyncTask<Double, Void, WeatherInfo> {

        @Override
        protected WeatherInfo doInBackground(Double... params) {
            if (params == null || params.length != 2) {
                throw new IllegalArgumentException("Parameter of WeatherQueryByLatLonTask is illegal."
                        + "No Lat Lon exists.");
            }
            final Double lat = params[0];
            final Double lon = params[1];
            // Get city name or place name
            if (mContext != null) {
                final Geocoder geocoder = new Geocoder(mContext);
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat.doubleValue(), lon.doubleValue(), 1);
                    for (Address address : addresses) {
                        WeatherLog.d("latlon : " + lat + ", " + lon);
                        int n = address.getMaxAddressLineIndex();
                        for (int i = 0; i < n; i++) {
                            WeatherLog.d("address line : " + address.getAddressLine(i));
                        }

                        WeatherLog.d("adminarea : " + address.getAdminArea());
                        WeatherLog.d("subAdminArea : " + address.getSubAdminArea());
                        WeatherLog.d("countryName : " + address.getCountryName());
                        WeatherLog.d("feature name : " + address.getFeatureName());
                        WeatherLog.d("locality : " + address.getLocality());
                        WeatherLog.d("sublocality : " + address.getSubLocality());
                        WeatherLog.d("postCode : " + address.getPostalCode());
                        WeatherLog.d("premises : " + address.getPremises());
                        WeatherLog.d("thoroughfare : " + address.getThoroughfare());

                        String weatherJsonString = getWeatherJsonString(mContext, addressToPlaceName(address));
                        JSONObject weatherJsonObject = convertStringToJson(weatherJsonString);
                        WeatherInfo weatherInfo = parseJsonToWeatherInfo(weatherJsonObject);

                        if (weatherInfo != null) weatherInfo.setAddress(address);
                        return weatherInfo;
                    }

                } catch (IOException e) {
                    WeatherLog.printStack(e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(WeatherInfo result) {
            super.onPostExecute(result);
            if (result == null && mErrorType == null) mErrorType = ErrorType.UNKNOWN;
            mWeatherInfoResult.gotWeatherInfo(result, mErrorType);
            mContext = null;
        }
    }
}
