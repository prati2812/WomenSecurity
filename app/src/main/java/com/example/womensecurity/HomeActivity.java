package com.example.womensecurity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.womensecurity.admin.AdminHospitalData;
import com.example.womensecurity.admin.AdminPoliceData;
import com.example.womensecurity.backgroundService.LocationRunningService;
import com.example.womensecurity.contact.ContactItem;
import com.example.womensecurity.databinding.ActivityHomeBinding;
import com.example.womensecurity.location.GpsTracker;
import com.example.womensecurity.location.LocationData;
import com.example.womensecurity.map.CurrentUserMapsActivity;
import com.example.womensecurity.map.MapsActivity;
import com.example.womensecurity.notification.FcmNotificationsSender;
import com.example.womensecurity.user.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ActivityHomeBinding binding;


    public ArrayList<ContactItem> contactItems;
    LocationData locationData1;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;


    public GpsTracker gpsTracker1;

    Intent policeIntent;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 100);
            }
        }

        // this is a special permission required only by devices using
        // Android Q and above. The Access Background Permission is responsible
        // for populating the dialog with "ALLOW ALL THE TIME" option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        policeIntent = new Intent(HomeActivity.this , MapsActivity.class);
        DatabaseReference minReference = firebaseDatabase.getReference().child("Women Security").child("Admin").child("Police");
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Women Security").child("Admin").child("Police");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){


                    databaseReference.child(dataSnapshot.getKey()).child("location")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChildren()){
                                        LocationData locationData = snapshot.getValue(LocationData.class);
                                        String dest_latitude = locationData.getLatitude();
                                        String dest_longtitude = locationData.getLongtitude();

                                        double latitude = gpsTracker1.getLatitude();
                                        double longtitude = gpsTracker1.getLongitude();

                                        Location me = new Location("");
                                        Location dest = new Location("");

                                        me.setLatitude(latitude);
                                        me.setLongitude(longtitude);


                                        dest.setLatitude(Double.parseDouble(dest_latitude));
                                        dest.setLongitude(Double.parseDouble(dest_longtitude));

                                        double min_distance = Integer.MAX_VALUE;

                                        double distance = me.distanceTo(dest);

                                        String id = null;

                                        if(min_distance > distance){
                                            min_distance = distance;
                                            id = dataSnapshot.getKey();
                                            policeIntent.putExtra("nearPoliceId" , String.valueOf(dataSnapshot.getKey()));
                                            minReference.child(id).child("police Data").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.hasChildren()){
                                                        AdminPoliceData adminPoliceData = snapshot.getValue(AdminPoliceData.class);
                                                        String policeName = adminPoliceData.getAdminName();
                                                        binding.nearPoliceStationName.setText(policeName);

                                                        Log.d("nearPoliceId" , dataSnapshot.getKey());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        Log.d("mindistance" , String.valueOf(distance));
                                        Log.d("min_id",id);



                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // User Profile In Home UI
        binding.userProfileUpdateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, showProfile.class));
            }
        });


        // Emergency Contact In Home UI
        binding.cardForEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, EmegencyContactDisplay.class));
                return;
            }
        });


        // find near police station and his location
        binding.nearPoliceStationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(policeIntent);
                return;
            }
        });



        // User Current location


        if(!isConnected(this)){
            showInternetDialog();
        }

        if(!foregroundServiceRunning()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent i = new Intent(HomeActivity.this , LocationRunningService.class);
                startForegroundService(i);
            }
        }


    }


    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo services: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(LocationRunningService.class.getName().equals(services.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void showInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.nointernetconnection, findViewById(R.id.no_internet_connection_layout));
        view.findViewById(R.id.relative_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(HomeActivity.this)) {
                    showInternetDialog();

                } else {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }
            }
        });

        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private boolean isConnected(HomeActivity homeActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) homeActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onStart() {
        super.onStart();


        gpsTracker1 = new GpsTracker(HomeActivity.this);
        if(gpsTracker1.canGetLocation()){
            double latitude = gpsTracker1.getLatitude();
            double longtitude  = gpsTracker1.getLongitude();
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
        else
        {
            gpsTracker1.showSettingsAlert();
        }


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String deviceToken = Objects.requireNonNull(task.getResult()).getToken();

                            firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                                    .child("User Data").child("deviceToken").setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                        }

                    }
                });


        // Retrieve Image
        DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                .child("User Data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    User user = snapshot.getValue(User.class);
                    String imagePath = user.getImagePath();
                    String userName = user.getUserName();
                    String phoneNumber =  user.getPhoneNumber();
                    Glide.with(HomeActivity.this).load(imagePath)
                            .placeholder(R.drawable.person_icon)
                            .into(binding.userHomeProfileCircleImage);
                    binding.userHomeUiName.setText(userName);
                    binding.userHomeUiPhonenumber.setText(phoneNumber);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        double latitude = 0;
        double longtitude = 0;


        if(gpsTracker1.canGetLocation()){
             latitude = gpsTracker1.getLatitude();
             longtitude  = gpsTracker1.getLongitude();
        }


        // near hospital
        DatabaseReference minHospitalReference = firebaseDatabase.getReference().child("Women Security").child("Admin").child("Hospital");
        DatabaseReference databaseReference1 = firebaseDatabase.getReference().child("Women Security").child("Admin").child("Hospital");
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            databaseReference1.child(dataSnapshot.getKey()).child("location")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                             if (snapshot.hasChildren()){
                                                 LocationData locationData = snapshot.getValue(LocationData.class);
                                                 String dest_hospital_latitude = locationData.getLatitude();
                                                 String dest_hospital_longtitude = locationData.getLongtitude();

                                                 double latitude = gpsTracker1.getLatitude();
                                                 double longtitude = gpsTracker1.getLongitude();


                                                 Location me = new Location("");
                                                 Location dest = new Location("");


                                                 me.setLatitude(latitude);
                                                 me.setLongitude(longtitude);


                                                 dest.setLatitude(Double.parseDouble(dest_hospital_latitude));
                                                 dest.setLongitude(Double.parseDouble(dest_hospital_longtitude));

                                                 double min_distance = Integer.MAX_VALUE;

                                                 double distance = me.distanceTo(dest);

                                                 String id = null;


                                                 if(min_distance > distance){

                                                     min_distance = distance;
                                                     id = dataSnapshot.getKey();
                                                     minHospitalReference.child(id).child("Hospital Data").addValueEventListener(new ValueEventListener() {
                                                         @Override
                                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                             if(snapshot.hasChildren()){
                                                                 AdminHospitalData adminHospitalData = snapshot.getValue(AdminHospitalData.class);
                                                                 String hospitalName = adminHospitalData.getHospitalName();
                                                                 binding.nearHospitalName.setText(hospitalName);
                                                             }
                                                         }

                                                         @Override
                                                         public void onCancelled(@NonNull DatabaseError error) {

                                                         }
                                                     });

                                                 }

                                             }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        GpsTracker gpsTracker = new GpsTracker(HomeActivity.this);


        // Retrieve Image
        DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                .child("user location");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    LocationData user = snapshot.getValue(LocationData.class);

                    double latitude = Double.parseDouble(user.getLatitude());
                    double longtitude = Double.parseDouble(user.getLongtitude());

                    LatLng sydney = new LatLng(latitude,longtitude);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {
                            startActivity(new Intent(HomeActivity.this , CurrentUserMapsActivity.class));
                            return;
                        }
                    });


                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        gpsTracker1 = new GpsTracker(HomeActivity.this);

        if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
            ++count;
            if(count == 6){
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentRef = rootRef.child("Women Security").child("User Panel").child("User").child(auth.getUid()).child("Emergency Contact");
                currentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            String token = user.getDeviceToken();
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token , "Hey i am feel uncomfortable"
                                    ,"please contact immediately" ,"what " , getApplicationContext() ,HomeActivity.this);
                            notificationsSender.SendNotifications();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                 Double latitude = gpsTracker1.getLatitude();
                 Double longtitude = gpsTracker1.getLongitude();


