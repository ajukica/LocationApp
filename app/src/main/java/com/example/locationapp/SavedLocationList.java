package com.example.locationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedLocationList extends AppCompatActivity {

    ListView lv_savedLocations;
    List <Location> savedLocations;

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("locations","");
        Type type = new TypeToken<ArrayList<Gps>>() {}.getType();
        savedLocations = gson.fromJson(json,type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_location_list2);
        loadData();

        lv_savedLocations = findViewById(R.id.lv_locations);
        //Gps gps = (Gps) getApplicationContext();


        if (savedLocations == null){
            savedLocations = new ArrayList<Location>();
        }

        lv_savedLocations.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1,savedLocations));
    }
}