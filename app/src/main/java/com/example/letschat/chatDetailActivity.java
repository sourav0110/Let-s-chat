package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.letschat.databinding.ActivityChatDetailBinding;

public class chatDetailActivity extends AppCompatActivity {
    ActivityChatDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());
    }
}