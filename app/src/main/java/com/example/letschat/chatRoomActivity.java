package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class chatRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_room);
    }
}