package com.example.kornel.alphaui.weather;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
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
    private static final String API_KEY = "bd74ab823f6c099bf680cfab73897589";

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5 * 1000;

    private static OpenWeather mInstance = new OpenWeather();

    private Weather.ErrorType mErrorType = null;

    private Context mContext;
    private WeatherInfoListener mWeatherInfoResult;


    private int mConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    public static WeatherInfo sWeatherInfo;

    // INSTANCE

    public static OpenWeather getInstance() {
        return mInstance;
    }

    public static OpenWeather getInstance(boolean isDebuggable) {
        WeatherLog.setDebuggable(isDebuggable);
        return mInstance;
    }

    public void queryWeatherByGPS(final Activity activity, final Context context,
                                  final WeatherInfoListener result) {
        WeatherLog.d("query yahoo weather by gps");
        if (!NetworkUtils.isConnected(context)) {
            mErrorType = Weather.ErrorType.CONNECTION_FAILED;
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
                mErrorType = Weather.ErrorType.NO_LOCATION_PERMISSION;
            } else {
                mErrorType = Weather.ErrorType.NO_LOCATION_FOUND;
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
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_GATEWAY");
                case HttpURLConnection.HTTP_BAD_METHOD:
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_METHOD");
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_BAD_REQUEST");
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_CLIENT_TIMEOUT");
                case HttpURLConnection.HTTP_CONFLICT:
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_CONFLICT");
                case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_ENTITY_TOO_LARGE");
                case HttpURLConnection.HTTP_FORBIDDEN:
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_FORBIDDEN");
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
                    throw new Exception("HTTP_GATEWAY_TIMEOUT");
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    mErrorType = Weather.ErrorType.CONNECTION_FAILED;
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
            mErrorType = Weather.ErrorType.CONNECTION_FAILED;
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
            mErrorType = Weather.ErrorType.PARSING_FAILED;
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
            // JSONArray list = jsonObject.getJSONArray("list");
            // Log.d("hakuna", "list" + list);
            // JSONObject main = list.getJSONObject(0);
            // Log.d("hakuna", "main" + main);
            // JSONObject main2 = main.getJSONObject("main");
            // Log.d("hakuna", "main2" + main2);
            // double tempString = main2.getDouble("temp");
            // Log.d("hakuna", "tempString" + tempString);

            // weatherInfo.setTodayCode(Integer.valueOf(todayCode));

            JSONArray root = jsonObject.getJSONArray("list");
            Log.d("hakuna", "root " + root);

            JSONObject list = root.getJSONObject(0);
            Log.d("hakuna", "list " + list);

            long dt = list.getLong("dt");
            Log.d("hakuna", "dt " + dt);

            JSONObject main = list.getJSONObject("main");
            Log.d("hakuna", "main " + main);
            double temp = main.getDouble("temp");
            Log.d("hakuna", "temp " + temp);
            double tempMin = main.getDouble("temp_min");
            Log.d("hakuna", "tempMin " + tempMin);
            double tempMax = main.getDouble("temp_max");
            Log.d("hakuna", "tempMax " + tempMax);
            double pressure = main.getDouble("pressure");
            Log.d("hakuna", "pressure " + pressure);
            double humidity = main.getDouble("humidity");
            Log.d("hakuna", "humidity " + humidity);

            String dt_txt = list.getString("dt_txt");
            Log.d("hakuna", "dt_txt " + dt_txt);

            JSONArray weather = list.getJSONArray("weather");
            Log.d("hakuna", "weather " + weather);
            JSONObject weatherObj = weather.getJSONObject(0);
            Log.d("hakuna", "weatherObj " + weatherObj);
            String mainString = weatherObj.getString("main");
            Log.d("hakuna", "mainString " + mainString);
            String descriptionString = weatherObj.getString("description");
            Log.d("hakuna", "descriptionString " + descriptionString);
            String iconString = weatherObj.getString("icon");
            Log.d("hakuna", "iconString " + iconString);

            JSONObject wind = list.getJSONObject("wind");
            Log.d("hakuna", "wind " + wind);
            double windSpeed = wind.getDouble("speed");
            Log.d("hakuna", "windSpeed " + windSpeed);


            JSONObject city = jsonObject.getJSONObject("city");
            Log.d("hakuna", "city " + city);

            String name = city.getString("name");
            Log.d("hakuna", "name " + name);

            String country = city.getString("country");
            Log.d("hakuna", "country " + country);



            weatherInfo.setLastBuildDate(dt_txt);


            weatherInfo.setLocationCity(name);
            weatherInfo.setLocationCountry(country);

            weatherInfo.setWindSpeedKmh((int)Math.round(turnMpsIntoKmPerHours(windSpeed)));

            weatherInfo.setAtmosphereHumidity((int)Math.round(humidity));
            weatherInfo.setAtmospherePressure((int)Math.round(pressure));


            weatherInfo.setConditionTitle(mainString);

            weatherInfo.setPubDate(dt_txt);

            // Current
            weatherInfo.setCurrentConditionDate(dt_txt);
            weatherInfo.setCurrentTempC((int)Math.round(temp));
            weatherInfo.setCurrentText(descriptionString);

            // For today
            weatherInfo.setTodayConditionDate(dt_txt);
            weatherInfo.setTodayHighC((int)Math.round(tempMax));
            weatherInfo.setTodayLowC((int)Math.round(tempMin));
            weatherInfo.setTodayText(mainString);
            weatherInfo.setDescription(descriptionString);

            String iconPath = iconUrl + iconString + iconFormat;
            weatherInfo.setCurrentConditionIconURL(iconPath);
            weatherInfo.setTodayConditionIconURL(iconPath);

            weatherInfo.setCurrentCode(descriptionString);

            WeatherLog.d(weatherInfo.toString());

            return weatherInfo;
        } catch (JSONException e) {
            WeatherLog.printStack(e);
            mErrorType = Weather.ErrorType.PARSING_FAILED;
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
            if (result == null && mErrorType == null) mErrorType = Weather.ErrorType.UNKNOWN;
            sWeatherInfo = result;
            mWeatherInfoResult.gotWeatherInfo(result, mErrorType);
            mContext = null;
        }
    }
}
