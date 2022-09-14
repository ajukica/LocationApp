package com.example.locationapp;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.List;

public class Map extends AppCompatActivity {
    MapView map = null;

    List <Location> lokacije;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        Gps gps = (Gps) getApplicationContext();
        lokacije = gps.getLokacije();

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        GeoPoint pocetna = new GeoPoint(20.5992, 72.9342);
        mapController.setZoom(10);
        mapController.setCenter(pocetna);


        GeoPoint posljednjaLokacija = pocetna;

        for (Location lokacije: lokacije){
            GeoPoint gpsLokacije = new GeoPoint(lokacije.getLatitude(),lokacije.getLongitude());
            Marker marker = new Marker(map);
            marker.setPosition(gpsLokacije);
            marker.setOnMarkerClickListener((Marker,MapView) ->{
                //marker.showInfoWindow();
                MapView.getController().animateTo(marker.getPosition());
                Toast.makeText(ctx, "Latitude: " + lokacije.getLatitude() +"\n Longtitude: " +lokacije.getLongitude() , Toast.LENGTH_LONG).show();
                return  true;
            });
            marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(marker);
            posljednjaLokacija = gpsLokacije;
        }

        map.getController().animateTo(posljednjaLokacija);

    }



    public void onResume(){
        super.onResume();
        map.onResume();
    }

    public void onPause(){
        super.onPause();
        map.onPause();
    }


}