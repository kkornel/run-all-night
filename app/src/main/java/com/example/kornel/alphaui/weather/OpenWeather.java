package com.example.kornel.alphaui.weather;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

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

public class OpenWeather implements LocationUtils.MyLocationResult {
    public enum ErrorType {
        CONNECTION_FAILED,
        NO_LOCATION_FOUND,
        PARSING_FAILED,
        NO_LOCATION_PERMISSION,
        UNKNOWN
    }

    private static final String API_KEY = "bd74ab823f6c099bf680cfab73897589";

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5 * 1000;
    private int mConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    private static OpenWeather mInstance = new OpenWeather();

    private OpenWeather.ErrorType mErrorType = null;

    private Context mContext;
    private WeatherInfoListener mWeatherInfoResult;

    public static WeatherInfo sWeatherInfo;

    // INSTANCE

    public static OpenWeather getInstance() {
        return mInstance;
    }

    public static OpenWeather getInstance(boolean isDebuggable) {
        WeatherLog.setDebuggable(isDebuggable);
        return mInstance;
    }

    public void queryWeatherByGPS(final Activity activity, final Context context, final WeatherInfoListener result) {
        if (!NetworkUtils.isConnected(context)) {
            mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
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
                mErrorType = OpenWeather.ErrorType.NO_LOCATION_PERMISSION;
            } else {
                mErrorType = OpenWeather.ErrorType.NO_LOCATION_FOUND;
            }
            mWeatherInfoResult.gotWeatherInfo(null, mErrorType);
            return;
        }
        final Double lat = location.getLatitude();
        final Double lon = location.getLongitude();
        final WeatherQueryByLatLonTask task = new WeatherQueryByLatLonTask();
        task.execute(new Double[]{lat, lon});
    }

    private double turnMpsIntoKmPerHours(double mps) {
        return mps * 3.6;
    }

    private String getWeatherJsonString(Context context, double lat, double lon) {
        String queryUrl = "http://api.openweathermap.org/data/2.5/forecast?" +
                "lat=" + lat +
                "&lon=" + lon +
                "&appid=" + API_KEY +
                "&units=metric";

        String qResult = "";
        WeatherLog.d("query url : " + queryUrl);

        try {
            URL url = new URL(queryUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(mConnectionTimeout);
            urlConnection.connect();
            switch (urlConnection.getResponseCode()) {
                case HttpURLConnection.HTTP_BAD_GATEWAY:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_GATEWAY");
                case HttpURLConnection.HTTP_BAD_METHOD:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_METHOD");
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_REQUEST");
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_CLIENT_TIMEOUT");
                case HttpURLConnection.HTTP_CONFLICT:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_CONFLICT");
                case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_ENTITY_TOO_LARGE");
                case HttpURLConnection.HTTP_FORBIDDEN:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_FORBIDDEN");
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_GATEWAY_TIMEOUT");
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
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
            mErrorType = OpenWeather.ErrorType.CONNECTION_FAILED;
        }
        return qResult;
    }

    private JSONObject convertStringToJson(String src) {
        try {
            JSONObject obj = new JSONObject(src);
            return obj;
        } catch (JSONException e) {
            WeatherLog.printStack(e);
            mErrorType = OpenWeather.ErrorType.PARSING_FAILED;
            return null;
        }
    }

    private WeatherInfo parseJsonToWeatherInfo(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        try {
            WeatherInfo weatherInfo = new WeatherInfo();

            String iconUrl = "http://openweathermap.org/img/w/";
            String iconFormat = ".png";

            JSONArray root = jsonObject.getJSONArray("list");

            JSONObject list = root.getJSONObject(0);

            long dt = list.getLong("dt");

            JSONObject main = list.getJSONObject("main");
            double temp = main.getDouble("temp");
            weatherInfo.setCurrentTempC((int)Math.round(temp));
            double tempMin = main.getDouble("temp_min");
            weatherInfo.setTodayLowC((int)Math.round(tempMin));
            double tempMax = main.getDouble("temp_max");
            weatherInfo.setTodayHighC((int)Math.round(tempMax));
            double pressure = main.getDouble("pressure");
            weatherInfo.setAtmospherePressure((int)Math.round(pressure));
            double humidity = main.getDouble("humidity");
            weatherInfo.setAtmosphereHumidity((int)Math.round(humidity));


            String dt_txt = list.getString("dt_txt");
            weatherInfo.setLastBuildDate(dt_txt);
            weatherInfo.setCurrentConditionDate(dt_txt);
            weatherInfo.setTodayConditionDate(dt_txt);
            weatherInfo.setPubDate(dt_txt);

            JSONArray weather = list.getJSONArray("weather");
            JSONObject weatherObj = weather.getJSONObject(0);
            String mainString = weatherObj.getString("main");
            weatherInfo.setConditionTitle(mainString);
            weatherInfo.setTodayText(mainString);
            String descriptionString = weatherObj.getString("description");
            weatherInfo.setCurrentCode(descriptionString);
            weatherInfo.setCurrentText(descriptionString);
            weatherInfo.setDescription(descriptionString);
            String iconString = weatherObj.getString("icon");

            JSONObject wind = list.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");
            weatherInfo.setWindSpeedKmh((int)Math.round(turnMpsIntoKmPerHours(windSpeed)));


            JSONObject city = jsonObject.getJSONObject("city");

            String name = city.getString("name");
            weatherInfo.setLocationCity(name);

            String country = city.getString("country");
            weatherInfo.setLocationCountry(country);

            String iconPath = iconUrl + iconString + iconFormat;
            weatherInfo.setCurrentConditionIconURL(iconPath);
            weatherInfo.setTodayConditionIconURL(iconPath);

            WeatherLog.d(weatherInfo.toString());

            return weatherInfo;
        } catch (JSONException e) {
            WeatherLog.printStack(e);
            mErrorType = OpenWeather.ErrorType.PARSING_FAILED;
            return null;
        }
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

            if (mContext != null) {
                final Geocoder geocoder = new Geocoder(mContext);
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat.doubleValue(), lon.doubleValue(), 1);
                    for (Address address : addresses) {
                        int n = address.getMaxAddressLineIndex();
                        for (int i = 0; i < n; i++) {
                            WeatherLog.d("address line : " + address.getAddressLine(i));
                        }

                        String weatherJsonString = getWeatherJsonString(mContext, lat, lon);
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
            if (result == null && mErrorType == null) mErrorType = OpenWeather.ErrorType.UNKNOWN;
            sWeatherInfo = result;
            mWeatherInfoResult.gotWeatherInfo(result, mErrorType);
            mContext = null;
        }
    }
}
