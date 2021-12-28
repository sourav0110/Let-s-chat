package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.letschat.MODELS.User;
import com.example.letschat.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class signUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        progressDialog=new ProgressDialog(signUpActivity.this);
        progressDialog.setTitle("Creating account");
        progressDialog.setMessage("We are creating your account");

        binding.SignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = binding.UserSignUpEmailET.getText().toString();
                String userPassword=binding.userPasswordSignUpET.getText().toString();
                if (userEmail.equals("") || userPassword.equals("")) {
                    Toast.makeText(signUpActivity.this, "The required field cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(binding.UserSignUpEmailET.getText().toString(), binding.userPasswordSignUpET.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        User user = new User(binding.UsernameSignUpET.getText().toString(), binding.UserSignUpEmailET.getText().toString(), binding.userPasswordSignUpET.getText().toString());
                                        String id = task.getResult().getUser().getUid();
                                        database.getReference().child("Users").child(id).setValue(user);
                                        Toast.makeText(signUpActivity.this, "User created successfully", Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(signUpActivity.this,chatAppMainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(signUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        binding.signIntransferClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signUpActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAffinity();
    }
}