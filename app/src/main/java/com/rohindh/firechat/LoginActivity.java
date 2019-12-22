package com.rohindh.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText passwordtxt;
    private MaterialEditText emailtxt;
    private Button loginbtn;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private FirebaseUser user;
    private TextView forgotpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.login_progressbar);
        emailtxt = findViewById(R.id.login_email);
        passwordtxt = findViewById(R.id.login_password);
        loginbtn = findViewById(R.id.login_loginbtn);
        forgotpassword = findViewById(R.id.forgot_password);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailtxt.getText().toString().trim();
                String password = passwordtxt.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    loginWithEmail(email, password);
                }
            }
        });
    }

    private void loginWithEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = auth.getCurrentUser();
                    assert user != null;
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

