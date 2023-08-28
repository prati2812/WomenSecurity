package com.example.womensecurity.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.womensecurity.R;
import com.example.womensecurity.databinding.EmergencyContactListHorizontallyBinding;
import com.example.womensecurity.user.User;

import java.util.ArrayList;

public class SelectedContactItemHorizontally extends RecyclerView.Adapter<SelectedContactItemHorizontally.ViewHolder> {

    Context context;
    public ArrayList<User> contactItemArrayList;

    public SelectedContactItemHorizontally(Context context, ArrayList<User> contactItemArrayList) {
        this.context = context;
        this.contactItemArrayList = contactItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.emergency_contact_list_horizontally , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         User contactItem = contactItemArrayList.get(position);
         holder.binding.userName.setText(contactItem.getUserName());
         Glide.with(context).load(contactItem.getImagePath())
                 .placeholder(R.drawable.person_icon)
                 .into(holder.binding.userImage);

    }

    @Override
    public int getItemCount() {
        return contactItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        EmergencyContactListHorizontallyBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = EmergencyContactListHorizontallyBinding.bind(itemView);
        }
    }
}
