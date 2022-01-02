package com.example.letschat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.letschat.MODELS.MessagesModel;
import com.example.letschat.MODELS.User;
import com.example.letschat.databinding.ActivitySettingsBinding;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class settingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        storage = FirebaseStorage.getInstance();
        mAuth=FirebaseAuth.getInstance();
        dialog = new ProgressDialog(settingsActivity.this);
        dialog.setMessage("Uploading image");
        dialog.setCancelable(false);
        binding.plusButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                galleryPicker.launch(intent);
            }
        });
        binding.SettingsAddUserdetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=binding.UsernameSettingsET.getText().toString();
                String status=binding.UserAboutSettingsET.getText().toString();
                HashMap<String, Object> map=new HashMap<>();
                map.put("userName",username);
                map.put("userStatus",status);
                FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid())
                        .updateChildren(map);
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfilepic()).placeholder(R.drawable.ugr).into(binding.profileImageSetting);
                        binding.UsernameSettingsET.setText(user.getUserName());
                        binding.UserAboutSettingsET.setText(user.getUserStatus());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private ActivityResultLauncher<Intent> galleryPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        if (imageUri != null) {

                            //StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                            dialog.show();
                            binding.profileImageSetting.setImageURI(imageUri);
                            final StorageReference reference = storage.getReference().child("profilePic").child(mAuth.getUid());
                            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    dialog.dismiss();
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("profilepic").setValue(uri.toString());
                                        }
                                    });
                                }
                            });



                        } else {
                            Toast.makeText(settingsActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                        }
                    }
                }

            });
}

