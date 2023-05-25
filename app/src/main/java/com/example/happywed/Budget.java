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
import android.widget.Toast;

import com.example.happywed.Adapters.BudgetAdapter;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BudgetModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Budget extends AppCompatActivity {

    public ImageView emptyNoteImage;
    public TextView emptyNoteText, titleText, estimateText, totalCostText, estimatedCostText, dueCostText, loadBudget;
    public LinearLayout bottomBorder;
    public FloatingActionButton addBudgetlistBtn;
    public RecyclerView budgetList;
    public  static EditText budgetInfoText, budgetCostText;

    private Animation openEmptyNote, closeEmptyNote;

    private ArrayList<BudgetModel> budgetModels = new ArrayList<BudgetModel>();

    private BudgetAdapter budgetAdapter;
    static Cursor searchReult;

    private Toolbar toolBar;

    public static boolean isPopUpOpen = false;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget);

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

        emptyNoteImage = (ImageView) findViewById(R.id.emptyNoteImage);
        emptyNoteText = (TextView) findViewById(R.id.emptyNoteText);

        titleText = (TextView) findViewById(R.id.titleText);
        estimateText = (TextView) findViewById(R.id.estimatedCostText);
        bottomBorder = (LinearLayout) findViewById(R.id.bottomBorder);

        loadBudget = findViewById(R.id.estimatedCost);
        totalCostText = findViewById(R.id.totalCost);
        estimatedCostText = findViewById(R.id.estimatedCost);
        dueCostText = findViewById(R.id.dueCost);

        budgetInfoText = findViewById(R.id.titleText);
        budgetCostText = findViewById(R.id.estimatedCostText);

        addBudgetlistBtn = (FloatingActionButton) findViewById(R.id.addBudgetListBtn);
        budgetList = (RecyclerView) findViewById(R.id.budgetList);

        openEmptyNote = AnimationUtils.loadAnimation(this, R.anim.open_empty_note);
        closeEmptyNote = AnimationUtils.loadAnimation(this, R.anim.close_empty_note);

        bottomBorder.setTranslationY(1130);

        budgetAdapter = new BudgetAdapter(this, budgetModels);
        budgetList.setLayoutManager(new LinearLayoutManager(this));
        budgetList.setAdapter(budgetAdapter);



        if (budgetList.getAdapter().getItemCount() == 0) {

            emptyNoteImage.startAnimation(openEmptyNote);
            emptyNoteText.startAnimation(openEmptyNote);

        } else {

            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }


        userSearch = HappyWed.search(Budget.this, "SELECT estBudget from user");

        if (userSearch.moveToNext()){
            loadBudget.setText(userSearch.getString(0));
        }


        loadAllItem();


    }

    public void gotoAddBudget(View view) {

        isPopUpOpen = true;
        bottomBorder.animate().translationY(0).setDuration(800).start();
        addBudgetlistBtn.setAlpha((float) 0.0);

        budgetList.animate().alpha(0.0f).setStartDelay(500);

        if (budgetList.getAdapter().getItemCount() == 0) {
            emptyNoteText.startAnimation(closeEmptyNote);
            emptyNoteImage.startAnimation(closeEmptyNote);
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }

    }

    public void gotoCancelBudget(View view) {

        isPopUpOpen = false;
        budgetList.animate().alpha(1.0f).setStartDelay(150);
        titleText.setText(null);
        estimateText.setText(null);
        titleText.setError(null);

        if (budgetList.getAdapter().getItemCount() == 0) {

            bottomBorder.animate().translationY(1130).setDuration(800).start();
            addBudgetlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
            emptyNoteImage.startAnimation(openEmptyNote);
            emptyNoteImage.setVisibility(View.VISIBLE);
            emptyNoteText.setVisibility(View.VISIBLE);

        } else {
            bottomBorder.animate().translationY(1130).setDuration(800).start();
            addBudgetlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
            emptyNoteImage.setVisibility(View.GONE);
            emptyNoteText.setVisibility(View.GONE);
        }

    }

    public static boolean isEdit= false;
    public static int updateId= 0;


    Cursor searchLast, sumFullCost, userSearch;
    String userId;

    public void gotoDoneBudget(View view) {





        if (!titleText.getText().toString().isEmpty()) {

            isPopUpOpen = false;

            if (!isEdit) {
                Log.d("INSIDE_", budgetInfoText.getText().toString());

        HappyWed.iud(Budget.this, "INSERT INTO budget (budgetInfo, budgetCost) " +
                "VALUES ('" + budgetInfoText.getText().toString() + "','" + Double.parseDouble(budgetCostText.getText().toString()) + "')");

                Log.d("INSIDE_", "AFTER_SAVED");

        searchLast = HappyWed.search(Budget.this, "SELECT * FROM budget WHERE budgetId = (SELECT MAX(budgetId)  FROM budget)");

        if (searchLast.moveToNext()) {
            budgetModels.add(new BudgetModel()
                    .setBudgetId(Integer.parseInt(searchLast.getString(0)))
                    .setTitle(searchLast.getString(1))
                    .setEstimatedCost(Double.parseDouble(searchLast.getString(2))));

            budgetAdapter.notifyDataSetChanged();
            titleText.setText(null);
            estimateText.setText(null);

            Toast.makeText(this, "Successfully Saved!", Toast.LENGTH_SHORT).show();

            loadAllItem();
        }else {
            if (updateId!=0){
                HappyWed.iud(this, "UPDATE budget " +
                        "SET budgetInfo='"+budgetInfoText.getText()+"', budgetCost='"+Double.parseDouble(budgetCostText.getText().toString())+"' WHERE budgetId='"+updateId+"' ");
                loadAllItem();
            }

        }

            }else {
                titleText.setError("Field cannot be empty!");
                titleText.requestFocus();
            }

            budgetList.animate().alpha(1.0f).setStartDelay(150);
            bottomBorder.animate().translationY(1130).setDuration(800).start();
            addBudgetlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
        }else{
            titleText.setError("Field cannot be empty!");
        }
    }

    public void loadAllItem(){



        Log.d("INSIDE_LOAD_ALL","___");
        budgetModels.clear();
        Log.d("INSIDE_LOAD_ALL","MODEL_CLEARED");

        searchReult = HappyWed.search(Budget.this, "SELECT * FROM budget");
        Log.d("INSIDE_LOAD_ALL","SEARCHED");

        while (searchReult.moveToNext()) {
            budgetModels.add(new BudgetModel()
                    .setBudgetId(Integer.parseInt(searchReult.getString(0)))
                    .setTitle(searchReult.getString(1))
                    .setEstimatedCost(Double.parseDouble(searchReult.getString(2))));
            Log.d("INSIDE_LOAD_ALL", "SEARCHED");

            budgetAdapter.notifyDataSetChanged();
        }

        sumFullCost = HappyWed.search(Budget.this, "SELECT SUM(budgetCost) FROM budget");

        if (sumFullCost.moveToNext()){
            if (sumFullCost.getString(0)==null){
                totalCostText.setText("0.0");
            }else {
                totalCostText.setText(sumFullCost.getString(0).toString());
            }
        }
//        sumChild = HappyWed.search(Budget.this, "SELECT SUM(childCount) FROM guest");



        if (budgetModels.isEmpty()) {

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
            budgetList.animate().alpha(1.0f).setStartDelay(150);
            titleText.setText(null);
            estimateText.setText(null);
            titleText.setError(null);

            if (budgetList.getAdapter().getItemCount() == 0) {

                bottomBorder.animate().translationY(1130).setDuration(800).start();
                addBudgetlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
                emptyNoteImage.startAnimation(openEmptyNote);
                emptyNoteImage.setVisibility(View.VISIBLE);
                emptyNoteText.setVisibility(View.VISIBLE);

            } else {
                bottomBorder.animate().translationY(1130).setDuration(800).start();
                addBudgetlistBtn.animate().alpha(1.0F).setStartDelay(800).start();
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
