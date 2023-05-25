package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happywed.Adapters.GuestAdapter;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.GuestModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Guest extends AppCompatActivity {

    public int adultCount = 0, childCount= 0;

    public static ImageView emptyNoteImage;
    public static TextView emptyNoteText, totalAdultText, totalChildText, totalGuestText;
    public LinearLayout bottomBorder;
    public FloatingActionButton addGuestlistBtn;
    public RecyclerView guestList;
    public static Animation openEmptyNote, closeEmptyNote;

    public static GuestAdapter guestAdapter;

    private Toolbar toolBar;

    static Cursor searchReult;
    public static ArrayList<GuestModel> guestModels = new ArrayList<GuestModel>();

    public EditText familyName, adultCountText, childCountText;

    public static boolean isPopUpOpen = false;


    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest);

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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        familyName = (EditText) findViewById(R.id.familyText);
        adultCountText = (EditText) findViewById(R.id.adultCount);
        childCountText = (EditText) findViewById(R.id.childCount);

        adultCountText.setText(String.valueOf(adultCount));
        childCountText.setText(String.valueOf(childCount));

        emptyNoteImage = (ImageView) findViewById(R.id.emptyNoteImage);
        emptyNoteText = (TextView) findViewById(R.id.emptyNoteText);

        totalAdultText = findViewById(R.id.estimatedAdult);
        totalChildText = findViewById(R.id.totalChildren);
        totalGuestText = findViewById(R.id.totalCount);


        bottomBorder = (LinearLayout) findViewById(R.id.bottomBorder);

        addGuestlistBtn = (FloatingActionButton) findViewById(R.id.addGuesttListBtn);
        guestList = (RecyclerView) findViewById(R.id.guestList);

        openEmptyNote = AnimationUtils.loadAnimation(this, R.anim.open_empty_note);
        closeEmptyNote = AnimationUtils.loadAnimation(this, R.anim.close_empty_note);

        bottomBorder.setTranslationY(1420);

        guestAdapter = new GuestAdapter(this, guestModels);
        guestList.setLayoutManager(new LinearLayoutManager(this));
        guestList.setAdapter(guestAdapter);


        loadAllItem();

