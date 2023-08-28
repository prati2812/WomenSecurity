package com.example.womensecurity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.womensecurity.databinding.ActivitySplashScreenBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    ActivitySplashScreenBinding binding;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               if(firebaseAuth.getCurrentUser()  == null){
                   startActivity(new Intent(SplashScreen.this , phoneNumber.class));
                   finish();
               }
               else{
                   startActivity(new Intent(SplashScreen.this , HomeActivity.class));
                   finish();
               }

            }
        },2000);
    }
}