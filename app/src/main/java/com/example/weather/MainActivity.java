package com.example.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView weatherTextView;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherTextView = findViewById(R.id.weatherTextView);
        editText = findViewById(R.id.editText);
    }

    public void getWeather(View view) {
        JsonDownload obj = new JsonDownload();
        String result = "";
        try {
            result = obj.execute("https://openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() +"&appid=b6907d289e10d714a6e88b30761fae22").get();
            Log.i("Result: TXT", result + " " + editText.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ERROR", "Something wrong");
            Toast.makeText(this, "Cannot find " + editText.getText(), Toast.LENGTH_LONG).show();
            weatherTextView.setText("");
        }
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    public class JsonDownload extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int i;
                do {
                    i = reader.read();
                    if (i != -1) json += (char) i;
                } while (i != -1);
                return json;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("ERROR1 ", "WRONG");
                Toast.makeText(getApplicationContext(), "Cannot find " + editText.getText(), Toast.LENGTH_LONG).show();
                weatherTextView.setText("");
                return json;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject object = new JSONObject(s);

                JSONObject coord = object.getJSONObject("coord");
                String lon = coord.getString("lon");
                String lat = coord.getString("lat");

                JSONArray weather = object.getJSONArray("weather");
                JSONObject weatherObject = weather.getJSONObject(0);
                String description = weatherObject.getString("description");

                JSONObject main = object.getJSONObject("main");
                String temp = main.getString("temp");
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");
                String minTemp = main.getString("temp_min");
                String maxTemp = main.getString("temp_max");

                JSONObject wind = object.getJSONObject("wind");
                String speed = wind.getString("speed");
                String message = String.format("Longitude: %s \r\nLatitude: %s \r\nDescription: %s \r\n" +
                        "Temperature: %s \r\nPressure: %s \r\nHumidity: %s \r\nMin. Temperature: %s \r\nMax. Temperature: %s\r\n" +
                                "Wind Speed: %skm",
                        lon, lat, description, temp, pressure, humidity, minTemp, maxTemp, speed);
                weatherTextView.setText(message);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("OnPostExe: ", "ERROR");
                Toast.makeText(getApplicationContext(), "Cannot find " + editText.getText(), Toast.LENGTH_LONG).show();
                weatherTextView.setText("");
            }
        }
    }
}
