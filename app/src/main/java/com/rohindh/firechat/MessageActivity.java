package com.rohindh.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rohindh.firechat.Adapter.MessageAdapter;

import com.rohindh.firechat.model.Chat;
import com.rohindh.firechat.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView opp_username;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private Toolbar toolbar;
    private Intent intent;
    private ImageButton sendbtn;
    private EditText sendtxt;
    private List<Chat> chatList;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ValueEventListener seenListener;
    private String opp_userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Firebase.setAndroidContext(getApplicationContext());
        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);

        //toolbar
        toolbar = findViewById(R.id.messageact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        intent = getIntent();
        opp_userid = intent.getStringExtra("userid");
        Log.d("rockers",opp_userid);
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        profile_image = findViewById(R.id.messageact_profile_image);
        opp_username = findViewById(R.id.messageact_username);
        sendbtn = findViewById(R.id.messageact_sendbtn);
        sendtxt = findViewById(R.id.messageact_sendtxt);

        recyclerView = findViewById(R.id.messageact_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sendtxt.getText().toString();
                if (!message.equals("")) {
                    sendmessage(fuser.getUid(), opp_userid, message);
                } else {
                    Toast.makeText(MessageActivity.this, "cannot send an empty text", Toast.LENGTH_SHORT).show();
                }
                sendtxt.setText("");

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(opp_userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User usermodel = dataSnapshot.getValue(User.class);
                Picasso.get().load(usermodel.getImageurl()).placeholder(R.mipmap.ic_launcher).into(profile_image);
                opp_username.setText(usermodel.getUsername());
                readmsg(fuser.getUid(), opp_userid, usermodel.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMsg(opp_userid);


    }

    private void seenMsg(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isseen", true);
                        snapshot.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendmessage(String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> msg = new HashMap<>();
        msg.put("sender", sender);
        msg.put("receiver", receiver);
        msg.put("message", message);
        msg.put("isseen", false);

        reference.child("Chats").push().setValue(msg);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(opp_userid);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(opp_userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void readmsg(final String myid, final String oppid, final String imageurl) {
        chatList = new ArrayList<>();


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(myid) && chat.getReceiver().equals(oppid) || chat.getSender().equals(oppid) && chat.getReceiver().equals(myid)) {
                        chatList.add(chat);

                    }

                    messageAdapter = new MessageAdapter(chatList, MessageActivity.this, imageurl);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(messageAdapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void status(String status) {
        if (fuser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            HashMap<String, Object> map = new HashMap<>();
            map.put("status", status);
            reference.updateChildren(map);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }

}
