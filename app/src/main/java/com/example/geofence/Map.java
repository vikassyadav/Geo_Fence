package com.example.geofence;


import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geofence.databinding.ActivityMapBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class Map extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private static final String TAG = "Map";

    private float GEO_FENCE_RADIUS = 200;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";


    private ActivityMapBinding binding;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);

        geofenceHelper = new GeofenceHelper(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); //type of map

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // LatLng latLng = new LatLng(27.17, 78.04);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        enableUserLocation();

        mMap.setOnMapClickListener(this);


    }

    private void enableUserLocation() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //we need to show user a dialog for dissolution why the permission is needed and then ask for the permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission
                        .ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission
                        .ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);


            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super can be commented
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //this block can be commented
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have the permissions
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
                mMap.setMyLocationEnabled(true);
            } else {
                //we don't have the permission
            }

        }
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have the permissions
                Toast.makeText(this, "you can add geofences", Toast.LENGTH_SHORT).show();
//
                mMap.setMyLocationEnabled(true);
            } else {
                //we don't have the permission
                Toast.makeText(this,  "Background location access is necessary... for geofences to trigger", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        //we made function for this and put them in conditions to check for sdk version > 29
//        mMap.clear();//this will clear the previous marker when new is added
//        addMarker(latLng);
//        addCircle(latLng, GEO_FENCE_RADIUS);  //we had use hard-coded radius for now alter w'll be asking for end user for radius
//        addGeofence(latLng, GEO_FENCE_RADIUS);
        tryAddingGeofence(latLng);


        //check sdk in
//        if(Build.VERSION.SDK_INT>=29){
//            //we need background permission
//            if(ContextCompat.checkSelfPermission(this ,  Manifest.permission.ACCESS_BACKGROUND_LOCATION)) == PackageManager.PERMISSION_GRANTED{
//               tryAddingGeofence(latLng);
//            }else{
//                if(ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_BACKGROUND_LOCATION))==PackageManager.PERMISSION_GRANTED{
//                    //we show dialog and ask for permission
//                ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission
//                        .ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
//                }else{
//                    ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission
//                            .ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
//                }
//
//
//            }
//
//        }
//        else{
//             tryAddingGeofence(latLng);
//        }
    }

    private void tryAddingGeofence(LatLng latLng){

        mMap.clear();//this will clear the previous marker when new is added
        addMarker(latLng);
        addCircle(latLng, GEO_FENCE_RADIUS);  //we had use hard-coded radius for now alter w'll be asking for end user for radius
        addGeofence(latLng, GEO_FENCE_RADIUS);

    }
    private void addGeofence(LatLng latLng, float radius) {
        //combination of all this can be made any one or all  can be used according to need
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER, Geofence.GEOFENCE_TRANSITION_DWELL, Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();


        //can be commented
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Geofence Added!!!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: "+ errorMessage);


                    }
                });
    }
    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);

    }
    private void addCircle(LatLng latLng,float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);

    }
}
