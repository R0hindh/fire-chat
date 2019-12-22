package com.rohindh.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccActivity extends AppCompatActivity {
    private EditText passwordtxt;
    private MaterialEditText usernametxt, emailtxt;
    private Button createaccbtn;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);
        db = FirebaseDatabase.getInstance();


        toolbar = findViewById(R.id.create_acc_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressBar = findViewById(R.id.create_acc_progressbar);
        usernametxt = findViewById(R.id.create_acc_username);
        emailtxt = findViewById(R.id.create_acc_email);
        passwordtxt = findViewById(R.id.create_acc_password);
        createaccbtn = findViewById(R.id.create_acc_createbtn);

        auth = FirebaseAuth.getInstance();

        createaccbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernametxt.getText().toString().trim();
                String email = emailtxt.getText().toString().trim();
                String password = passwordtxt.getText().toString().trim();

                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    if (!(password.length() < 8)) {
                        progressBar.setVisibility(View.VISIBLE);
                        createaccount(username, email, password);
                    } else if (!email.contains("@")) {
                        Toast.makeText(CreateAccActivity.this, "enter a valid email address", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateAccActivity.this, "password length must be at least 8", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CreateAccActivity.this, "please enter all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createaccount(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = auth.getCurrentUser();
                    Map<String, Object> userdetails = new HashMap<>();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                    userdetails.put("username", username);
                    userdetails.put("userid", user.getUid());
                    userdetails.put("imageurl", "default");
                    userdetails.put("status", "offline");
                    userdetails.put("search", username.toLowerCase());

                    reference.setValue(userdetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirechatApi api = FirechatApi.getInstance();
                                api.setUserid(user.getUid());
                                api.setUsername(username);
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(CreateAccActivity.this, StartActivity.class));
                                finish();

                                Toast.makeText(CreateAccActivity.this, "success", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateAccActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreateAccActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
