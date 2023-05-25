package com.example.happywed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.Chat;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.ChatMessageModel;
import com.example.happywed.DBModel.Shop;
import com.example.happywed.DBModel.User;
import com.example.happywed.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatListAdapterForUser extends RecyclerView.Adapter<ChatListAdapterForUser.MyViewHolder> {

    Context context;
    ArrayList<Shop> chatLists;



    public ChatListAdapterForUser(Context context, ArrayList<Shop> chatLists) {
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

        final Shop shop = chatLists.get(position);

        if (shop.getProfilePic()!=null){
        Picasso.get().load(shop.getProfilePic()).into(holder.profPic);}
        else {
            holder.profPic.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultshoppic));
        }
        holder.profName.setText(shop.getShopName());
        getLastMessage(shop.getShopId(), holder.profMessage, holder.profStatus);


        holder.chatListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, Chat.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("shopKey", shop.getShopId()).putExtra("ownerKey",shop.getOwnerId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    private String theLastMessage;

    private void getLastMessage(final String uid, final TextView lastMessage, final ImageView status){
        theLastMessage = "default";
        final FirebaseUser currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        DatabaseReference chatRef = HappyWedDB.getDBConnection().child("chats");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    ChatMessageModel chatModel = snaps.getValue(ChatMessageModel.class);
                    if (chatModel.getRecieverId().equals(currentUser.getUid()) && chatModel.getSenderId().equals(uid)
                    || chatModel.getRecieverId().equals(uid) && chatModel.getSenderId().equals(currentUser.getUid())){

                        if (chatModel.getRecieverId().equals(uid) && chatModel.getSenderId().equals(currentUser.getUid())){
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
