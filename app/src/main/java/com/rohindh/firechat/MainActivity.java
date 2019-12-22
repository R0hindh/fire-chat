package com.rohindh.firechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button loginbtn,createaccbtn;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(MainActivity.this,StartActivity.class));

            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        loginbtn = findViewById(R.id.mainact_loginbtn);
        createaccbtn = findViewById(R.id.mainact_createaccbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
        createaccbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CreateAccActivity.class));
            }
        });
    }

    private void status(String status){
        if(user!=null){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()) ;
        HashMap<String,Object> map =new HashMap<>();
        map.put("status",status);
        reference.updateChildren(map);}
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
