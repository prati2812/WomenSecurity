package com.example.womensecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.womensecurity.databinding.ActivityUserFullImageBinding;
import com.example.womensecurity.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class userFullImage extends AppCompatActivity {


    ActivityUserFullImageBinding binding;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserFullImageBinding.inflate(getLayoutInflater());
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
                    Glide.with(userFullImage.this).load(imagePath)
                            .placeholder(R.drawable.person_icon)
                            .into(binding.userImage);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}