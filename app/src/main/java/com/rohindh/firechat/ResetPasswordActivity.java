package com.rohindh.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private MaterialEditText email;
    private Button resetbtn;
    private Toolbar toolbar;
    private FirebaseUser user;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        toolbar = findViewById(R.id.restpassword_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Reset password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        email = findViewById(R.id.restpassword_email);
        resetbtn = findViewById(R.id.restpassword_resetbtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Objects.requireNonNull(email.getText()).toString().equals("")){
                    auth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Please check your email",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ResetPasswordActivity.this,StartActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Email id required",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
