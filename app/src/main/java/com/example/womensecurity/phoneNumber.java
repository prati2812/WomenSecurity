package com.example.womensecurity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.womensecurity.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class phoneNumber extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());


            binding.editPhoneNumber.requestFocus();

            binding.editPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){

                            if(TextUtils.isEmpty(binding.editPhoneNumber.getText().toString().trim())){
                                binding.editPhoneNumber.setError("Please enter the phone number");
                                binding.editPhoneNumber.requestFocus();
                            }
                            else{
                                String mobile_number = binding.editPhoneNumber.getText().toString().trim();
                                Intent intent = new Intent(phoneNumber.this , otpActivity.class);
                                intent.putExtra("mobile_Number" , mobile_number);
                                startActivity(intent);
                            }

                        return true;
                    }
                    return false;
                }
            });


        binding.phoneNumberContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.editPhoneNumber.getText().toString())){
                    binding.editPhoneNumber.setError("Please enter the phone number");
                    binding.editPhoneNumber.requestFocus();
                }
                else{
                    String mobile_number = binding.editPhoneNumber.getText().toString();
                    Intent intent = new Intent(phoneNumber.this , otpActivity.class);
                    intent.putExtra("mobile_Number" , mobile_number);
                    startActivity(intent);
                }

            }
        });


        if(!isConnected(this)){
            showInternetDialog();
        }

    }


    private void showInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.nointernetconnection, findViewById(R.id.no_internet_connection_layout));
        view.findViewById(R.id.relative_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(phoneNumber.this)) {
                    showInternetDialog();

                } else {
                    startActivity(new Intent(getApplicationContext(), phoneNumber.class));
                    finish();
                }
            }
        });

        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private boolean isConnected(phoneNumber phoneNumber1) {
        ConnectivityManager connectivityManager = (ConnectivityManager) phoneNumber1.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }



}