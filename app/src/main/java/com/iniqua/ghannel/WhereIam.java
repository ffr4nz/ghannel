package com.iniqua.ghannel;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import java.sql.Timestamp;

public class WhereIam extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private long lastepoch = 0;
    private String emailuser = "<GMAIL_USER>";
    private String emailpass = "<GMAIL_USER_PASS>";
    private Integer mins = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_where_iam);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


    }

    private void makeUseOfNewLocation(Location location) {
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("You are here and we know it."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        String lat = Double.toString(location.getLatitude());
        String lon = Double.toString(location.getLongitude());
        String b64lat = Base64.encodeToString(lat.getBytes(),0);
        String b64lon = Base64.encodeToString(lon.getBytes(),0);
        java.util.Date date =  new java.util.Date();
        long epoch = date.getTime();
        Log.i("INFO", "epoch: "+String.valueOf(epoch));
        Log.i("INFO", "lastepoch: "+String.valueOf(lastepoch));
        if (lastepoch < (epoch - mins * 60 * 1000)) {
            String dmail = emailuser + "+" + String.valueOf(epoch) + "-" + b64lat.trim() + "-" + b64lon.trim() + "@gmail.com";
            try {
                GMailSender sender = new GMailSender(emailuser+"@gmail.com", emailpass);
                sender.sendMail("This is Subject", "This is Body", emailuser+"@gmail.com", dmail);
                Log.i("INFO", "Email sent");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            lastepoch = epoch;
        }else{
            Log.i("INFO", "Wait for it!");
        }
    }

}
