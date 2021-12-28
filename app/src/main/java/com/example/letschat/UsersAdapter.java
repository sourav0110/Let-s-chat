package com.example.letschat;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.MODELS.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.viewHolder>{

    ArrayList<User> list;
    Context context;

    public UsersAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User users=list.get(position);
        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.ugr).into(holder.image);
        holder.username.setText(users.getUserName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),chatDetailActivity.class);
                intent.putExtra("userId",users.getUserId());
                intent.putExtra("userName",users.getUserName());
                intent.putExtra("userProfilePic",users.getProfilepic());
                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView  username,lastMessage;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.profile_image);
            username=itemView.findViewById(R.id.userNameList);
            lastMessage=itemView.findViewById(R.id.lastMessage);

        }
    }
}