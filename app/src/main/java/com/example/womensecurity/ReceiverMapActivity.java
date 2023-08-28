package com.example.womensecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.womensecurity.admin.AdminPoliceData;
import com.example.womensecurity.databinding.ActivityReceiverMapBinding;
import com.example.womensecurity.location.GpsTracker;
import com.example.womensecurity.location.LocationData;
import com.example.womensecurity.map.CurrentUserMapsActivity;
import com.example.womensecurity.map.MapsActivity;
import com.example.womensecurity.notification.FirebaseMessagingService;
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

public class ReceiverMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    ActivityReceiverMapBinding binding;

    private GoogleMap mMap;

    LatLng source;
    LatLng destination;

    GpsTracker gpsTracker1;

    public Context context;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityReceiverMapBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.receiver_map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        gpsTracker1 = new GpsTracker(this);
        if(gpsTracker1.canGetLocation()){
            double latitude = gpsTracker1.getLatitude();
            double longtitude = gpsTracker1.getLongitude();
            LatLng sydney = new LatLng(latitude,longtitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }



    }
}