package com.example.womensecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.womensecurity.contact.ContactItemAdapter;

import com.example.womensecurity.contact.SelectedContactItemHorizontally;
import com.example.womensecurity.databinding.ActivityAddContactBinding;
import com.example.womensecurity.user.User;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class addContact extends AppCompatActivity {

    ActivityAddContactBinding binding;

    private ArrayList<User> contactItems = new ArrayList<>();
    private ContactItemAdapter contactItemAdapter;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;

    ImageView imageView;



    private final ArrayList<User> selectedContact = new ArrayList<>();
    private SelectedContactItemHorizontally selectedContactItemHorizontally;

    private static final int STORAGE_PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());


        imageView = findViewById(R.id.add_contact_check_box);



        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        binding.listContact.setLayoutManager(new LinearLayoutManager(this));
        contactItemAdapter = new ContactItemAdapter(this , contactItems);
        binding.listContact.setAdapter(contactItemAdapter);



        // Selected Contact
        binding.contactSelected.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));
        selectedContactItemHorizontally = new SelectedContactItemHorizontally(this , selectedContact);
        binding.contactSelected.setAdapter(selectedContactItemHorizontally);






        // Retrieve Contact
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentRef = rootRef.child("Women Security").child("User Panel").child("User").child(Objects.requireNonNull(auth.getUid())).child("Emergency Contact");
        currentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedContact.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User contactItem = dataSnapshot.getValue(User.class);
                    selectedContact.add(contactItem);
                }
                selectedContactItemHorizontally.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        binding.toolbarImageBack.setOnClickListener(view -> finish());

//        binding.fabNextAddContactComplete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (contactItemAdapter.getSelected().size() > 0) {
//                    firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User")
//                            .child(auth.getUid()).child("Emergency Contact").setValue(null);
//                    for (int i = 0; i < contactItemAdapter.getSelected().size()-1; i++) {
//
//                        String userId = firebaseDatabase.getReference().push().getKey();
//                        String contact_name = contactItemAdapter.getSelected().get(i).getContact_name();
//                        String contact_phoneNo = contactItemAdapter.getSelected().get(i).getContact_no();
//                        boolean selected = true;
//
//
//                        ContactItem contactItem = new ContactItem(contact_name , contact_phoneNo , selected);
//
//                        firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User")
//                                .child(auth.getUid()).child("Emergency Contact").child(userId).setValue(contactItem);
//                    }
//                    String contact_name = contactItemAdapter.getSelected().get(contactItemAdapter.getSelected().size() - 1).getContact_name();
//                    String contact_phoneNo = contactItemAdapter.getSelected().get(contactItemAdapter.getSelected().size() - 1).getContact_no();
//                    boolean selected = true;
//
//                    String userId = firebaseDatabase.getReference().push().getKey();
//                    ContactItem contactItem = new ContactItem(contact_name ,contact_phoneNo , selected);
//                    firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User")
//                            .child(auth.getUid()).child("Emergency Contact").child(userId).setValue(contactItem).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                      if(task.isSuccessful()){
//                                          startActivity(new Intent(addContact.this , EmegencyContactDisplay.class));
//                                          finish();
//                                          return;
//                                      }
//                                      else{
//                                          Toast.makeText(addContact.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                                      }
//                                }
//                            });
//
//                } else {
//                    showToast("No Selection");
//                }
//            }
//        });

        binding.fabNextAddContactComplete.setOnClickListener(view -> {
            DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
            DatabaseReference currentRef1 = rootRef1.child("Women Security").child("User Panel").child("User");
            currentRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        currentRef1.child(Objects.requireNonNull(dataSnapshot.getKey())).child("User Data").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                String phoneNumberDatabase = user.getPhoneNumber();
                                String deviceToken = user.getDeviceToken();
                                String userId = user.getUserId();
                                String imagePath = user.getImagePath();

                                if(contactItemAdapter.getSelected().size() > 0){

                                    firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User")
                                    .child(auth.getUid()).child("Emergency Contact").setValue(null);

                                    for(int i = 0; i < contactItemAdapter.getSelected().size(); i++){
                                        String contact_name = contactItemAdapter.getSelected().get(i).getUserName();
                                        String contact_no  = contactItemAdapter.getSelected().get(i).getPhoneNumber();
                                        if(phoneNumberDatabase.equals(contact_no)){
                                             User user1 = new User(contact_name , contact_no , userId , imagePath , deviceToken);

                                             firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User")
                                                     .child(auth.getUid()).child("Emergency Contact").child(userId).setValue(user1);
                                        }
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
        });

        binding.searchContact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

    }

    // Search Contact
    private void filter(String text){
        ArrayList<User> filteredList = new ArrayList<User>();
        for(User item : contactItems) {
            if (item.getUserName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
            else if(item.getUserName().toUpperCase().contains(text.toUpperCase())){
                filteredList.add(item);
            }
        }

        contactItemAdapter.filterList(filteredList);

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(Manifest.permission.READ_CONTACTS , STORAGE_PERMISSION_CODE);
    }

    private void checkPermission(String permission , int permission_code){
        if(ContextCompat.checkSelfPermission(addContact.this , permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(addContact.this , new String[]{permission} , permission_code);
        }
        else{
            ReadContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ReadContact();
                }else{
                    checkPermission(Manifest.permission.READ_CONTACTS , STORAGE_PERMISSION_CODE);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    void ReadContact(){
//        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//        while(cursor.moveToNext()){
//            User contactItem = new User();
//            @SuppressLint("Range") String name= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            @SuppressLint("Range") String phoneNumber1 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            contactItem.setUserName(name);
//            contactItem.setPhoneNumber(phoneNumber1);
//            contactItems.add(contactItem);
//        }
//
//        contactItemAdapter.setContactItem(contactItems);
//        contactItemAdapter.notifyDataSetChanged();

        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentRef1 = rootRef1.child("Women Security").child("User Panel").child("User");
        currentRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                  for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                      currentRef1.child(dataSnapshot.getKey()).child("User Data").addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {

                              for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                  User contact = new User();
                                  User user = dataSnapshot1.getValue(User.class);
                                  String userName = user.getUserName();
                                  String userPhoneNumber = user.getPhoneNumber();
                                  contact.setUserName(String.valueOf(userName));
                                  contact.setPhoneNumber(userPhoneNumber);
                                  contactItems.add(contact);
                              }
                              contactItemAdapter.setContactItem(contactItems);
                              contactItemAdapter.notifyDataSetChanged();
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
        startActivity(new Intent(this , EmegencyContactDisplay.class));
    }


}