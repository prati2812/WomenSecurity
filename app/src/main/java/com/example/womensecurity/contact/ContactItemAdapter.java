package com.example.womensecurity.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.womensecurity.HomeActivity;
import com.example.womensecurity.R;
import com.example.womensecurity.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactItemAdapter extends RecyclerView.Adapter<ContactItemAdapter.ContactItemHolder> {

    private Context context;
    public ArrayList<User> contactItem;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;


    public ContactItemAdapter(Context context, ArrayList<User> contactItem) {
        this.context = context;
        this.contactItem = contactItem;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<User> filterList){
        contactItem = filterList;
        notifyDataSetChanged();
    }

    public void setContactItem(ArrayList<User> contactItem) {
        this.contactItem = contactItem;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, viewGroup,  false);
        return new ContactItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactItemHolder multiViewHolder, int position) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        multiViewHolder.bind(contactItem.get(position));
    }

    @Override
    public int getItemCount() {
        return contactItem.size();
    }



    public class ContactItemHolder extends RecyclerView.ViewHolder{
         TextView text_name;
         TextView text_phono;
         ImageView imageView;
         CircleImageView userProfile;
        public ContactItemHolder(@NonNull View itemView) {
            super(itemView);

            text_name  =  itemView.findViewById(R.id.user_name);
            text_phono = itemView.findViewById(R.id.user_phonenumber);
            imageView = itemView.findViewById(R.id.add_contact_check_box);
            userProfile = itemView.findViewById(R.id.user_image);
            context = itemView.getContext();
        }

        void bind(User contactItem){
            imageView.setVisibility(contactItem.isSelected() ? View.VISIBLE : View.GONE);
            text_name.setText(contactItem.getUserName());
            text_phono.setText(contactItem.getPhoneNumber());

            Glide.with(context).load(contactItem.getImagePath())
                    .placeholder(R.drawable.person_icon)
                    .into(userProfile);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactItem.setSelected(!contactItem.isSelected());
                    imageView.setVisibility(contactItem.isSelected() ? View.VISIBLE : View.GONE);
                }
            });
        }

    }
    public ArrayList<User> getAll(){
        return contactItem;
    }

    public ArrayList<User> getSelected() {
        ArrayList<User> selected = new ArrayList<>();
        for (int i = 0; i < contactItem.size(); i++) {
            if (contactItem.get(i).isSelected()) {
                selected.add(contactItem.get(i));
            }
        }
        return selected;
    }
}
