package com.rohindh.firechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rohindh.firechat.MessageActivity;
import com.rohindh.firechat.R;
import com.rohindh.firechat.model.Chat;
import com.rohindh.firechat.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> userList;
    private Context context;
    private boolean ischat;
    String lastmessage;

    public UserAdapter(List<User> userList, Context context,boolean ischat) {
        this.userList = userList;
        this.context = context;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User usermodel = userList.get(position);
        holder.username.setText(usermodel.getUsername());
        if(ischat){
            lastMessage(usermodel.getUserid(),holder.last_msg);
            if(usermodel.getStatus().equals("online")){
                holder.img_off.setVisibility(View.GONE);
                holder.img_on.setVisibility(View.VISIBLE);
            }else{
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }
        }else {
            holder.last_msg.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
            holder.img_on.setVisibility(View.GONE);
        }
        Picasso.get().load(usermodel.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.profileimage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid",usermodel.getUserid());
                Log.d("rockers",usermodel.getUserid());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    @SuppressWarnings("WeakerAccess")
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public CircleImageView profileimage;
        public CircleImageView img_on;
        private CircleImageView img_off;
        private TextView last_msg;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            last_msg = itemView.findViewById(R.id.last_message);
            img_off = itemView.findViewById(R.id.user_item_status_off);
            img_on = itemView.findViewById(R.id.user_item_status_on);
            username = itemView.findViewById(R.id.user_item_username);
            profileimage = itemView.findViewById(R.id.user_item_profileimage);


        }
    }

    public void lastMessage(final String userid, final TextView last_msg_txt){
        lastmessage = "default";
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && fuser != null && (chat.getSender().equals(fuser.getUid()) && chat.getReceiver().equals(userid) ||
                            chat.getSender().equals(userid) && chat.getReceiver().equals(fuser.getUid()))) {
                        lastmessage = chat.getMessage();
                    }
                }
                if (!lastmessage.equals("default")){
                    last_msg_txt.setText(lastmessage);
                }else {
                    last_msg_txt.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
