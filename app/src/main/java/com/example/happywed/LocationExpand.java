package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.happywed.Adapters.LocationAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.LocationModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationExpand extends AppCompatActivity {

    static ExpandableListView sampleExpandList;
    static LocationAdapter sampleExpandAdapter;
    List<String> rootList;
    HashMap<String, List<String>> listDetails;
    private HashMap<String,ArrayList<String>> locationDetails = new HashMap<String,ArrayList<String>>();

    private SearchView searhLocationText;

    Toolbar toolBar;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_expand);

        databaseReference = HappyWedDB.getDBConnection();
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();

        toolBar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.location_close_btn);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sampleExpandList = (ExpandableListView) findViewById(R.id.locationExpandList);
        searhLocationText = (SearchView) findViewById(R.id.searhLocationText);
        locationDetails = LocationModel.getLocation();

        sampleExpandAdapter = new LocationAdapter(this,locationDetails);

        sampleExpandList.setAdapter(sampleExpandAdapter);

        sampleExpandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int rootPosition, int childPosition, long l) {
                Toast.makeText(getApplicationContext(),LocationAdapter.locations.get(LocationAdapter.locationsKey.get(rootPosition)).get(childPosition),Toast.LENGTH_LONG).show();
                CommonCategoryView.flterLocation(LocationAdapter.locations.get(LocationAdapter.locationsKey.get(rootPosition)).get(childPosition));
                LocationExpand.this.finish();
                return true;
            }
        });

//        sampleExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        rootList.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
//
//                LocationAdapter.locations.get(LocationAdapter.locationsKey.get(groupPosition));
//            }
//        });
//
//        sampleExpandList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        rootList.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });


        searhLocationText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                sampleExpandAdapter.filterData(s);
                return false;
            }
        });

    }

    public void selectAllSriLanka(View view) {
        CommonCategoryView.flterLocation(getString(R.string.all_of_srilanka));
        LocationExpand.this.finish();
    }


    public static void expandAll() {
        int count = sampleExpandAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            sampleExpandList.expandGroup(i);
        }
    }

    public static void unExpandAll() {
        int count = sampleExpandAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            sampleExpandList.collapseGroup(i);
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
