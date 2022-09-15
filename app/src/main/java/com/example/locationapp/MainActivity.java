package com.example.locationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final int INTERVAL_MILLIS = 30000;
    public static final int FASTEST_INTERVAL_MILLIS = 5000;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address,tv_locationNumber;
    Button btn_newLocation, btn_locationList, btn_Map;
    Switch sw_locationupdates, sw_gps;

    Location currentLocation;
    List <Location> savedLocations;

    // Postavke za API poziv
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    // API za lokacijske usluge googla
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        btn_newLocation = findViewById(R.id.btn_newLocation);
        btn_locationList = findViewById(R.id.btn_locationList);
        tv_locationNumber = findViewById(R.id.tv_locationNumber);
        btn_Map = findViewById(R.id.btn_Map);
        //inicijalizacija postavki za lokaciju
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL_MILLIS);
        locationRequest.setFastestInterval(FASTEST_INTERVAL_MILLIS);
        int highAccuracy = Priority.PRIORITY_HIGH_ACCURACY;
        int lowAccuracy = Priority.PRIORITY_BALANCED_POWER_ACCURACY;


        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

        btn_newLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gps gps = (Gps) getApplicationContext();
                savedLocations = gps.getLokacije();
                savedLocations.add(currentLocation);
                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(savedLocations);
                editor.putString("locations",json);
                editor.commit();
                //editor.apply();
            }
        });


        btn_locationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SavedLocationList.class);
                startActivity(i);
            }
        });

        btn_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Map.class);
                startActivity(i);
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(highAccuracy);
                    tv_sensor.setText("Koristim GPS senzore");
                } else {
                    locationRequest.setPriority(lowAccuracy);
                    tv_sensor.setText("Koristim WIFI+Tornjeve");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_locationupdates.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
    }

    private void stopLocationUpdates() {

        tv_updates.setText("Praćenje lokacije isključeno ");
        tv_lat.setText("Praćenje lokacije isključeno");
        tv_lon.setText("Praćenje lokacije isključeno");
        tv_speed.setText("Praćenje lokacije isključeno");
        tv_address.setText("Praćenje lokacije isključeno");
        tv_accuracy.setText("Praćenje lokacije isključeno");
        tv_altitude.setText("Praćenje lokacije isključeno");
        tv_sensor.setText("Praćenje lokacije isključeno");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

    }

    private void startLocationUpdates() {
        tv_updates.setText("Praćenje lokacije uključeno");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSIONS_FINE_LOCATION:
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updateGPS();
            }else {
                Toast.makeText(this,"Ova aplikacija zahtjeva lokacijske usluge!",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    updateUIValues(location);
                    currentLocation = location;
                }

            });
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }



    private void   updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }else{
            tv_altitude.setText("Nije podržano");
        }

        if (location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }else{
            tv_speed.setText("Nije podržano");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);

        try {
            List <Address> adresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(adresses.get(0).getAddressLine(0));
        } catch (Exception e){
            tv_address.setText("Dohvaćanje adrese nije uspjelo");
        }

        Gps gps = (Gps) getApplicationContext();
        savedLocations = gps.getLokacije();
        tv_locationNumber.setText(Integer.toString(savedLocations.size()));
    }
}