package com.example.womensecurity.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.womensecurity.R;
import com.example.womensecurity.admin.AdminPoliceData;
import com.example.womensecurity.databinding.ActivityMapsBinding;
import com.example.womensecurity.location.GpsTracker;
import com.example.womensecurity.location.LocationData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;


import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    LatLng source;
    LatLng destination;


    public Context context;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

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

        GpsTracker gpsTracker = new GpsTracker(MapsActivity.this);

        Intent i = getIntent();
        String nearPoliceId  = i.getStringExtra("nearPoliceId");

        Log.d("policeId" , String.valueOf(nearPoliceId));





        DatabaseReference minReference = firebaseDatabase.getReference().child("Women Security").child("Admin").child("Police");
        minReference.child(nearPoliceId).child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChildren()){
                    LocationData locationData = snapshot.getValue(LocationData.class);
                    String dest_latitude = locationData.getLatitude();
                    String dest_longtitude = locationData.getLongtitude();

                    source = new LatLng(gpsTracker.getLatitude() , gpsTracker.getLongitude());
                    destination = new LatLng(Double.parseDouble(dest_latitude) , Double.parseDouble(dest_longtitude));

                    mMap.addMarker(new MarkerOptions().position(source).title("Your Location"));
                    mMap.addMarker(new MarkerOptions().position(destination).title("Near Police Station"));

                    mMap.addPolyline((new PolylineOptions()).add(source , destination).width(15).color(Color.RED).geodesic(true));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source,14));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}