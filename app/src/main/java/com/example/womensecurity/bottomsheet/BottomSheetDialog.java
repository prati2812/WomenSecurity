package com.example.womensecurity.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.womensecurity.R;
import com.example.womensecurity.user.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BottomSheetDialog extends BottomSheetDialogFragment {


    EditText updateUsername;
    RelativeLayout updateBtn , cancelBtn;

    FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.update_username_bottom_dialog , container , false);

        updateUsername = v.findViewById(R.id.update_userName);
        updateBtn =  v.findViewById(R.id.relativeUpdateBtn);
        cancelBtn = v.findViewById(R.id.relativeCancelBtn);

        firebaseDatabase = FirebaseDatabase.getInstance();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedUserName = updateUsername.getText().toString();
                if(updatedUserName.isEmpty()){
                    updateUsername.setError("Please type a name!");
                    return;
                }

                firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid()).child("User Data")
                        .child("userName").setValue(updatedUserName).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                dismiss();
                            }
                        });

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Retrieve User name
        DatabaseReference ref = firebaseDatabase.getReference().child("Women Security").child("User Panel").child("User").child(FirebaseAuth.getInstance().getUid()).child("User Data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                String userName = user.getUserName();
                updateUsername.setText(userName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
