package com.firstapp.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int DEFAULT_UPDATE_INTERVAL = 30 * 1000; // Changed to milliseconds
    private static final int FAST_UPDATE_INTERVAL = 5 * 1000; // Changed to milliseconds
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    boolean updateOn = false;
    Location currentlocation;
    List<Location> savedLocations;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address,tv_waypointcounts;
    Switch sw_locationupdates, sw_gps;
    Button btn_newWayPoint, btn_showWayPoint,btn_showmap;
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
        tv_waypointcounts=findViewById(R.id.tv_CountOfCrumbs);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        btn_newWayPoint = findViewById(R.id.btn_newwaypoint);
        btn_showWayPoint = findViewById(R.id.btn_showWayPointList);
        sw_gps = findViewById(R.id.sw_gps);
        btn_showmap=findViewById(R.id.btn_showmap);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());
            }
        };

        btn_newWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication myapplication = (MyApplication)getApplicationContext();
                savedLocations = myapplication.getMylocations();
                savedLocations.add(currentlocation);
            }
        });

        btn_showWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, ShowSavedLocationsList.class);
                startActivity(i);

            }
        });



        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
                updateGPS();
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
    }

    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
            updateGPS();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is not being tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                    currentlocation = location;
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateUIValues(Location location) {
        if (location != null) {
            double latitude = location.getLatitude(); // Get latitude
            double longitude = location.getLongitude(); // Get longitude

            tv_lat.setText(String.valueOf(latitude));
            tv_lon.setText(String.valueOf(longitude));
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));

            if (location.hasAltitude()) {
                tv_altitude.setText(String.valueOf(location.getAltitude()));
            } else {
                tv_altitude.setText("Not available");
            }

            if (location.hasSpeed()) {
                tv_speed.setText(String.valueOf(location.getSpeed()));
            } else {
                tv_speed.setText("Not available");
            }

            Geocoder geocoder = new Geocoder(MainActivity.this);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String addressLine = address.getAddressLine(0);
                    tv_address.setText(addressLine);
                } else {
                    tv_address.setText("Address not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                tv_address.setText("Error retrieving address");
            }

            MyApplication myapplication = (MyApplication)getApplicationContext();
            savedLocations = myapplication.getMylocations();

            tv_waypointcounts.setText(Integer.toString(savedLocations.size()));
        }
    }
}

