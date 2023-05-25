package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.happywed.Adapters.MessageAdapter;
import com.example.happywed.Adapters.MessageAdapterForBusiness;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.ChatMessageModel;
import com.example.happywed.DBModel.Shop;
import com.example.happywed.DBModel.User;
import com.example.happywed.Notifications.Data;
import com.example.happywed.Notifications.Sender;
import com.example.happywed.Notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatForBusiness extends AppCompatActivity {

    private EditText messageArea;
    private ImageButton setButton;
    private TextView profileName;
    private CircleImageView profilePicture;
    Toolbar toolBar;

    private String receiverId,senderId,sendOwnerKey,senderName;

    MessageAdapterForBusiness messageAdapter;
    ArrayList<ChatMessageModel> chatList = new ArrayList<ChatMessageModel>();
    private RecyclerView chatMessageList;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private RequestQueue requestQueue;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        receiverId = getIntent().getStringExtra("receiverId");
        senderId = getIntent().getStringExtra("senderId");
        senderName = getIntent().getStringExtra("senderName");

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

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        profileName = findViewById(R.id.profileName);
        profilePicture = findViewById(R.id.profilePicture);

        chatMessageList = findViewById(R.id.chatMessageList);
        chatMessageList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        chatMessageList.setLayoutManager(linearLayoutManager);




        messageArea = findViewById(R.id.messageArea);
        setButton = findViewById(R.id.sendButton);

        setButton.setEnabled(false);
        setButton.setImageResource(R.drawable.not_send_button);

        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (messageArea.getText().toString().trim().isEmpty()){
                    setButton.setEnabled(false);
                    setButton.setImageResource(R.drawable.not_send_button);
                }else{
                    setButton.setEnabled(true);
                    setButton.setImageResource(R.drawable.send_button);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

            databaseReference.child("users").orderByChild("uid").equalTo(receiverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String profPicture = ((Map<String, String>) snapshot.getValue()).get("profilePicUrl");
                        String name = ((Map<String, String>) snapshot.getValue()).get("userName");

                        Picasso.get().load(profPicture).into(profilePicture);

                        if (name.length() >= 20) {
                            profileName.setText(name.substring(0, 20) + "...");
                        } else {
                            profileName.setText(name);
                        }

                     readMessages(senderId, receiverId);


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        seenMessage(receiverId);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notify = true;
                String message = messageArea.getText().toString().trim();

                ChatMessageModel chatMessageModel = new ChatMessageModel()
                        .setRecieverId(receiverId)
                        .setOwnerId(sendOwnerKey)
                        .setSenderId(senderId)
                        .setMessageText(message)
                        .setMessageStatus("delivered");

                databaseReference.child("chats").push().setValue(chatMessageModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            messageArea.setText("");


                            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chatList")
                                    .child(senderId).child(receiverId);

                            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        chatRef.child("id").setValue(receiverId);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final String msg = message;

                if (notify) {
                    sendNotification(receiverId, senderName, msg);
                }
                notify = false;


//        databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Shop shop = dataSnapshot.getValue(Shop.class);
//
//                String name = shop.getShopName();
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


            }

            private void sendNotification(String reciever, final String username, final String msg) {
                DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                Query query = tokens.orderByKey().equalTo(reciever);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snaps : dataSnapshot.getChildren()) {
                            Token token = snaps.getValue(Token.class);
                            Data data = new Data(currentUser.getUid(), R.mipmap.ic_launcher, username + ": " + msg, "New Message", receiverId);
                            Sender sender = new Sender(data, token.getToken());

                            try {
                                JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("JSON_RESPONSE", "Error: " + response.toString());
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("JSON_RESPONSE", "onResponse: " + error.toString());
                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {

                                        Map<String, String> headers = new HashMap<>();
                                        headers.put("Content-Type", "application/json");
                                        headers.put("Authorization", "key=AAAA4LWnWAQ:APA91bHkDftTLivpLm3bSBdNEMxYpt13xkJk9SSxZURo90828MZ2VT2HF8kfpliOcZetRlAcaoUIbYFKkremCM9XzJMc3UALkgC4d8-qi_DEnAbLa172a0gNPHrf5bKLZGrPFY5fn0Ax");

                                        return headers;
                                    }
                                };

                                requestQueue.add(jsonObjectRequest);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

            private void readMessages(final String senderId, final String recieverId) {
                chatList.clear();
                Log.d("aaaa", senderId + ".");
                Log.d("aaaa", recieverId + "...");
                databaseReference.child("chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatList.clear();

                        for (DataSnapshot snaps : dataSnapshot.getChildren()) {
                            ChatMessageModel chatMessageModel = snaps.getValue(ChatMessageModel.class);

                            if (chatMessageModel.getRecieverId().equals(senderId) && chatMessageModel.getSenderId().equals(recieverId)
                                    || chatMessageModel.getRecieverId().equals(recieverId) && chatMessageModel.getSenderId().equals(senderId)) {

                                sendOwnerKey = chatMessageModel.getOwnerId();
                                Log.d("aaaa", chatMessageModel.getMessageText() + "--");
                                chatList.add(chatMessageModel);


                                messageAdapter = new MessageAdapterForBusiness(getApplicationContext(), chatList, senderId);
                                chatMessageList.setAdapter(messageAdapter);
//                 messageAdapter.notifyDataSetChanged();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void seenMessage(final String uid) {

                databaseReference.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snaps1 : dataSnapshot.getChildren()) {
                            ChatMessageModel chatModel = snaps1.getValue(ChatMessageModel.class);

                            if (chatModel.getRecieverId().equals(senderId) && chatModel.getSenderId().equals(uid)) {
                                HashMap<String, Object> updateMap = new HashMap<>();
                                updateMap.put("messageStatus", "seen");
                                snaps1.getRef().updateChildren(updateMap);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }


