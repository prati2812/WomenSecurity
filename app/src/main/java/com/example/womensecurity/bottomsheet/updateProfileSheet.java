package com.example.womensecurity.bottomsheet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.womensecurity.HomeActivity;
import com.example.womensecurity.R;
import com.example.womensecurity.user.User;
import com.example.womensecurity.userProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class updateProfileSheet extends BottomSheetDialogFragment {

    CircleImageView updateProfile;
    RelativeLayout updateprofilebtn;

    private static final int STORAGE_PERMISSION_CODE = 101;
    Uri selectedImage;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    String userName;

    ProgressBar progressbarupdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.update_user_profile , container , false);

        updateProfile = v.findViewById(R.id.userProfile);
        updateprofilebtn =  v.findViewById(R.id.update_profile_btn);

        progressbarupdate = v.findViewById(R.id.progressBar_update);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE , STORAGE_PERMISSION_CODE);
            }
        });



        updateprofilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedImage != null){
                    updateprofilebtn.setVisibility(View.GONE);
                    progressbarupdate.setVisibility(View.VISIBLE);
                    StorageReference storageReference = firebaseStorage.getReference().child("Women Security").child("User Profile").child(auth.getUid());
                       storageReference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                 if(task.isSuccessful()){
                                     storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                         @Override
                                         public void onSuccess(Uri uri) {
                                              String imagePath = uri.toString();
                                             firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                                                     .child("User Data")
                                                     .child("imagePath").setValue(imagePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                         @Override
                                                         public void onSuccess(Void unused) {
                                                             updateprofilebtn.setVisibility(View.VISIBLE);
                                                             progressbarupdate.setVisibility(View.GONE);
                                                             dismiss();
                                                         }
                                                     });
                                         }
                                     });
                                 }
                           }
                       });
                }
            }
        });

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Retrieve Image
        DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid()).child("User Data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    User user = snapshot.getValue(User.class);
                    String imagePath = user.getImagePath();
                    Glide.with(updateProfileSheet.this).load(imagePath)
                            .placeholder(R.drawable.person_icon)
                            .into(updateProfile);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkPermission(String permission , int permission_code){

        if(ContextCompat.checkSelfPermission(getActivity() , permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity() , new String[]{permission} , permission_code);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(data.getData() != null){
                updateProfile.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }

    }
}
