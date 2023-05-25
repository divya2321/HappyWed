package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happywed.Adapters.ChecklistAdapter;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.ChecklistModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Checklist extends AppCompatActivity {

    public static ImageView emptyNoteImage;
    public static TextView emptyNoteText, titleText;
    public static  LinearLayout bottomBorder;
    public static FloatingActionButton addChecklistBtn;
    public static RecyclerView checklistList;
    public static DatePicker completedDate;

    private Toolbar toolBar;
    private DatePicker datePicker;
    private Button addBtn, cancelBtn;
    private int day, month, year;
    static String checkDate;

    static Cursor searchReult;

    static Animation openEmptyNote, closeEmptyNote;

    public static ArrayList<ChecklistModel> checklistModels = new ArrayList<ChecklistModel>();

    static ChecklistAdapter checklistAdapter;

    public static boolean isPopUpOpen = false;

    private static final int NOTIFICATIONID = 1;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);

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




        emptyNoteImage = (ImageView) findViewById(R.id.emptyNoteImage);
        emptyNoteText = (TextView) findViewById(R.id.emptyNoteText);
        bottomBorder = (LinearLayout) findViewById(R.id.bottomBorder);
        addChecklistBtn = (FloatingActionButton) findViewById(R.id.addChecklistBtn);
        checklistList = (RecyclerView) findViewById(R.id.checklistList);
        titleText = (TextView) findViewById(R.id.titleText);
        completedDate = (DatePicker) findViewById(R.id.completedDate);


        datePicker = (DatePicker) findViewById(R.id.completedDate);
        addBtn = (Button) findViewById(R.id.doneCheckItem);
        cancelBtn = (Button) findViewById(R.id.cancelCheckItem);

        openEmptyNote = AnimationUtils.loadAnimation(this, R.anim.open_empty_note);
        closeEmptyNote = AnimationUtils.loadAnimation(this, R.anim.close_empty_note);

        bottomBorder.setTranslationY(1900);


        checklistAdapter = new ChecklistAdapter(this, checklistModels);
        checklistList.setLayoutManager(new LinearLayoutManager(this));
        checklistList.setAdapter(checklistAdapter);

        loadAllItem();

    }

    public static boolean isEdit= false;
    public static int updateId= 0;

    @SuppressLint("RestrictedApi")
    public void goToAddCheckList(View view) {
        isPopUpOpen = true;
        isEdit = false;

        bottomBorder.animate().translationY(0).setDuration(800).start();
        addChecklistBtn.setAlpha((float) 0.0);

        checklistList.animate().alpha(0.0f).setStartDelay(500);

        if (checklistList.getAdapter().getItemCount() == 0) {
            emptyNoteText.startAnimation(closeEmptyNote);
            emptyNoteImage.startAnimation(closeEmptyNote);
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }
    }

    SimpleDateFormat sdf;
    Date date;
    Date convertDate;

    Cursor searchLast, userSearch;

    String userId;

    @SuppressLint("RestrictedApi")
    public void goToDoneCheckList(View view) {

        date = new Date();
        sdf = new SimpleDateFormat("dd-mm-YYYY");


        day = completedDate.getDayOfMonth();
        month = completedDate.getMonth()+1;
        year = completedDate.getYear();

        checkDate = day+"-"+month+"-"+year;

        try {
            convertDate = sdf.parse(checkDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }






        if (!titleText.getText().toString().isEmpty()) {
            isPopUpOpen = false;
            if (!isEdit) {
                HappyWed.iud(this, "INSERT INTO checklist(itemName, completeDate, status) VALUES('" + titleText.getText().toString() + "','" + checkDate + "','0')");
                searchLast = HappyWed.search(Checklist.this, "SELECT * FROM checklist WHERE itemId = (SELECT MAX(ItemId)  FROM checklist)");

                if (searchLast.moveToNext()) {
                    Log.d("Saveddata ", String.valueOf(searchLast.getString(0)).concat(searchLast.getString(1)));
                    checklistModels.add(new ChecklistModel()
                            .setId(Integer.parseInt(searchLast.getString(0)))
                            .setTitle(titleText.getText().toString())
                            .setCheckDate(checkDate)
                            .setStatus(R.drawable.checklist_pending)
                            .setStatusText("Done"));

                    checklistAdapter.notifyDataSetChanged();

                    Intent intent = new Intent(Checklist.this,AlarmReceiver.class);
                    intent.putExtra("notificationId",NOTIFICATIONID);
                    intent.putExtra("todo",titleText.getText().toString());

                    PendingIntent alarmIntent =  PendingIntent.getBroadcast(Checklist.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                    Calendar startTime = Calendar.getInstance();
//                    startTime.set(Calendar.DATE,day);
//                    startTime.set(Calendar.MONTH,month);
//                    startTime.set(Calendar.YEAR,year);
                    startTime.set(Calendar.HOUR_OF_DAY,17);
                    startTime.set(Calendar.MINUTE,23);
                    startTime.set(Calendar.SECOND,0);
                    long alarmStartTime = startTime.getTimeInMillis();

                    alarm.set(AlarmManager.RTC_WAKEUP,alarmStartTime,alarmIntent);


                    loadAllItem();
                }

            }else {
                if (updateId!=0){
                    HappyWed.iud(this, "UPDATE checklist SET itemName='"+titleText.getText()+"', completeDate='"+checkDate+"', status='0' WHERE itemId='"+updateId+"' ");
                    loadAllItem();
                }

            }

            titleText.setText(null);
            checklistList.animate().alpha(1.0f).setStartDelay(150);
            bottomBorder.animate().translationY(1900).setDuration(800).start();
            addChecklistBtn.animate().alpha(1.0F).setStartDelay(800).start();

        }else {
            titleText.setError("Field cannot be empty!");
            titleText.requestFocus();
        }



    }

    @SuppressLint("RestrictedApi")
    public void goToCloseCheckList(View view) {

        isPopUpOpen = false;
        checklistList.animate().alpha(1.0f).setStartDelay(150);
        titleText.setText(null);
        titleText.setError(null);

        if (checklistList.getAdapter().getItemCount() == 0) {

            bottomBorder.animate().translationY(1900).setDuration(800).start();
            addChecklistBtn.animate().alpha(1.0F).setStartDelay(800).start();
            emptyNoteImage.startAnimation(openEmptyNote);
            emptyNoteImage.setVisibility(View.VISIBLE);
            emptyNoteText.setVisibility(View.VISIBLE);

        } else {
            bottomBorder.animate().translationY(1900).setDuration(800).start();
            addChecklistBtn.animate().alpha(1.0F).setStartDelay(800).start();
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }

    }

    int defaultStatus = R.drawable.checklist_pending;
    String doneText = "Done";

    public void loadAllItem(){
        checklistModels.clear();

        searchReult = HappyWed.search(Checklist.this, "SELECT * FROM checklist");


        while (searchReult.moveToNext()){
            String statusValue = searchReult.getString(3);
            if (statusValue.equals("0")){
                defaultStatus=R.drawable.checklist_pending;
                doneText= "Done";
            }else if (statusValue.equals("1")){
                defaultStatus=R.drawable.checklist_done;
                doneText= "Undone";
            }else if (statusValue.equals("2")){
                defaultStatus=R.drawable.checklist_pending;
                doneText= "Done";
            }
            checklistModels.add(new ChecklistModel()
                    .setId(Integer.parseInt(searchReult.getString(0)))
                    .setTitle(searchReult.getString(1))
                    .setCheckDate(searchReult.getString(2))
                    .setStatus(defaultStatus)
                    .setStatusText(doneText));

            checklistAdapter.notifyDataSetChanged();
        }

        if (checklistModels.isEmpty()) {

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
            checklistList.animate().alpha(1.0f).setStartDelay(150);
            titleText.setText(null);
            titleText.setError(null);

            if (checklistList.getAdapter().getItemCount() == 0) {

                bottomBorder.animate().translationY(1900).setDuration(800).start();
                addChecklistBtn.animate().alpha(1.0F).setStartDelay(800).start();
                emptyNoteImage.startAnimation(openEmptyNote);
                emptyNoteImage.setVisibility(View.VISIBLE);
                emptyNoteText.setVisibility(View.VISIBLE);

            } else {
                bottomBorder.animate().translationY(1900).setDuration(800).start();
                addChecklistBtn.animate().alpha(1.0F).setStartDelay(800).start();
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
