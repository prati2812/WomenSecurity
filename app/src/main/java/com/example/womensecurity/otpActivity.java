package com.example.womensecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.womensecurity.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class otpActivity extends AppCompatActivity {

    ActivityOtpBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    String verificationId;
    String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Intent i = getIntent();
         mobileNumber = "+91"+i.getStringExtra("mobile_Number");

        SendVerficationCode(mobileNumber);




        binding.editVerificationCode.requestFocus();
        binding.editVerificationCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){

                    if(TextUtils.isEmpty(binding.editVerificationCode.getText().toString().trim())){
                        binding.editVerificationCode.setError("Please Enter the code");
                        binding.editVerificationCode.requestFocus();
                    }
                    else{
                        binding.verifyOtpBtn.setVisibility(View.GONE);
                        binding.progressBar1.setVisibility(View.VISIBLE);
                        verifyCode(binding.editVerificationCode.getText().toString());
                    }

                    return true;
                }

                return false;
            }
        });

        binding.verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.editVerificationCode.getText().toString().trim())){
                    binding.editVerificationCode.setError("Please Enter the code");
                    binding.editVerificationCode.requestFocus();
                }
                else{
                    binding.verifyOtpBtn.setVisibility(View.GONE);
                    binding.progressBar1.setVisibility(View.VISIBLE);
                    verifyCode(binding.editVerificationCode.getText().toString());
                }
            }
        });

        if(!isConnected(this)){
            showInternetDialog();
        }

    }

    private void SendVerficationCode(String mobileNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setActivity(this)
                .setPhoneNumber(mobileNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(mCallback)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            final String code = phoneAuthCredential.getSmsCode();

            if(code != null){
                binding.editVerificationCode.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(otpActivity.this , "Please again try later.." , Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId , code);

        signWithCredential(credential);
    }

    private void signWithCredential(PhoneAuthCredential credential) {

         mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        binding.progressBar1.setVisibility(View.GONE);
                        binding.verifyOtpBtn.setVisibility(View.VISIBLE);
                        Intent i = new Intent(otpActivity.this ,userProfile.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        binding.progressBar1.setVisibility(View.GONE);
                        binding.verifyOtpBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(otpActivity.this , "Enter correct otp" , Toast.LENGTH_LONG).show();
                        binding.editVerificationCode.requestFocus();
                        return;
                    }
             }

         });
    }

    private void showInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.nointernetconnection, findViewById(R.id.no_internet_connection_layout));
        view.findViewById(R.id.relative_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(otpActivity.this)) {
                    showInternetDialog();

                } else {
                    startActivity(new Intent(getApplicationContext(), otpActivity.class));
                    finish();
                }
            }
        });

        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private boolean isConnected(otpActivity otpActivity1) {
        ConnectivityManager connectivityManager = (ConnectivityManager) otpActivity1.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this , phoneNumber.class));
        return;
    }
}