package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.letschat.databinding.ActivityChatAppMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class chatAppMainActivity extends AppCompatActivity {
    ActivityChatAppMainBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        binding=ActivityChatAppMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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