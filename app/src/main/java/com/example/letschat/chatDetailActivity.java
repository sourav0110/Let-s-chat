package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.letschat.MODELS.MessagesModel;
import com.example.letschat.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class chatDetailActivity extends AppCompatActivity {
    ActivityChatDetailBinding binding;
    FirebaseDatabase database,db1;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        db1=FirebaseDatabase.getInstance();

        final String senderId=mAuth.getCurrentUser().getUid();
        String receiveId=getIntent().getStringExtra("userId");
        String userName=getIntent().getStringExtra("userName");
        String profilePic=getIntent().getStringExtra("userProfilePic");
        binding.usernameChatDetails.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.user).into(binding.profileImageChatDetails);
        final String senderRoom =senderId+receiveId;
        final String receiverRoom = receiveId+senderId;
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(chatDetailActivity.this,chatAppMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        final ArrayList<MessagesModel> messagesModels=new ArrayList<>();
        final ChatAdapter chatAdapter=new ChatAdapter(messagesModels,chatDetailActivity.this,senderRoom,receiverRoom);
        binding.chatDetailsRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(chatDetailActivity.this);
        binding.chatDetailsRecyclerView.setLayoutManager(linearLayoutManager);
        binding.chatDetailsRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount());

        db1.getReference().child("Chats").child(senderRoom).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesModels.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    MessagesModel model=snapshot1.getValue(MessagesModel.class);
                    model.setMessageId(snapshot1.getKey());
                    messagesModels.add(model);

                }
                chatAdapter.notifyDataSetChanged();
                binding.chatDetailsRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        try {
            binding.sendButtonChatDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = binding.editTextChatDetail.getText().toString();
                    // newly added for emogi
                    String randomKey = database.getReference().push().getKey();
                    if (!message.equals("")) {
                        final MessagesModel model = new MessagesModel(senderId, message);
                        model.setTimestamp(new Date().getTime());
                        model.setMessageId(randomKey);
                        binding.editTextChatDetail.setText("");
                        database.getReference().child("Chats")
                                .child(senderRoom)
                                .child(randomKey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("Chats").child(receiverRoom).child(randomKey).setValue(model).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        }
                                );
                            }
                        });
                    }
                }
            });
        }
        catch (Exception e){
            Toast.makeText(chatDetailActivity.this, Objects.requireNonNull(e.getMessage()).toString(),Toast.LENGTH_LONG).show();
        }




    }


}