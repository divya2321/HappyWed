package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.happywed.DBCon.HappyWedDB;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Service extends AppCompatActivity {

    Toolbar toolBar;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service);

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
    }


    public void gotoJewelleryCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.jewellery)));
    }

    public void gotoDressCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.dresses)));
    }

    public void gotoSalonCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.salon)));
    }

    public void gotoHotelCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.hotel)));
    }

    public void gotoPoruCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.poru)));
    }

    public void gotoDecorationCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.decoration)));
    }

    public void gotoPhotographerCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.photographers)));
    }

    public void gotoInvitationCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.invitations)));
    }

    public void gotoCakeCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.cake)));
    }

    public void gotoMusicCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.music)));
    }

    public void gotoCaterineCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.caterine)));
    }

    public void gotoVehicleCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.vehicle)));
    }

    public void gotoWeddingPlanCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.wedding_planner)));
    }

    public void gotoEventPlaneCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.event_planner)));
    }

    public void gotoOtherCategoryView(View view) {
        startActivity(new Intent(this, CommonCategoryView.class).putExtra("category",getString(R.string.other)));
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
        databaseReference.child("users").orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    hm.put("status", status);

                    databaseReference.child("users").child(snaps.getKey()).updateChildren(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
