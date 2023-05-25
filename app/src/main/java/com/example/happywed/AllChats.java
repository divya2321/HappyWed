package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.happywed.Adapters.ChatListAdapter;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.ChatList;
import com.example.happywed.DBModel.ChatMessageModel;
import com.example.happywed.DBModel.User;
import com.example.happywed.Models.BusinessShopModel;
import com.example.happywed.Notifications.Token;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;

public class AllChats extends AppCompatActivity {

    private Toolbar toolBar;
    private RecyclerView recyclerView;

    ArrayList<User> chatLists  = new ArrayList<User>();
    ChatListAdapter chatListAdapter;

    private LinkedHashSet<String> userList = new LinkedHashSet<String>();

    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    private String shopKey;
    private String shopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_chats);

       shopKey =  getIntent().getStringExtra("shopKey");
        shopName =  getIntent().getStringExtra("shopName");

        databaseReference = HappyWedDB.getDBConnection();
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();

        toolBar = (Toolbar) findViewById(R.id.my_toolbar);

        if (getSupportActionBar() != null){
            setSupportActionBar(toolBar);
        }
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.chatList);

        chatLists.clear();

        chatListAdapter = new ChatListAdapter(getApplicationContext(), chatLists);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(chatListAdapter);

        userList.clear();


                                    Log.d("abccc",shopKey);
                                    databaseReference.child("chatList").child(shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                            databaseReference.child("chatList").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {

                                                    for (DataSnapshot snaps : dataSnapshot.getChildren()){
                                                        ChatList chatList = snaps.getValue(ChatList.class);
                                                        Log.d("abccc",chatList.getId()+".");
                                                        userList.add(chatList.getId());
                                                    }

                                                        Log.d("abccc",dataSnapshot1.getChildrenCount()+"");
                                                    for (final DataSnapshot snaps1  : dataSnapshot1.getChildren()){
                                                        snaps1.getRef().orderByChild("id").equalTo(shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                                                for (DataSnapshot snaps2 : dataSnapshot2.getChildren()){
                                                                    Log.d("abccc",snaps2.getKey()+"...");
                                                                    userList.add(snaps1.getKey());
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                    getChatList();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token1 = new Token(token);
        databaseReference.child(shopKey).setValue(token1);
    }


    private void getChatList() {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatLists.clear();

                for (DataSnapshot snaps1 : dataSnapshot.getChildren()){
                    User user = snaps1.getValue(User.class);

                    for (String userId : userList){
                        if (user.getUid().equals(userId)){
                            user.setShopId(shopKey);
                            user.setShopName(shopName);
                            chatLists.add(user);
                        }
                    }
                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
