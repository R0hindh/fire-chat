package com.rohindh.firechat.Adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.rohindh.firechat.R;
import com.rohindh.firechat.model.Chat;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Chat> chatList;
    private Context context;
    private String imageurl;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    public MessageAdapter(List<Chat> chatList, Context context, String imageurl) {
        this.chatList = chatList;
        this.context = context;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);

            return new MessageAdapter.ViewHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);

            return new MessageAdapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.show_message.setText(chat.getMessage());

        Picasso.get().load(imageurl).placeholder(R.mipmap.ic_launcher).into(holder.profileimage);

        if(position == chatList.size()-1){
            Log.d("seen", String.valueOf(chat.isIsseen()));
            if(chat.isIsseen()){

                holder.txt_seen.setText("Seen");
            }else {

                Log.d("seen", String.valueOf(chat.isIsseen()));
                holder.txt_seen.setText("Delivered");
            }
        }else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message ;
        public CircleImageView profileimage;
        public TextView txt_seen;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_seen = itemView.findViewById(R.id.text_seen);
            show_message = itemView.findViewById(R.id.message_show);
            profileimage = itemView.findViewById(R.id.msg_profileimage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

}
