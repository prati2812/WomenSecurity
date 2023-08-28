package com.example.womensecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.womensecurity.contact.ContactItem;
import com.example.womensecurity.contact.SelectedContactItemAdapter;
import com.example.womensecurity.databinding.ActivityEmegencyContactDisplayBinding;
import com.example.womensecurity.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.UnknownServiceException;
import java.util.ArrayList;

public class EmegencyContactDisplay extends AppCompatActivity {

    ActivityEmegencyContactDisplayBinding binding;

    RecyclerView recyclerView;
    public ArrayList<User> contactItems;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    SelectedContactItemAdapter selectedContactItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmegencyContactDisplayBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        binding.EmergencyselectedContact.setLayoutManager(new LinearLayoutManager(this));
        contactItems = new ArrayList<>();
        selectedContactItemAdapter = new SelectedContactItemAdapter(this , contactItems);
        binding.EmergencyselectedContact.setAdapter(selectedContactItemAdapter);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentRef = rootRef.child("Women Security").child("User Panel").child("User").child(auth.getUid()).child("Emergency Contact");
        currentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User contactItem = dataSnapshot.getValue(User.class);
                    contactItems.add(contactItem);
                }
                selectedContactItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.toolbarImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmegencyContactDisplay.this , addContact.class));
                return;
            }
        });

    }


}