//                String []phone = {"8733049183" , "9033578258" , "6351536071"};
//                for(int i = 0; i < phone.length; i++){
//                    SmsManager sms=SmsManager.getDefault();
//                    sms.sendTextMessage(phone[i],null,"I am feel uncomfortable please contact immediately using these link"+"https://www.google.com/maps/search/?api=1&query="+latitude+","+longtitude , null,null);
//
//                }


                count = 0;
            }

            return true;
        }

        if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
            ++count;
            if(count == 6){
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentRef = rootRef.child("Women Security").child("User Panel").child("User").child(auth.getUid()).child("Emergency Contact");
                currentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            String token = user.getDeviceToken();
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token , "Hey i am in danger"
                                    ,"Can you help me ?" ,"what " , getApplicationContext() ,HomeActivity.this);
                            notificationsSender.SendNotifications();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Double latitude = gpsTracker1.getLatitude();
                Double longtitude = gpsTracker1.getLongitude();

//
//                String []phone = {"8733049183" , "9033578258" , "6351536071"};
//                for(int i = 0; i < phone.length; i++){
//                    SmsManager sms=SmsManager.getDefault();
//                    sms.sendTextMessage(phone[i],null,"I need your help please click on these link"+"https://www.google.com/maps/search/?api=1&query="+latitude+","+longtitude , null,null);
//
//                }

                count = 0;
            }

            return true;
        }

        return super.dispatchKeyEvent(event);
    }

}