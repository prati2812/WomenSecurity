package com.example.womensecurity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.womensecurity.databinding.ActivityUserProfileBinding;
import com.example.womensecurity.user.User;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class userProfile extends AppCompatActivity {

    ActivityUserProfileBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    private static final int STORAGE_PERMISSION_CODE = 101;
    Uri selectedImage;


    public String previous_imagePath = null;


    String deviceToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        // Retrieve Exisiting Data
        try{
            // Retrieve Data
            DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                    .child("User Data");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()){
                        User user = snapshot.getValue(User.class);
                        previous_imagePath = user.getImagePath();
                        String userName = user.getUserName();
                        Glide.with(userProfile.this).load(previous_imagePath)
                                .placeholder(R.drawable.person_icon)
                                .into(binding.userProfileImage);

                        binding.editprofileName.setText(userName);

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            Log.d("userProfileError" , String.valueOf(e.getMessage()));
        }


        binding.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE , STORAGE_PERMISSION_CODE);
            }
        });


        binding.editprofileName.requestFocus();
        binding.editprofileName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){

                    String userName = binding.editprofileName.getText().toString();

                    if(userName.isEmpty()){
                        binding.editprofileName.setError("Please type a name!");
                        binding.editprofileName.requestFocus();
                    }

                    binding.setupProfileBtn.setVisibility(View.GONE);
                    binding.progressBar1.setVisibility(View.VISIBLE);


                    if(selectedImage != null){

                        Bitmap bmp = null;
                        try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 45, baos);
                        byte[] fileInBytes = baos.toByteArray();

                        StorageReference storageReference = firebaseStorage.getReference().child("Women Security").child("User Profile").child(auth.getUid());
                        storageReference.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imagePath = uri.toString();
                                            String userName  = binding.editprofileName.getText().toString();
                                            String userId = auth.getUid();
                                            String phoneNumber =  auth.getCurrentUser().getPhoneNumber();


                                            // store user data
                                            FirebaseInstanceId.getInstance().getInstanceId()
                                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                            if (task.isSuccessful()) {
                                                                deviceToken = Objects.requireNonNull(task.getResult()).getToken();

                                                                User user = new User(userName , phoneNumber , userId , imagePath  , deviceToken);

                                                                // Store Data
                                                                firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(auth.getUid())
                                                                        .child("User Data")
                                                                        .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                binding.progressBar1.setVisibility(View.GONE);
                                                                                binding.setupProfileBtn.setVisibility(View.VISIBLE);
                                                                                Intent i = new Intent(userProfile.this , HomeActivity.class);
                                                                                startActivity(i);
                                                                                finish();
                                                                            }
                                                                        });
                                                            }

                                                        }
                                                    });

                                        }
                                    });
                                }

                            }
                        });
                    }
                    else
                    {
                        String userName1  = binding.editprofileName.getText().toString();
                        String userId = auth.getUid();
                        String phoneNumber =  auth.getCurrentUser().getPhoneNumber();



                        DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                                .child("User Data");

                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChildren()){
                                    User user = snapshot.getValue(User.class);
                                    previous_imagePath = user.getImagePath();

                                    if(previous_imagePath != null){


                                        // store exisiting data
                                        FirebaseInstanceId.getInstance().getInstanceId()
                                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                        if (task.isSuccessful()) {
                                                            deviceToken = Objects.requireNonNull(task.getResult()).getToken();

                                                            User user = new User(userName1 , phoneNumber , userId , previous_imagePath , deviceToken);

                                                            Glide.with(userProfile.this).load(previous_imagePath)
                                                                    .placeholder(R.drawable.person_icon)
                                                                    .into(binding.userProfileImage);

                                                            firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(auth.getUid()).child("User Data")
                                                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            binding.progressBar1.setVisibility(View.GONE);
                                                                            binding.setupProfileBtn.setVisibility(View.VISIBLE);
                                                                            startActivity(new Intent(userProfile.this , HomeActivity.class));
                                                                            finish();
                                                                        }
                                                                    });

                                                        }

                                                    }
                                                });

                                    }
                                    else{


                                        FirebaseInstanceId.getInstance().getInstanceId()
                                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                        if (task.isSuccessful()) {
                                                            deviceToken = Objects.requireNonNull(task.getResult()).getToken();

                                                            User user = new User(userName1 , phoneNumber , userId , "No image" , deviceToken);

                                                            // Store Data
                                                            firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(auth.getUid()).child("User Data")
                                                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            binding.progressBar1.setVisibility(View.GONE);
                                                                            binding.setupProfileBtn.setVisibility(View.VISIBLE);
                                                                            startActivity(new Intent(userProfile.this , HomeActivity.class));
                                                                            finish();
                                                                        }
                                                                    });

                                                        }

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

                    return true;
                }

                return false;
            }
        });

        binding.setupProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = binding.editprofileName.getText().toString();

                if(userName.isEmpty()){
                    binding.editprofileName.setError("Please type a name!");
                    return;
                }

                binding.setupProfileBtn.setVisibility(View.GONE);
                binding.progressBar1.setVisibility(View.VISIBLE);

                if(selectedImage != null){

                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] fileInBytes = baos.toByteArray();

                    StorageReference storageReference = firebaseStorage.getReference().child("Women Security").child("User Profile").child(auth.getUid());
                    storageReference.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                               if(task.isSuccessful()){
                                   storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                       @Override
                                       public void onSuccess(Uri uri) {
                                              String imagePath = uri.toString();
                                              String userName  = binding.editprofileName.getText().toString();
                                              String userId = auth.getUid();
                                              String phoneNumber =  auth.getCurrentUser().getPhoneNumber();



                                           // store user data
                                           FirebaseInstanceId.getInstance().getInstanceId()
                                                   .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                           if (task.isSuccessful()) {
                                                               deviceToken = Objects.requireNonNull(task.getResult()).getToken();

                                                               User user = new User(userName , phoneNumber , userId , imagePath , deviceToken);

                                                               // Store Data
                                                               firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(auth.getUid())
                                                                       .child("User Data")
                                                                       .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void unused) {
                                                                               binding.progressBar1.setVisibility(View.GONE);
                                                                               binding.setupProfileBtn.setVisibility(View.VISIBLE);
                                                                               Intent i = new Intent(userProfile.this , HomeActivity.class);
                                                                               startActivity(i);
                                                                               finish();
                                                                           }
                                                                       });

                                                           }

                                                       }
                                                   });



                                       }
                                   });
                               }

                        }
                    });
                }
                else{
                    String userName1  = binding.editprofileName.getText().toString();
                    String userId = auth.getUid();
                    String phoneNumber =  auth.getCurrentUser().getPhoneNumber();


                    DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                            .child("User Data");

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                User user = snapshot.getValue(User.class);
                                previous_imagePath = user.getImagePath();

                                if(previous_imagePath != null){


                                    // store exisiting data
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (task.isSuccessful()) {
                                                        deviceToken = Objects.requireNonNull(task.getResult()).getToken();

                                                        User user = new User(userName1 , phoneNumber , userId , previous_imagePath , deviceToken);

                                                        Glide.with(userProfile.this).load(previous_imagePath)
                                                                .placeholder(R.drawable.person_icon)
                                                                .into(binding.userProfileImage);

                                                        firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(auth.getUid()).child("User Data")
                                                                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        binding.progressBar1.setVisibility(View.GONE);
                                                                        binding.setupProfileBtn.setVisibility(View.VISIBLE);
                                                                        startActivity(new Intent(userProfile.this , HomeActivity.class));
                                                                        finish();
                                                                    }
                                                                });

                                                    }

                                                }
                                            });

                                }
                                else{


                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (task.isSuccessful()) {
                                                        deviceToken = Objects.requireNonNull(task.getResult()).getToken();

                                                        User user = new User(userName1 , phoneNumber , userId , "No image" , deviceToken);

                                                        // Store Data
                                                        firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(auth.getUid()).child("User Data")
                                                                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        binding.progressBar1.setVisibility(View.GONE);
                                                                        binding.setupProfileBtn.setVisibility(View.VISIBLE);
                                                                        startActivity(new Intent(userProfile.this , HomeActivity.class));
                                                                        finish();
                                                                    }
                                                                });

                                                    }

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
        });

        if(!isConnected(this)){
            showInternetDialog();
        }

    }


    private void checkPermission(String permission , int permission_code){
          if(ContextCompat.checkSelfPermission(userProfile.this , permission) == PackageManager.PERMISSION_DENIED){
              ActivityCompat.requestPermissions(userProfile.this , new String[]{permission} , permission_code);
          }
          else{
              galleryIntent();
          }
    }

    private  void galleryIntent(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i , STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                galleryIntent();
            }else{
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE , STORAGE_PERMISSION_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
             if(data.getData() != null){
                 binding.userProfileImage.setImageURI(data.getData());
                 selectedImage = data.getData();
             }

        }

    }

    private void showInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.nointernetconnection, findViewById(R.id.no_internet_connection_layout));
        view.findViewById(R.id.relative_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(userProfile.this)) {
                    showInternetDialog();

                } else {
                    startActivity(new Intent(getApplicationContext(), userProfile.class));
                    finish();
                }
            }
        });

        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private boolean isConnected(userProfile userProfile1) {
        ConnectivityManager connectivityManager = (ConnectivityManager) userProfile1.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User")
                .child(auth.getUid()).child("Emergency Contact").setValue(null);

     }


}