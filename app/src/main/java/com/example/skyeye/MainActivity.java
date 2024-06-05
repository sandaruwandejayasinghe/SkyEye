package com.example.skyeye;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";

    // UI Elements
    private FusedLocationProviderClient fusedLocationClient;
    private TextView weatherConditionText, tempText, feelsLikeText, tempMaxText, tempMinText, humidityText, windSpeedText, addressText, rainfallText, locationText, timeText;
    private ImageView weatherImage;
    private Button refreshButton;

    // Helper Classes
    WeatherFetcher weatherFetcher;
    private LocationHandler locationHandler;

    // Permission request launcher
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // If permission is granted, get the current location
                    locationHandler.getCurrentLocation();
                } else {
                    // Show a message if permission is denied
                    showPermissionDeniedMessage();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initViews();

        // Initialize location client and handlers
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationHandler = new LocationHandler(this, fusedLocationClient);
        weatherFetcher = new WeatherFetcher(this);

        // Request location permission
        requestLocationPermission();

        // Set refresh button click listener
        refreshButton.setOnClickListener(v -> {
            refreshButton.setEnabled(false);
            refreshButton.setText("Waiting for data...");
            locationHandler.getCurrentLocation();
        });
    }

    // Initialize the UI elements
    private void initViews() {
        weatherConditionText = findViewById(R.id.weatherConditionText);
        tempText = findViewById(R.id.tempText);
        feelsLikeText = findViewById(R.id.feelsLikeText);
        tempMaxText = findViewById(R.id.tempMaxText);
        tempMinText = findViewById(R.id.tempMinText);
        humidityText = findViewById(R.id.humidityText);
        windSpeedText = findViewById(R.id.windSpeedText);
        addressText = findViewById(R.id.addressText);
        weatherImage = findViewById(R.id.weatherImage);
        rainfallText = findViewById(R.id.rainfallText);
        locationText = findViewById(R.id.locationText);
        timeText = findViewById(R.id.timeText);
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setEnabled(false);
        refreshButton.setText("Waiting for data...");
    }

    // Request location permission
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationHandler.getCurrentLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    // Show a message if permission is denied
    private void showPermissionDeniedMessage() {
        Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
    }

    // Update the time display
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        timeText.setText(currentTime);
    }

    // Update the weather UI with new data
    public void updateWeatherUI(String temp, String feelsLike, String tempMax, String tempMin, String pressure, String humidity, String windSpeed, String weatherCondition, String rainfall) {
        weatherConditionText.setText(weatherCondition);
        tempText.setText(temp + "°");
        feelsLikeText.setText(feelsLike + "°C");
        tempMaxText.setText(tempMax + "°C");
        tempMinText.setText(tempMin + "°C");
        humidityText.setText(humidity + "%");
        windSpeedText.setText(windSpeed + " km/h");
        rainfallText.setText(rainfall + " mm");

        setWeatherImage(weatherCondition);
        updateTime();
    }

    // Update the address UI with new data
    public void updateAddressUI(String address) {
        addressText.setText(address);
    }

    // Set the weather image based on the weather condition
    private void setWeatherImage(String weatherCondition) {
        switch (weatherCondition) {
            case "Clear":
                weatherImage.setImageResource(R.drawable.sunny);
                break;
            case "Clouds":
                weatherImage.setImageResource(R.drawable.cloudy);
                break;
            case "Rain":
            case "Drizzle":
                weatherImage.setImageResource(R.drawable.rainy);
                break;
            case "Snow":
                weatherImage.setImageResource(R.drawable.snowy);
                break;
            case "Thunderstorm":
                weatherImage.setImageResource(R.drawable.stormy);
                break;
            case "Atmosphere":
                weatherImage.setImageResource(R.drawable.windy);
                break;
            default:
                weatherImage.setImageResource(R.drawable.sunny);
                break;
        }
    }

    // Update the location UI with new data
    public void updateLocationUI(double lat, double lon) {
        locationText.setText("lat: " + lat + ", lon: " + lon);
    }
}
