package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.letschat.MODELS.User;
import com.example.letschat.databinding.ActivityChatAppMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class chatAppMainActivity extends AppCompatActivity implements AddContactDialog.AddContactDialogListener {
    ActivityChatAppMainBinding binding;
    FirebaseAuth mAuth;
    ArrayList<User> list = new ArrayList<>();
    FirebaseDatabase database, contactDatabase;
    ArrayList<String> userContacts = new ArrayList<>();

    UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        binding = ActivityChatAppMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        contactDatabase = FirebaseDatabase.getInstance();
        adapter = new UsersAdapter(list, chatAppMainActivity.this);
        binding.chatRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);
       // Log.d("Sourav01", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());


        try {
            contactDatabase.getReference().child("userContacts").child(mAuth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userContacts.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String data = Objects.requireNonNull(dataSnapshot1.getValue(String.class));

                            if (userContacts.contains(data) == false) {

                                userContacts.add(data);
                                Log.d("Sourav01", String.valueOf(userContacts.contains(data)));

                            } else
                                Log.d("Sourav01", "Not added");
                            UserListUpdate();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } catch (Exception e) {
            Toast.makeText(chatAppMainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Settings:
                Toast.makeText(chatAppMainActivity.this, "Settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.Logout:
                mAuth.signOut();
                Intent intent = new Intent(chatAppMainActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.addContact:
                openDialog();
                break;
            default:
                Toast.makeText(chatAppMainActivity.this, "Some error has occured", Toast.LENGTH_LONG).show();

        }
        return true;
    }

    public void UserListUpdate() {
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User users = dataSnapshot.getValue(User.class);
                    users.setUserId(dataSnapshot.getKey());
                    String mail = users.getMail();
                    Log.d("SouravTag", mail);
                    Log.d("SouravTag", String.valueOf(userContacts.contains(mail)));
                    if (!users.getUserId().equals(mAuth.getUid())) {
                        if (userContacts.contains(mail)) {
                            list.add(users);
                        }
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void openDialog() {
        AddContactDialog addContactDialog = new AddContactDialog();
        addContactDialog.show(getSupportFragmentManager(), "AddCustomDialog");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void addContact(String email) {
        if(email.equals(mAuth.getCurrentUser().getEmail())){
            Toast.makeText(chatAppMainActivity.this,"Not allowed to add own email",Toast.LENGTH_LONG).show();
        }else {
           addContactDatabase(email);
        }

    }


    public void addContactDatabase(String email) {

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("mail").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String receiverContactUid="";

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User users = dataSnapshot.getValue(User.class);
                        users.setUserId(dataSnapshot.getKey());
                        receiverContactUid=users.getUserId();
                    }


                   //Toast.makeText(chatAppMainActivity.this, receiverContactUid, Toast.LENGTH_LONG).show();


                    if(!userContacts.contains(email)) { //prevents duplication in own contact list
                        try {
                            final String selfUserID = mAuth.getUid();
                            if (selfUserID != null) {
                                String finalReceiverContactUid = receiverContactUid; /*The Sender receiver uid mechanism avoid duplication in the receiver side contact */
                                HashMap<String,String> mHashmap=new HashMap<>();
                                mHashmap.put("contactID",email);
                                FirebaseDatabase.getInstance().getReference("userContacts").child(selfUserID).child(receiverContactUid)
                                        .setValue(mHashmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (finalReceiverContactUid != null) {
                                            HashMap<String, String> mHashmap1 = new HashMap<>();
                                            mHashmap1.put("contactID",mAuth.getCurrentUser().getEmail() );
                                            FirebaseDatabase.getInstance().getReference("userContacts").child(finalReceiverContactUid).child(selfUserID).setValue(mHashmap1);
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Toast.makeText(chatAppMainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(chatAppMainActivity.this, "Already present in contact", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(chatAppMainActivity.this, email + "is not " +
                            "present", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        String currentId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("presence").child(currentId).setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("presence").child(currentId).setValue("offline");
    }

}
