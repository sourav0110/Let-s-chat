package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.letschat.Adapters.UsersAdapter;
import com.example.letschat.MODELS.User;
import com.example.letschat.databinding.ActivityChatAppMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chatAppMainActivity extends AppCompatActivity {
    ActivityChatAppMainBinding binding;
    FirebaseAuth mAuth;
    ArrayList<User> list=new ArrayList<>();
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        binding=ActivityChatAppMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        UsersAdapter adapter=new UsersAdapter(list,getApplicationContext());
        binding.chatRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User users=dataSnapshot.getValue(User.class);
                    users.getUserId(dataSnapshot.getKey());
                    list.add(users);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.Settings:
                Toast.makeText(chatAppMainActivity.this,"Settings",Toast.LENGTH_LONG).show();
                break;
            case R.id.Logout:
                mAuth.signOut();
                Intent intent=new Intent(chatAppMainActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(chatAppMainActivity.this,"Some error has occured",Toast.LENGTH_LONG).show();

        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAffinity();
    }
}