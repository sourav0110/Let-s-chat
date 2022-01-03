package com.example.letschat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.letschat.MODELS.MessagesModel;
import com.example.letschat.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class chatDetailActivity extends AppCompatActivity {
    ActivityChatDetailBinding binding;
    FirebaseDatabase database,db1;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderRoom,receiverRoom;
    String senderId,receiveId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        db1=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        dialog=new ProgressDialog(chatDetailActivity.this);
        dialog.setMessage("Uploading image");
        dialog.setCancelable(false);
        senderId=mAuth.getCurrentUser().getUid();
        receiveId=getIntent().getStringExtra("userId");
        String userName=getIntent().getStringExtra("userName");
        String profilePic=getIntent().getStringExtra("userProfilePic");
        binding.usernameChatDetails.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.ugr).into(binding.profileImageChatDetails);
        senderRoom =senderId+receiveId;
        receiverRoom = receiveId+senderId;
        ArrayList<MessagesModel> messagesModels=new ArrayList<>();
        final ChatAdapter chatAdapter=new ChatAdapter(messagesModels,chatDetailActivity.this,senderRoom,receiverRoom);
        binding.chatDetailsRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(chatDetailActivity.this);
        binding.chatDetailsRecyclerView.setLayoutManager(linearLayoutManager);
        binding.chatDetailsRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
        FirebaseDatabase.getInstance().getReference().child("presence").child(receiveId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status=snapshot.getValue(String.class);
                    if(!status.isEmpty())
                        binding.currentUserStatus.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(chatDetailActivity.this,chatAppMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
        final Handler handler=new Handler();

        binding.editTextChatDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                FirebaseDatabase.getInstance().getReference().child("presence").child(senderId).setValue("typing..");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);
            }
            Runnable userStoppedTyping=new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference().child("presence").child(senderId).setValue("online");
                }
            };
        });
        binding.attachmentChatDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                galleryPicker.launch(intent);
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
                        model.setMessageType("text");
                        model.setImageUrl("noUrl");
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
    private ActivityResultLauncher<Intent> galleryPicker=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){

                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data=result.getData();
                        Uri imageUri=data.getData();
                        if(imageUri !=null){
                            String randomKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                            StorageReference reference=storage.getReference().child("chats").child(randomKey);
                            dialog.show();
                            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                dialog.dismiss();
                                              String filePath=uri.toString();
                                              final MessagesModel model = new MessagesModel(senderId, "");
                                              model.setTimestamp(new Date().getTime());
                                              model.setMessageType("pic");
                                              model.setImageUrl(filePath);
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
                                        });
                                    }
                                }
                            });
                        }


                    }else{
                        Toast.makeText(chatDetailActivity.this,"Cancelled",Toast.LENGTH_LONG).show();
                    }
                }
            }
    );
    @Override
    protected void onResume() {
        super.onResume();
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