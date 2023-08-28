package com.example.womensecurity.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.womensecurity.R;
import com.example.womensecurity.databinding.ContactListItemBinding;
import com.example.womensecurity.showProfile;
import com.example.womensecurity.user.User;

import java.util.ArrayList;

public class SelectedContactItemAdapter extends RecyclerView.Adapter<SelectedContactItemAdapter.ViewHolder> {

    Context context;
    ArrayList<User> contactList;

    public SelectedContactItemAdapter(Context context, ArrayList<User> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       User contactItem = contactList.get(position);
       holder.binding.userName.setText(contactItem.getUserName());
       holder.binding.userPhonenumber.setText(contactItem.getPhoneNumber());
        Glide.with(context).load(contactItem.getImagePath())
                .placeholder(R.drawable.person_icon)
                .into(holder.binding.userImage);

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ContactListItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ContactListItemBinding.bind(itemView);
        }
    }
}
