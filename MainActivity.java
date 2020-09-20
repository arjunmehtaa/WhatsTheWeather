package com.arjuj.whatstheweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView cityOutput;
    TextView mainOutput;
    TextView temperatureOutput;
    TextView descriptionOutput;
    GridLayout outputScreen;
    LinearLayout welcomeScreen;
    Button getWeatherButton;
    TextView enterCity;
    TextView pressureOutput;
    TextView minmaxOuput;
    TextView humidityOuput;
    ImageView imageView;
    ImageView imageView2;
    ImageView imageView3;
    View line;
    AutoCompleteTextView cityInput;

    LocationManager locationManager;
    LocationListener locationListener;
    String latString="empty";
    String longString="empty";
    Location myLocation;
    Location mlocation;
    Looper looper;
    Criteria criteria;
    boolean inUse=false;
    Button useLocationButton;
    TextView allowText;





    @Override
    public void onBackPressed() {
        if(inUse==true){
            inUse=false;
            cityInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(cityInput, InputMethodManager.SHOW_IMPLICIT);
            imageView.animate().alpha(1).setDuration(500);
            imageView2.animate().alpha(0).setDuration(500);
            imageView3.animate().alpha(0).setDuration(500);
            welcomeScreen.setVisibility(View.VISIBLE);
            outputScreen.setVisibility(View.INVISIBLE);
            cityOutput.setText("");
            mainOutput.setText("-");
            descriptionOutput.setText("-");
            temperatureOutput.setText("-\u2103");
            pressureOutput.setText("-hPa");
            minmaxOuput.setText("-\u2103/-\u2103");
            humidityOuput.setText("-%");
        }
        else{
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {


                locationManager.requestSingleUpdate(criteria, locationListener, looper);

                useLocationButton.setText("U S E   Y O U R   L O C A T I O N");
                allowText.setVisibility(View.INVISIBLE);




                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.txt_layout);
        cityInput=(AutoCompleteTextView) findViewById(R.id.cityInput);
        cityOutput=(TextView)findViewById(R.id.cityOutput);
        mainOutput=(TextView)findViewById(R.id.mainOutput);
        temperatureOutput=(TextView)findViewById(R.id.temperatureOutput);
        descriptionOutput=(TextView)findViewById(R.id.descriptionOutput);
        outputScreen=(GridLayout) findViewById(R.id.outputScreen);
        welcomeScreen=(LinearLayout)findViewById(R.id.welcomeScreen);
        getWeatherButton=(Button)findViewById(R.id.getWeatherButton);
        enterCity=(TextView)findViewById(R.id.enterCity);
        pressureOutput=(TextView)findViewById(R.id.pressureOutput);
        minmaxOuput=(TextView)findViewById(R.id.minmaxOutput);
        humidityOuput=(TextView)findViewById(R.id.humidityOutput);
        imageView=(ImageView)findViewById(R.id.imageView);
        imageView2=(ImageView)findViewById(R.id.imageView2);
        imageView3=(ImageView)findViewById(R.id.imageView3);
        line=(View)findViewById(R.id.line);
        allowText=(TextView)findViewById(R.id.allowText);

        String cities[]={};
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cities);

        cityInput.setAdapter(arrayAdapter);
        cityInput.setThreshold(2);


        useLocationButton = (Button) findViewById(R.id.useLocationButton);






        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mlocation = location;
                Log.d("Location Changes", location.toString());
                latString=Double.toString(location.getLatitude());
                longString=Double.toString(location.getLongitude());
                locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Status Changed", String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Provider Enabled", provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Provider Disabled", provider);
            }
        };

        // Now first make a criteria with your requirements
        // this is done to save the battery life of the device
        // there are various other other criteria you can search for..

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);



       locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        looper = null;

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(criteria, locationListener, looper);
            useLocationButton.setText("U S E   Y O U R   L O C A T I O N");
            allowText.setVisibility(View.INVISIBLE);

        }





        useLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);


                }
                else {
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    myLocation=lastKnownLocation;
                    locationManager.requestSingleUpdate(criteria, locationListener, looper);


                    inUse=true;
                    imageView.animate().alpha(0).setDuration(1000);
                    imageView3.animate().alpha(1).setDuration(1000);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(welcomeScreen.getWindowToken(), 0);
                    cityInput.onEditorAction(EditorInfo.IME_ACTION_DONE);



                    welcomeScreen.setVisibility(View.INVISIBLE);
                    outputScreen.setVisibility(View.VISIBLE);


                    DownloadTask task1=new DownloadTask();
                    task1.execute("https://openweathermap.org/data/2.5/weather?lat="+latString+"&lon="+longString+"&appid=439d4b804bc8187953eb36d2a8c26a02");
                }




            }
        });








    }

    public void getWeather(View view) {


        inUse=true;
        imageView.animate().alpha(0).setDuration(1000);
        imageView3.animate().alpha(1).setDuration(1000);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(welcomeScreen.getWindowToken(), 0);
        cityInput.onEditorAction(EditorInfo.IME_ACTION_DONE);



        welcomeScreen.setVisibility(View.INVISIBLE);
        outputScreen.setVisibility(View.VISIBLE);
        cityOutput.setText(cityInput.getText().toString());




        DownloadTask task=new DownloadTask();
        task.execute("https://openweathermap.org/data/2.5/weather?q="+cityInput.getText().toString()+"&appid=439d4b804bc8187953eb36d2a8c26a02");

    }






    public void enterAgain(View view){

        inUse=false;
        cityInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(cityInput, InputMethodManager.SHOW_IMPLICIT);
        imageView.animate().alpha(1).setDuration(500);
        imageView2.animate().alpha(0).setDuration(500);
        imageView3.animate().alpha(0).setDuration(500);
        welcomeScreen.setVisibility(View.VISIBLE);
        outputScreen.setVisibility(View.INVISIBLE);
        cityOutput.setText("");
        mainOutput.setText("-");
        descriptionOutput.setText("-");
        temperatureOutput.setText("-\u2103");
        pressureOutput.setText("-hPa");
        minmaxOuput.setText("-\u2103/-\u2103");
        humidityOuput.setText("-%");
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String result= "";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();

                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();

                }
                return result;

            }catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo=jsonObject.getString("weather");
                JSONArray arr=new JSONArray(weatherInfo);

                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart=arr.getJSONObject(i);
                    String mainText=jsonPart.getString("main");

                    cityOutput.setText(cityInput.getText().toString());
                    mainOutput.setText(mainText);
                    descriptionOutput.setText(jsonPart.getString("description"));

                    if(mainText.equals("Haze")){
                        imageView2.setImageResource(R.drawable.haze);

                    }
                    else if(mainText.equals("Clear")){
                        imageView2.setImageResource(R.drawable.clear);
                    }
                    else if(mainText.equals("Thunderstorm")){
                        imageView2.setImageResource(R.drawable.thunderstorm);
                    }
                    else if(mainText.equals("Clouds")){
                        imageView2.setImageResource(R.drawable.clouds);
                    }
                    else if(mainText.equals("Drizzle")){
                        imageView2.setImageResource(R.drawable.drizzle);
                    }
                    else if(mainText.equals("Rain")){
                        imageView2.setImageResource(R.drawable.rain);
                    }
                    else if(mainText.equals("Snow")){
                        imageView2.setImageResource(R.drawable.snow);
                    }
                    else if(mainText.equals("Mist")){
                        imageView2.setImageResource(R.drawable.mist);
                    }
                    else if(mainText.equals("Smoke")){
                        imageView2.setImageResource(R.drawable.smoke);
                    }
                    else if(mainText.equals("Dust")){
                        imageView2.setImageResource(R.drawable.dust);
                    }
                    else if(mainText.equals("Fog")){
                        imageView2.setImageResource(R.drawable.fog);
                    }
                    else if(mainText.equals("Sand")){
                        imageView2.setImageResource(R.drawable.sand);
                    }
                    else if(mainText.equals("Ash")){
                        imageView2.setImageResource(R.drawable.ash);
                    }
                    else if(mainText.equals("Squall")){
                        imageView2.setImageResource(R.drawable.squall);
                    }
                    else if(mainText.equals("Tornado")){
                        imageView2.setImageResource(R.drawable.tornado);
                    }

                    imageView3.animate().alpha(0).setDuration(500);
                    imageView2.animate().alpha(1).setDuration(500);







                }
                JSONObject currentTemp = jsonObject.getJSONObject("main");
                double temp = currentTemp.getDouble("temp");
                double pressure = currentTemp.getDouble("pressure");
                double mintemp = currentTemp.getDouble("temp_min");
                double maxtemp = currentTemp.getDouble("temp_max");
                double humidity = currentTemp.getDouble("humidity");
                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart=arr.getJSONObject(i);
                    int finalTemp=(int)temp;
                    int finalPressure=(int)pressure;
                    int finalmintemp=(int)mintemp;
                    int finalmaxtemp=(int)maxtemp;
                    int finalHumidity=(int)humidity;






                    temperatureOutput.setText(Integer.toString(finalTemp)+"\u2103");
                    pressureOutput.setText(Integer.toString(finalPressure)+"hPa");
                    minmaxOuput.setText(Integer.toString(finalmintemp)+"\u2103"+" / "+Integer.toString(finalmaxtemp)+"\u2103");
                    humidityOuput.setText(Integer.toString(finalHumidity)+"%");





                }

                JSONObject currentCity = jsonObject;
                String city = currentCity.getString("name");
                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart=arr.getJSONObject(i);

                    cityOutput.setText(city);


                }


            }catch(Exception e){
                e.printStackTrace();;

            }
        }
    }
}
