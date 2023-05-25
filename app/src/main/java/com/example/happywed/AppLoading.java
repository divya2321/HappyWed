package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.example.happywed.DBCon.HappyWedDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppLoading extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_loading);

        firebaseAuth = HappyWedDB.getFirebaseAuth();
        databaseReference = HappyWedDB.getDBConnection();

        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        final Thread loadingThread = new Thread(){

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
//                    Log.d("UserExits",currentUser.getDisplayName());
                    if(currentUser!= null){
                        Log.d("UserExits","User have");

                        databaseReference.child("users").orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                    AppLoading.this.finish();
                                }else{

                                    databaseReference.child("businesses").orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                startActivity(new Intent(getApplicationContext(), BusinessOwnerHome.class));
                                                AppLoading.this.finish();
                                            }else{
                                                startActivity(new Intent(getApplicationContext(), AppIntro.class));
                                                AppLoading.this.finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    else{
                        startActivity(new Intent(getApplicationContext(), AppIntro.class));
                        AppLoading.this.finish();
                    }



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };

        loadingThread.start();
    }

//    public static void printHashKey(Context pContext) {
//        try {
//            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("LOG_NAME", "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//        } catch (Exception e) {
//        }
//    }
}
