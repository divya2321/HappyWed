package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class BusinessOwnerHome extends AppCompatActivity {


    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private UserInfo currentProfile;
    private GoogleSignInClient mGoogleSignInClient;

    private TextView ownerName,ownerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_owner_home);


        databaseReference = HappyWedDB.getDBConnection();
        firebaseAuth = HappyWedDB.getFirebaseAuth();
        currentProfile= firebaseAuth.getCurrentUser().getProviderData().get(0);

        ownerName = (TextView) findViewById(R.id.ownerName);
        ownerNumber = (TextView) findViewById(R.id.ownerNumber);

        ownerName.setText(currentProfile.getDisplayName());
        ownerNumber.setText(currentProfile.getPhoneNumber().substring(3).trim());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    public void gotoBusinessShopList(View view) {
        startActivity(new Intent(this, BusinessShopList.class));
    }

    public void goToLogout(View view) {

        firebaseAuth.signOut();
        mGoogleSignInClient.signOut();
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
        finishAffinity();
        startActivity(new Intent(getApplicationContext(), SignInOption.class));

    }

    public void goToSignupAsUser(View view) {

        databaseReference.child("users").orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    startActivity(new Intent(getApplicationContext(), UserDetails.class));
                    finishAffinity();
                }else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(BusinessOwnerHome.this);
                    builder.setTitle("Do you want to restore the database");
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("UserDetails", "dataSnapshot not exists");
                            Toast.makeText(BusinessOwnerHome.this, "Yes", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            finishAffinity();
                        }
                    });

                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {

                                databaseReference.child("users").child(childSnapshot.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                                Log.d("dbNode", "success");
                                    }
                                });
                                HappyWed.resetAllDatabase(BusinessOwnerHome.this);
                                startActivity(new Intent(getApplicationContext(), UserDetails.class));
                                finishAffinity();

                            }
                        }

                    });
                    builder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void goToProfile(View view) {
        startActivity(new Intent(getApplicationContext(),BusinessProfileEdit.class));
    }

    public void goToHelpCenter(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
        startActivity(i);
    }

    public void goToTermsConditions(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
        startActivity(i);
    }

    public void goToAboutUs(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
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

    private void status(final String status){
        databaseReference.child("businesses").orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object>  hm = new HashMap<String, Object>();
                    hm.put("status", status);

                    databaseReference.child("businesses").child(snaps.getKey()).updateChildren(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