//        if (guestList.getAdapter().getItemCount() == 0) {
//
//            emptyNoteImage.startAnimation(openEmptyNote);
//            emptyNoteText.startAnimation(openEmptyNote);
//
//        } else {
//
//            emptyNoteImage.setVisibility(View.GONE);
//            emptyNoteText.setVisibility(View.GONE);
//        }



    }



    public void gotoCancelGuest(View view) {

        isPopUpOpen = false;

        guestList.animate().alpha(1.0f).setStartDelay(150);
        familyName.setText(null);
        familyName.setError(null);

        if (guestList.getAdapter().getItemCount() == 0) {

            bottomBorder.animate().translationY(1420).setDuration(800).start();
            addGuestlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
            emptyNoteImage.startAnimation(openEmptyNote);
            emptyNoteImage.setVisibility(View.VISIBLE);
            emptyNoteText.setVisibility(View.VISIBLE);

        } else {
            bottomBorder.animate().translationY(1420).setDuration(800).start();
            addGuestlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }


    }

    public static boolean isEdit= false;
    public static int updateId= 0;

    Cursor searchLast, userSearch;
    String userId;

    public void gotoDoneGuest(View view) {


        if (!familyName.getText().toString().isEmpty()) {
            isPopUpOpen = false;
            if (!isEdit) {

                HappyWed.iud(this, "INSERT INTO guest(familyName, adultCount, childCount) " +
                        "VALUES('" + familyName.getText().toString() + "','" + Integer.parseInt(adultCountText.getText().toString()) + "','"+Integer.parseInt(childCountText.getText().toString())+"')");
                searchLast = HappyWed.search(Guest.this, "SELECT * FROM guest WHERE guestId = (SELECT MAX(guestId)  FROM guest)");

                if (searchLast.moveToNext()) {
                    guestModels.add(new GuestModel()
                            .setGuestId(Integer.parseInt(searchLast.getString(0)))
                            .setFamilyName(searchLast.getString(1))
                            .setAdultCount(Integer.parseInt(searchLast.getString(2)))
                            .setChildCount(Integer.parseInt(searchLast.getString(3)))
                            .setAllCount(Integer.parseInt(searchLast.getString(2)) + Integer.parseInt(searchLast.getString(3))));


                    guestAdapter.notifyDataSetChanged();
                    loadAllItem();
                    Toast.makeText(this, "Successfully Saved!", Toast.LENGTH_SHORT).show();
                }

            }else {
                if (updateId!=0){
                    HappyWed.iud(this, "UPDATE guest " +
                            "SET familyName='"+familyName.getText()+"', adultCount='"+Integer.parseInt(adultCountText.getText().toString())+"', childCount='"+Integer.parseInt(childCountText.getText().toString())+"' WHERE guestId='"+updateId+"' ");
                    loadAllItem();
                }

            }

            familyName.setText(null);
            adultCountText.setText("0");
            childCountText.setText("0");

            guestList.animate().alpha(1.0f).setStartDelay(150);
            bottomBorder.animate().translationY(1420).setDuration(800).start();
            addGuestlistBtn.animate().alpha(1.0F).setStartDelay(800).start();

        }else {
            familyName.setError("Field cannot be empty!");
            familyName.requestFocus();
        }



    }


    public void gotoAddGuest(View view) {

        isPopUpOpen = true;

        bottomBorder.animate().translationY(0).setDuration(800).start();
        addGuestlistBtn.setAlpha((float) 0.0);

        guestList.animate().alpha(0.0f).setStartDelay(500);

        if (guestList.getAdapter().getItemCount() == 0) {
            emptyNoteText.startAnimation(closeEmptyNote);
            emptyNoteImage.startAnimation(closeEmptyNote);
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }


    }

    public void gotoIncrementAdult(View view) {
        int i = Integer.parseInt(adultCountText.getText().toString());
        adultCountText.setText(String.valueOf(++i));
    }
    public void gotoDecrementAdult(View view) {
        int i = Integer.parseInt(adultCountText.getText().toString());

        if (--i<0){
            adultCountText.setText("0");
        }else {
            adultCountText.setText(String.valueOf(i));
        }
    }
    public void gotoIncrementChild(View view) {
        int i = Integer.parseInt(childCountText.getText().toString());
        childCountText.setText(String.valueOf(++i));
    }
    public void gotoDecrementChild(View view) {
        int i = Integer.parseInt(childCountText.getText().toString());

        if (--i<0){
            childCountText.setText("0");
        }else {
            childCountText.setText(String.valueOf(i));
        }
    }


    public static Cursor sumAdult, sumChild;
    public static int totalAdults, totalChildren, totalGuests;

    public void loadAllItem(){

        Log.d("INSIDE_LOAD_ALL","___");
        guestModels.clear();
        Log.d("INSIDE_LOAD_ALL","MODEL_CLEARED");

        searchReult = HappyWed.search(Guest.this, "SELECT * FROM guest");
        Log.d("INSIDE_LOAD_ALL","SEARCHED");

        while (searchReult.moveToNext()) {
            guestModels.add(new GuestModel()
                    .setGuestId(Integer.parseInt(searchReult.getString(0)))
                    .setFamilyName(searchReult.getString(1))
                    .setAdultCount(Integer.parseInt(searchReult.getString(2)))
                    .setChildCount(Integer.parseInt(searchReult.getString(3)))
                    .setAllCount(Integer.parseInt(searchReult.getString(2)) + Integer.parseInt(searchReult.getString(3))));
            Log.d("INSIDE_LOAD_ALL", "SEARCHED");

            guestAdapter.notifyDataSetChanged();
        }





        if (guestModels.isEmpty()) {

            emptyNoteImage.startAnimation(openEmptyNote);
            emptyNoteText.startAnimation(openEmptyNote);

            totalAdultText.setText("0");
            totalChildText.setText("0");

        } else {
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);


            sumAdult = HappyWed.search(Guest.this, "SELECT SUM(adultCount) FROM guest");
            sumChild = HappyWed.search(Guest.this, "SELECT SUM(childCount) FROM guest");



            if (sumAdult.moveToNext()){
                totalAdults = Integer.parseInt(sumAdult.getString(0));
                Log.d("TOTAL_ADULT",String.valueOf(totalAdults));
//                totalAdultText.setText(totalAdults);}
            totalAdultText.setText(sumAdult.getString(0));}
            else {
                totalAdultText.setText("0");
            }
            if (sumChild.moveToNext()){
                totalChildren = Integer.parseInt(sumChild.getString(0));
                Log.d("TOTAL_CHILDREN",String.valueOf(totalChildren));
//                totalChildText.setText(totalChildren);}
            totalChildText.setText(sumChild.getString(0));}
            else {
                totalChildText.setText("0");
            }

            Log.d("TOTAL_GUEST",String.valueOf(totalAdults+totalChildren));
            totalGuests = totalAdults+totalChildren;
            totalGuestText.setText(String.valueOf(totalGuests));
        }

    }

    @Override
    public void onBackPressed() {
        if(isPopUpOpen){
            isPopUpOpen = false;

            guestList.animate().alpha(1.0f).setStartDelay(150);
            familyName.setText(null);
            familyName.setError(null);

            if (guestList.getAdapter().getItemCount() == 0) {

                bottomBorder.animate().translationY(1420).setDuration(800).start();
                addGuestlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
                emptyNoteImage.startAnimation(openEmptyNote);
                emptyNoteImage.setVisibility(View.VISIBLE);
                emptyNoteText.setVisibility(View.VISIBLE);

            } else {
                bottomBorder.animate().translationY(1420).setDuration(800).start();
                addGuestlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
                emptyNoteImage.setVisibility(View.GONE);
                emptyNoteText.setVisibility(View.GONE);
            }
        }else {
            super.onBackPressed();
        }
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
