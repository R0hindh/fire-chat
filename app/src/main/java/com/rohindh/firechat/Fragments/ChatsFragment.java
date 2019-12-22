package com.rohindh.firechat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rohindh.firechat.Adapter.UserAdapter;
import com.rohindh.firechat.R;
import com.rohindh.firechat.model.Chatlist;
import com.rohindh.firechat.model.User;
import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Chatlist> userlist;
    private List<User> mUser;
    private UserAdapter userAdapter;
    private FirebaseUser fuser;
    private List<String> stringUserList;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.chat_fragment_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        userlist = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid());



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    userlist.add(chatlist);
                    Log.d("firebase user id",chatlist.getId());
                }
                chatList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }


    private void chatList() {
        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist:userlist){
                        if (user.getUserid().equals(chatlist.getId())){
                            mUser.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(mUser,getContext(),true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
