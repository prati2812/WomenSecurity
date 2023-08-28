package com.example.womensecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.womensecurity.bottomsheet.BottomSheetDialog;
import com.example.womensecurity.bottomsheet.updateProfileSheet;
import com.example.womensecurity.databinding.ActivityShowProfileBinding;
import com.example.womensecurity.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class showProfile extends AppCompatActivity {

    ActivityShowProfileBinding binding;

    FirebaseDatabase firebaseDatabase;

    BottomSheetDialog deleteAccountSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowProfileBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();

        binding.toolbarImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Retrieve User name
        DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                .child("User Data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                String userName = user.getUserName();
                binding.updateUserName.setText(userName);
                binding.updateUserName.setEnabled(false);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.updateMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.show(getSupportFragmentManager() , "");
            }
        });

        binding.toolbarUserImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(showProfile.this , userFullImage.class));
                return;
            }
        });


       // Delete Account
       binding.relativeDeleteBtnLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               com.google.android.material.bottomsheet.BottomSheetDialog bottomSheet =
                       new com.google.android.material.bottomsheet.BottomSheetDialog(showProfile.this);
               View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.delete_user_account_bottom_dialog , null);

               v.findViewById(R.id.relativeDeleteAccountBtn).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                      v.findViewById(R.id.relativeDeleteAccountBtn).setVisibility(View.GONE);
                      v.findViewById(R.id.relativeProgressBtn).setVisibility(View.VISIBLE);
                      FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                  v.findViewById(R.id.relativeDeleteAccountBtn).setVisibility(View.VISIBLE);
                                  v.findViewById(R.id.relativeProgressBtn).setVisibility(View.GONE);
                                  startActivity(new Intent(showProfile.this , SplashScreen.class));
                                  finish();
                              }
                              else {
                                  Toast.makeText(showProfile.this, "Try Again later", Toast.LENGTH_SHORT).show();
                              }
                          }
                      });
                   }
               });

               v.findViewById(R.id.relativeCancelAccountBtn).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       bottomSheet.dismiss();
                   }
               });

               bottomSheet.setContentView(v);
               bottomSheet.show();
           }
       });

       binding.updateUserProfileBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               updateProfileSheet ups = new updateProfileSheet();
               ups.show(getSupportFragmentManager() , "");
           }
       });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Retrieve Image
        DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid())
                .child("User Data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    User user = snapshot.getValue(User.class);
                    String imagePath = user.getImagePath();
                    Glide.with(showProfile.this).load(imagePath)
                            .placeholder(R.drawable.person_icon)
                            .into(binding.toolbarUserImage1);
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
        startActivity(new Intent(this , HomeActivity.class));
        return;
    }
}