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
import com.example.happywed.Adapters.ChatListAdapterForUser;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.ChatList;
import com.example.happywed.DBModel.Shop;
import com.example.happywed.DBModel.User;
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

public class AllChatsForUser extends AppCompatActivity {

    private Toolbar toolBar;
    private RecyclerView recyclerView;

    ArrayList<Shop> chatLists  = new ArrayList<Shop>();
    ChatListAdapterForUser chatListAdapterForUser;

    private LinkedHashSet<String> userList = new LinkedHashSet<String>();

    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_chats_for_user);

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

        chatListAdapterForUser = new ChatListAdapterForUser(getApplicationContext(), chatLists);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(chatListAdapterForUser);



        userList.clear();

        databaseReference.child("chatList").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                databaseReference.child("chatList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {

                        for (DataSnapshot snaps : dataSnapshot.getChildren()){
                            ChatList chatList = snaps.getValue(ChatList.class);
                            Log.d("abcc",chatList.getId()+".");
                            userList.add(chatList.getId());
                        }

                        for (final DataSnapshot snaps1  : dataSnapshot1.getChildren()){
                            snaps1.getRef().orderByChild("id").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    for (DataSnapshot snaps2 : dataSnapshot2.getChildren()){
                                        Log.d("abcc",snaps2.getKey()+"...");
                                        userList.add(snaps2.getKey());
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
        databaseReference.child(currentUser.getUid()).setValue(token1);
    }


    private void getChatList() {


        databaseReference.child("businesses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snaps1 : dataSnapshot.getChildren()){

                    for (final String userId : userList) {
                        Log.d("abcc",snaps1.getKey()+"-");
                        Log.d("abcc",userId +"---");
                        databaseReference.child("businesses").child(snaps1.getKey()).child("Shops").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                Log.d("abcc",dataSnapshot1.getChildrenCount()+"");
//                                for (DataSnapshot snaps2 : dataSnapshot1.getChildren()){

                                if(dataSnapshot1.exists()) {
                                    String shopName = ((Map<String, String>) dataSnapshot1.getValue()).get("shopName");
                                    String shopId = userId;
                                    String shopProfilePic = ((Map<String, String>) dataSnapshot1.getValue()).get("profilePic");


                                    Log.d("abcc", shopName);
//
                                    chatLists.add(new Shop().setShopName(shopName).setShopId(shopId).setProfilePic(shopProfilePic).setOwnerId(snaps1.getKey()));
                                chatListAdapterForUser.notifyDataSetChanged();
//                                }

                                }
                            }



                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
