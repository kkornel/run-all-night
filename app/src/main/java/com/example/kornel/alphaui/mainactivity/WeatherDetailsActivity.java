package com.example.kornel.alphaui.mainactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.weather.WeatherConsts;
import com.example.kornel.alphaui.weather.WeatherInfo;
import com.squareup.picasso.Picasso;

import static com.example.kornel.alphaui.weather.WeatherInfo.CELSIUS;
import static com.example.kornel.alphaui.weather.WeatherInfo.HECTOPASCAL;
import static com.example.kornel.alphaui.weather.WeatherInfo.KMH;
import static com.example.kornel.alphaui.weather.WeatherInfo.PERCENT;

public class WeatherDetailsActivity extends AppCompatActivity {
    public static final String WEATHER_INFO_INTENT_EXTRAS = "weather_info_intent_extras";

    private TextView mCurrentLocationTextView;
    private ImageView mCurrentWeatherIconImageView;
    private TextView mCurrentTemperatureTextView;
    private TextView mCurrentWeatherDescriptionTextView;
    private TextView mCurrentUpdateTimeTextView;
    private TextView mHumidityTextView;
    private TextView mPressureTextView;
    private TextView mWindTextView;
    private TextView mTodayDateTextView;
    private ImageView mTodayWeatherIconImageView;
    private TextView mTodayHighTemperatureTextView;
    private TextView mTodayLowTemperatureTextView;
    private TextView mTodayWeatherDescriptionTextView;
    private TextView mTodayLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);

        getSupportActionBar().setTitle(R.string.details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mCurrentLocationTextView = findViewById(R.id.currentTimeLocationTextView);
        mCurrentWeatherIconImageView = findViewById(R.id.currentWeatherIconImageView);
        mCurrentTemperatureTextView = findViewById(R.id.currentTemperatureTextView);
        mCurrentWeatherDescriptionTextView = findViewById(R.id.currentWeatherDescriptionTextView);
        mCurrentUpdateTimeTextView = findViewById(R.id.currentUpdateTimeTextView);

        mHumidityTextView = findViewById(R.id.humidityTextView);
        mPressureTextView = findViewById(R.id.pressureTextView);
        mWindTextView = findViewById(R.id.windTextView);

        mTodayDateTextView = findViewById(R.id.todayDateTextView);
        mTodayWeatherIconImageView = findViewById(R.id.todayWeatherIconImageView);
        mTodayHighTemperatureTextView = findViewById(R.id.todayHighTempTextView);
        mTodayLowTemperatureTextView = findViewById(R.id.todayLowTempTextView);
        mTodayWeatherDescriptionTextView = findViewById(R.id.todayWeatherDescriptionTextView);
        mTodayLocationTextView = findViewById(R.id.todayLocationTextView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            WeatherInfo weatherInfo = extras.getParcelable(WEATHER_INFO_INTENT_EXTRAS);
            mCurrentLocationTextView.setText(weatherInfo.getAddress().getThoroughfare() + ", " + weatherInfo.getAddress().getLocality());
            Picasso.get()
                    .load(weatherInfo.getCurrentConditionIconURL())
                    .into(mCurrentWeatherIconImageView);
            mCurrentTemperatureTextView.setText(weatherInfo.getCurrentTempC() + CELSIUS);
            mCurrentWeatherDescriptionTextView.setText(WeatherConsts.getConditionPlByCode(weatherInfo.getCurrentCode()));
            mCurrentUpdateTimeTextView.setText(getString(R.string.updated) + " " + weatherInfo.getTimeFormatted());

            mHumidityTextView.setText(weatherInfo.getAtmosphereHumidity() + PERCENT);
            mPressureTextView.setText(weatherInfo.getAtmospherePressure() + HECTOPASCAL);
            mWindTextView.setText(weatherInfo.getWindSpeedKmh() + KMH);

            mTodayDateTextView.setText(weatherInfo.getCurrentConditionDatePl());
            mTodayLocationTextView.setText(weatherInfo.getAddress().getLocality());
            Picasso.get()
                    .load(weatherInfo.getTodayConditionIconURL())
                    .into(mTodayWeatherIconImageView);
            mTodayHighTemperatureTextView.setText(weatherInfo.getTodayHighC() + CELSIUS);
            mTodayLowTemperatureTextView.setText(weatherInfo.getTodayLowC() + CELSIUS);
            mTodayWeatherDescriptionTextView.setText(WeatherConsts.getConditionPlByCode(weatherInfo.getTodayCode()));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
