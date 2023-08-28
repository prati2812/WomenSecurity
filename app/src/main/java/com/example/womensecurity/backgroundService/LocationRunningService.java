package com.example.womensecurity.backgroundService;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.womensecurity.HomeActivity;
import com.example.womensecurity.R;
import com.example.womensecurity.location.GpsTracker;
import com.example.womensecurity.location.LocationData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LocationRunningService extends Service {


    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;

    LocationData locationData1;

    GpsTracker gpsTracker1;


    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }


    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new Notification();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setMaxWaitTime(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location =  locationResult.getLastLocation();
               
                auth = FirebaseAuth.getInstance();
                firebaseDatabase = FirebaseDatabase.getInstance();

        
            double latitude = location.getLatitude();
            double longtitude  = location.getLongitude();
            locationData1 = new LocationData(String.valueOf(latitude) , String.valueOf(longtitude));

            firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(auth.getUid()).child("user location")
                    .setValue(locationData1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                            }
                        }
                    });

            
            }
        };
        startLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        gpsTracker1 = new GpsTracker(LocationRunningService.this);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            Log.d("service" , "running");
                            try {

                                    Thread.sleep(5000);

                            }
                            catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }

        ).start();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final String CHANNEL_ID = "Foreground Service";
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_ID,
                    NotificationManager.IMPORTANCE_LOW
            );

            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this , CHANNEL_ID)
                    .setContentText("location service is running")
                    .setContentTitle("Location is enabled")
                    .setSmallIcon(R.drawable.ic_baseline_location_on_24);

            startForeground(1001 , notification.build());

        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
