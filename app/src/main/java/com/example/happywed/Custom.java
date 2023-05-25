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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.happywed.Adapters.CustomAdapter;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.CustomModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Custom extends AppCompatActivity {

    public ImageView emptyNoteImage;
    public TextView emptyNoteText;

    public LinearLayout bottomBorder;

    public FloatingActionButton addCustomlistBtn;
    public RecyclerView customList;

    public Animation openEmptyNote, closeEmptyNote;

    public CustomAdapter customAdapter;
    public CustomModel customModel;

    ArrayList<CustomModel> customModels = new ArrayList<CustomModel>();
    static Cursor searchReult;

    public EditText customTitle, addrContact, addrNo, addrStreet, addrCity, customDescription, customBudget;

    private Toolbar toolBar;

    public static boolean isPopUpOpen = false;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom);

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

        customTitle = (EditText) findViewById(R.id.titleText);
        addrContact = (EditText) findViewById(R.id.addrContact);
        addrNo = (EditText) findViewById(R.id.addrNo);
        addrStreet = (EditText) findViewById(R.id.addrStrt);
        addrCity = (EditText) findViewById(R.id.addrCity);
        customDescription = (EditText) findViewById(R.id.description);
        customBudget = (EditText) findViewById(R.id.budget);

        customModel = new CustomModel();
        customList = (RecyclerView) findViewById(R.id.customList);

        emptyNoteImage = (ImageView) findViewById(R.id.emptyNoteImage);
        emptyNoteText = (TextView) findViewById(R.id.emptyNoteText);

        bottomBorder = (LinearLayout) findViewById(R.id.bottomBorder);

        addCustomlistBtn = (FloatingActionButton) findViewById(R.id.addGuesttListBtn);
        customList = (RecyclerView) findViewById(R.id.customList);

        openEmptyNote = AnimationUtils.loadAnimation(this, R.anim.open_empty_note);
        closeEmptyNote = AnimationUtils.loadAnimation(this, R.anim.close_empty_note);

        bottomBorder.setTranslationY(1750);

        customAdapter = new CustomAdapter(this, customModels);
        customList.setLayoutManager(new LinearLayoutManager(this));
        customList.setAdapter(customAdapter);


        if (customList.getAdapter().getItemCount() == 0) {

            emptyNoteImage.startAnimation(openEmptyNote);
            emptyNoteText.startAnimation(openEmptyNote);

        } else {

            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }

        loadAllItem();

    }

    public void gotoAddCustom(View view) {

        isPopUpOpen = true;

        bottomBorder.animate().translationY(0).setDuration(800).start();
        addCustomlistBtn.setAlpha((float) 0.0);

        customList.animate().alpha(0.0f).setStartDelay(500);

        if (customList.getAdapter().getItemCount() == 0) {
            emptyNoteText.startAnimation(closeEmptyNote);
            emptyNoteImage.startAnimation(closeEmptyNote);
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }

    }



    public static boolean isEdit= false;
    public static int updateId= 0;

    Cursor searchLast, userSearch;
    String userId;

    public void gotoDoneCustom(View view) {

        if (!customTitle.getText().toString().isEmpty()) {

            isPopUpOpen = false;   isPopUpOpen = false;

            if (!isEdit) {
                Log.d("INSIDE_", customTitle.getText().toString());

                HappyWed.iud(Custom.this, "INSERT INTO custom (customTitle,customContact,customNo,customStreet,customCity,customDescription,customBudget) " +
                        "VALUES ('" + customTitle.getText().toString() + "','" + addrContact.getText().toString() + "','" + addrNo.getText().toString() + "','" + addrStreet.getText().toString() + "','" + addrCity.getText().toString() + "','" + customDescription.getText().toString() + "','" + customBudget.getText().toString() + "')");

                Log.d("INSIDE_", "AFTER_SAVED");

                searchLast = HappyWed.search(Custom.this, "SELECT * FROM custom WHERE customId = (SELECT MAX(customId)  FROM custom)");

                if (searchLast.moveToNext()) {
                    customModels.add(new CustomModel()
                            .setCustomId(Integer.parseInt(searchLast.getString(0)))
                            .setCustomTitle(searchLast.getString(1))
                            .setAddrContact(searchLast.getString(2))
                            .setAddrNo(searchLast.getString(3))
                            .setAddrStreet(searchLast.getString(4))
                            .setAddrCity(searchLast.getString(5))
                            .setAddrDescription(searchLast.getString(6))
                            .setCustomBudget(searchLast.getString(7)));


                    customAdapter.notifyDataSetChanged();


                    customTitle.setText(null);
                    addrContact.setText(null);
                    addrNo.setText(null);
                    addrStreet.setText(null);
                    addrCity.setText(null);
                    customDescription.setText(null);
                    customBudget.setText(null);

                    loadAllItem();

                }

            } else {
                if (updateId != 0) {
                    Log.d("JUST__", String.valueOf(updateId));
                    HappyWed.iud(this, "UPDATE custom SET customTitle='" + customTitle.getText() + "', customContact='" + addrContact.getText() + "', customNo='" + addrNo.getText() + "', customStreet='" + addrStreet.getText() + "', customCity='" + addrCity.getText() + "', customDescription='" + customDescription.getText() + "', customBudget='" + customBudget.getText() + "' WHERE customId='" + updateId + "' ");
                    loadAllItem();
                }
            }

            customList.animate().alpha(1.0f).setStartDelay(150);
            bottomBorder.animate().translationY(1750).setDuration(800).start();
            addCustomlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
        }else{
            customTitle.setError("Field cannot be empty!");
            customTitle.requestFocus();
        }




    }

    public void gotoCancelCustom(View view) {

        isPopUpOpen = false;

        customList.animate().alpha(1.0f).setStartDelay(150);
        customTitle.setText(null);
        customTitle.setError(null);
        addrContact.setText(null);
        addrNo.setText(null);
        addrStreet.setText(null);
        addrCity.setText(null);
        customDescription.setText(null);
        customBudget.setText(null);

        if (customList.getAdapter().getItemCount() == 0) {

            bottomBorder.animate().translationY(1750).setDuration(800).start();
            addCustomlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
            emptyNoteImage.startAnimation(openEmptyNote);
            emptyNoteImage.setVisibility(View.VISIBLE);
            emptyNoteText.setVisibility(View.VISIBLE);

        } else {
            bottomBorder.animate().translationY(1750).setDuration(800).start();
            addCustomlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }
    }


    public void loadAllItem(){

        Log.d("INSIDE_LOAD_ALL","___");
        customModels.clear();
        Log.d("INSIDE_LOAD_ALL","MODEL_CLEARED");

        searchReult = HappyWed.search(Custom.this, "SELECT * FROM custom");
        Log.d("INSIDE_LOAD_ALL","SEARCHED");

        while (searchReult.moveToNext()) {
            customModels.add(new CustomModel()
                    .setCustomId(Integer.parseInt(searchReult.getString(0)))
                    .setCustomTitle(searchReult.getString(1))
                    .setAddrContact(searchReult.getString(2))
                    .setAddrNo(searchReult.getString(3))
                    .setAddrStreet(searchReult.getString(4))
                    .setAddrCity(searchReult.getString(5))
                    .setAddrDescription(searchReult.getString(6))
                    .setCustomBudget(searchReult.getString(7)));
            Log.d("INSIDE_LOAD_ALL", "SEARCHED");

            customAdapter.notifyDataSetChanged();
        }

        if (customModels.isEmpty()) {

            emptyNoteImage.startAnimation(openEmptyNote);
            emptyNoteText.startAnimation(openEmptyNote);

        } else {
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        if(isPopUpOpen){
            isPopUpOpen = false;

            customList.animate().alpha(1.0f).setStartDelay(150);
            customTitle.setText(null);
            customTitle.setError(null);
            addrContact.setText(null);
            addrNo.setText(null);
            addrStreet.setText(null);
            addrCity.setText(null);
            customDescription.setText(null);
            customBudget.setText(null);

            if (customList.getAdapter().getItemCount() == 0) {

                bottomBorder.animate().translationY(1750).setDuration(800).start();
                addCustomlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
                emptyNoteImage.startAnimation(openEmptyNote);
                emptyNoteImage.setVisibility(View.VISIBLE);
                emptyNoteText.setVisibility(View.VISIBLE);

            } else {
                bottomBorder.animate().translationY(1750).setDuration(800).start();
                addCustomlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
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
