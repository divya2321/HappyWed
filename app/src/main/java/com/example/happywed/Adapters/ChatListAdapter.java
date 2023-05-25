package com.example.happywed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.happywed.Chat;
import com.example.happywed.ChatForBusiness;
import com.example.happywed.Checklist;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.ChatMessageModel;
import com.example.happywed.DBModel.User;
import com.example.happywed.Models.ChecklistModel;
import com.example.happywed.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    Context context;
    ArrayList<User> chatLists;



    public ChatListAdapter(Context context, ArrayList<User> chatLists) {
        this.context = context;
        this.chatLists = chatLists;
    }




    static class MyViewHolder extends RecyclerView.ViewHolder{

        public static TextView profName,profMessage;
        ImageView profPic, profStatus;
        LinearLayout chatListItem;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            profName = (TextView) itemView.findViewById(R.id.profileName);
            profMessage = (TextView) itemView.findViewById(R.id.profileMessage);
//            swipeLayout = (SwipeLayout)itemView.findViewById(R.id.swipe);
            profPic = (ImageView) itemView.findViewById(R.id.profileImage);
            profStatus = (ImageView) itemView.findViewById(R.id.chat_message_status);
            chatListItem = itemView.findViewById(R.id.chatListItem);
        }
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.all_chats_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final User user = chatLists.get(position);

        Picasso.get().load(user.getProfilePicUrl()).into(holder.profPic);
        holder.profName.setText(user.getUserName());


        getLastMessage(user.getUid(),user.getShopId(), holder.profMessage, holder.profStatus);


        holder.chatListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatForBusiness.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("receiverId", user.getUid()).putExtra("senderId",user.getShopId()).putExtra("senderName",user.getShopName()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }



    private String theLastMessage;

    private void getLastMessage(final String recieverId,final String senderId, final TextView lastMessage, final ImageView status){

        Log.d("adccc",recieverId);
        Log.d("adccc",senderId);
        theLastMessage = "default";
        DatabaseReference chatRef = HappyWedDB.getDBConnection().child("chats");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    ChatMessageModel chatModel = snaps.getValue(ChatMessageModel.class);

                    Log.d("adccc",chatModel.getRecieverId()+".");
                    Log.d("adccc",chatModel.getSenderId()+".");
                    if (chatModel.getRecieverId().equals(senderId) && chatModel.getSenderId().equals(recieverId)
                            || chatModel.getRecieverId().equals(recieverId) && chatModel.getSenderId().equals(senderId)){
                        Log.d("adccc",chatModel.getMessageText());
                        if (chatModel.getRecieverId().equals(recieverId) && chatModel.getSenderId().equals(senderId)){
                            lastMessage.setTextColor(context.getResources().getColor(R.color.ash));
                            if (chatModel.getMessageStatus().equals("seen")){
                                status.setImageResource(R.drawable.message_seen);
                            }else{
                                status.setImageResource(R.drawable.message_delivered);
                            }

                        }else{
                            if (chatModel.getMessageStatus().equals("seen")){
                                lastMessage.setTextColor(context.getResources().getColor(R.color.ash));
                            }else{
                                lastMessage.setTextColor(context.getResources().getColor(R.color.button_end));
                            }
                            status.setImageDrawable(null);
                        }

                        theLastMessage = chatModel.getMessageText();

                    }

                }

                switch (theLastMessage){
                    case "default": lastMessage.setText("No Message");
                        break;

                    default: lastMessage.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
