package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.letschat.Adapters.UsersAdapter;
import com.example.letschat.MODELS.User;
import com.example.letschat.databinding.ActivityChatAppMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chatAppMainActivity extends AppCompatActivity {
    ActivityChatAppMainBinding binding;
    FirebaseAuth mAuth;
    ArrayList<User> list=new ArrayList<>();
    FirebaseDatabase database,contactDatabase;
    ArrayList<String> userContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        binding=ActivityChatAppMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        contactDatabase=FirebaseDatabase.getInstance();
        UsersAdapter adapter=new UsersAdapter(list,getApplicationContext());
        binding.chatRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        Log.d("Sourav01",mAuth.getCurrentUser().getUid().toString());
        userContacts.clear();
        try {
            contactDatabase.getReference().child("userContacts").child(mAuth.getCurrentUser().getUid().toString()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String data = snapshot.getValue().toString();
                    if(userContacts.contains(data)==false) {

                        userContacts.add(data);
                        Log.d("Sourav01", String.valueOf(userContacts.contains(data)));

                    }
                    else
                        Log.d("Sourav01","Not added");

                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });





        }catch (Exception e){
            Toast.makeText(chatAppMainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User users=dataSnapshot.getValue(User.class);
                    users.getUserId(dataSnapshot.getKey());
                    //Log.d("Sourav",users.getMail());

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