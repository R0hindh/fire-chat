package com.rohindh.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;



import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.rohindh.firechat.Fragments.ChatsFragment;
import com.rohindh.firechat.Fragments.ProfileFragment;
import com.rohindh.firechat.Fragments.UserFragment;
import com.rohindh.firechat.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartActivity extends AppCompatActivity {
    CircleImageView profile_image;
    private TextView username;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        toolbar = findViewById(R.id.startact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        profile_image = findViewById(R.id.startact_profile_image);
        username = findViewById(R.id.startact_username);
        reference = db.getReference("Users").child(user.getUid());

        TabLayout tabLayout = findViewById(R.id.startact_table_layout);
        ViewPager viewPager = findViewById(R.id.startact_viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
        viewPagerAdapter.addFragment(new UserFragment(),"Users");
        viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                FirechatApi api = FirechatApi.getInstance();
                assert user != null;
                api.setUserid(user.getUserid());
                api.setUsername(user.getUsername());
                api.setImageurl(user.getImageurl());


                username.setText(user.getUsername());
                if(user.getImageurl().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageurl()).into(profile_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(StartActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                return true;
        }
        return false;
    }
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        ViewPagerAdapter (FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public  void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    private void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()) ;
        HashMap<String,Object> map =new HashMap<>();
        map.put("status",status);
        reference.updateChildren(map);
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
