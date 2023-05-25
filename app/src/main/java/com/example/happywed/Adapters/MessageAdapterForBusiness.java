package com.example.happywed.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.ChatMessageModel;
import com.example.happywed.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageAdapterForBusiness extends RecyclerView.Adapter<MessageAdapterForBusiness.MyViewHolder> {

    Context context;
    ArrayList<ChatMessageModel> chatLists;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

//    FirebaseUser firebaseUser;
private int type;
    String senderId;



    public MessageAdapterForBusiness(Context context, ArrayList<ChatMessageModel> chatLists,String senderId) {
        this.context = context;
        this.chatLists = chatLists;
        this.senderId = senderId;
    }




    static class MyViewHolder extends RecyclerView.ViewHolder{

        public static TextView messageChatText,msgStatus;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            messageChatText = itemView.findViewById(R.id.chatText);
            msgStatus = itemView.findViewById(R.id.msgStatus);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false));
        }   else {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ChatMessageModel chatMessageModel = chatLists.get(position);

        holder.messageChatText.setText(chatMessageModel.getMessageText());

        if(type == MSG_TYPE_RIGHT){
            if(position == chatLists.size()-1){
                holder.msgStatus.setVisibility(View.VISIBLE);
                holder.msgStatus.setText(chatMessageModel.getMessageStatus());
            }else{
                holder.msgStatus.setVisibility(View.GONE);
            }
        }else{
            holder.msgStatus.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatLists.get(position).getSenderId().equals(senderId)){
            type =MSG_TYPE_RIGHT;
            return MSG_TYPE_RIGHT;
        }else {
            type =MSG_TYPE_LEFT;
            return MSG_TYPE_LEFT;
        }

    }
}

