package com.example.skyeye;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {

    // Reference to the activity
    private Activity activity;

    // Constructor
    public WeatherFetcher(Activity activity) {
        this.activity = activity;
    }

    // Fetch weather data using the OpenWeatherMap API
    public void fetchWeatherData(double lat, double lon) {
        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=44e5de48279dab0408d006bdcb65acfc";
        new GetWeatherTask().execute(weatherUrl);
    }

    // AsyncTask to fetch weather data in the background
    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                Log.e("WeatherFetcher", "Error fetching weather data", e);
                return null;
            }
        }

        // Convert Kelvin to Celsius
        public String kelvinToCelsius(double kelvinTemperature) {
            int celsiusTemperature = (int) Math.round(kelvinTemperature - 273.15);
            return String.valueOf(celsiusTemperature);
        }

        // Convert meters per second to kilometers per hour
        public String metersPerSecondToKilometersPerHour(double metersPerSecond) {
            double kmPerHour = (double) (Math.round(metersPerSecond * 3.6 * 10)) / 10;
            return String.valueOf(kmPerHour);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.e("WeatherFetcher", "No weather data returned");
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                JSONObject wind = jsonObject.getJSONObject("wind");
                String weatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

                // Get weather data
                String temp = kelvinToCelsius(main.getDouble("temp"));
                String feelsLike = kelvinToCelsius(main.getDouble("feels_like"));
                String tempMax = kelvinToCelsius(main.getDouble("temp_max"));
                String tempMin = kelvinToCelsius(main.getDouble("temp_min"));
                String pressure = String.valueOf(main.getInt("pressure"));
                String humidity = String.valueOf(main.getInt("humidity"));
                String windSpeed = metersPerSecondToKilometersPerHour(wind.getDouble("speed"));
                String rainfall = "00";

                // Check if there is rainfall data
                if (jsonObject.has("rain")) {
                    JSONObject rain = jsonObject.getJSONObject("rain");
                    if (rain.has("1h")) {
                        rainfall = String.valueOf(rain.getDouble("1h"));
                    }
                }

                // Update the weather UI
                ((MainActivity) activity).updateWeatherUI(temp, feelsLike, tempMax, tempMin, pressure, humidity, windSpeed, weatherCondition, rainfall);

                // Re-enable the refresh button
                Button refreshButton = activity.findViewById(R.id.refreshButton);
                refreshButton.setEnabled(true);
                refreshButton.setText("Refresh");

            } catch (Exception e) {
                Log.e("WeatherFetcher", "Error parsing weather data", e);
            }
        }
    }
}
