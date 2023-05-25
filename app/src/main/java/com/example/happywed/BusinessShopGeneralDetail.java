package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happywed.Adapters.BusinessCategoryEditAdapter;
import com.example.happywed.Adapters.BusinessCategoryPopupAdapter;
import com.example.happywed.Adapters.BusinessShopListAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessShopModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BusinessShopGeneralDetail extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private Toolbar toolBar;
    private RecyclerView shopCategoryList;
    private EditText shopNameTxt,shopDescriptionTxt,shopContact1Txt,shopContact2Txt,shopLocationNoTxt,shopLocationStreet1Txt,shopLocationStreet2Txt;
    private Spinner shopLocationCitySpinner,shopLocationDistrictSpinner;
    private ImageView nameEditBtn,categoryEditBtn,descriptionEditBtn,contact1EditBtn,contact2EditBtn,noEditBtn,street1EditBtn,street2EditBtn,cityEditBtn,districtEditBtn;
    private TextView categoryTxtView;
    private View progressBar;

    private ArrayAdapter citiesAdapter;
    private String citiesList[];

    private ArrayAdapter districtsAdapter;
    private String districtsList[];

    private GoogleMap mMap;
    static LatLng savedLocation = null;

    ArrayList<String> categories= new ArrayList<String>();
    BusinessCategoryEditAdapter categoryEditAdapter;

    private static final int LOCATIONREQUESTCODE =  1;
    private static final int LOCATIONSAVEDCODE =  2;

    private static final String DETAILSUPDATED = "DetailsUpdated";


    private String shopKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_shop_general_detail);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        shopKey =  getIntent().getStringExtra("shopKey");

        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        shopCategoryList = findViewById(R.id.shopCategoryList);
        shopNameTxt = findViewById(R.id.shopName);
        shopDescriptionTxt = findViewById(R.id.shopDescription);
        shopContact1Txt = findViewById(R.id.shopContact1);
        shopContact2Txt = findViewById(R.id.shopContact2);
        shopLocationNoTxt = findViewById(R.id.shopLocationNo);
        shopLocationStreet1Txt = findViewById(R.id.shopLocationStreet1);
        shopLocationStreet2Txt = findViewById(R.id.shopLocationStreet2);
        shopLocationCitySpinner = findViewById(R.id.shopLocationCity);
        shopLocationDistrictSpinner = findViewById(R.id.shopLocationDistrict);
        categoryTxtView = findViewById(R.id.categoryTxtView);


        nameEditBtn = findViewById(R.id.nameEditBtn);
        categoryEditBtn = findViewById(R.id.categoryEditBtn);
        descriptionEditBtn = findViewById(R.id.descriptionEditBtn);
        contact1EditBtn = findViewById(R.id.contact1EditBtn);
        contact2EditBtn = findViewById(R.id.contact2EditBtn);
        noEditBtn = findViewById(R.id.noEditBtn);
        street1EditBtn = findViewById(R.id.street1EditBtn);
        street2EditBtn = findViewById(R.id.street2EditBtn);
        cityEditBtn = findViewById(R.id.cityEditBtn);
        districtEditBtn = findViewById(R.id.districtEditBtn);
        toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        progressBar = findViewById(R.id.userProgressBar);

        progressBar.setVisibility(View.GONE);

        if (getSupportActionBar() != null){
            setSupportActionBar(toolBar);
        }
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        citiesList = getResources().getStringArray(R.array.locations_city);
        citiesAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,citiesList);
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shopLocationCitySpinner.setAdapter(citiesAdapter);

        districtsList = getResources().getStringArray(R.array.locations_district);
        districtsAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,districtsList);
        districtsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shopLocationDistrictSpinner.setAdapter(districtsAdapter);

        savedLocation = null;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.shopLocatinMap);
        mapFragment.getMapAsync(this);

        categories.clear();
        categories.add(getResources().getString(R.string.jewellery));
        categories.add(getResources().getString(R.string.dresses));
        categories.add(getResources().getString(R.string.salon));
        categories.add(getResources().getString(R.string.hotel));
        categories.add(getResources().getString(R.string.poru));
        categories.add(getResources().getString(R.string.decoration));
        categories.add(getResources().getString(R.string.photographers));
        categories.add(getResources().getString(R.string.invitations));
        categories.add(getResources().getString(R.string.cake));
        categories.add(getResources().getString(R.string.music));
        categories.add(getResources().getString(R.string.caterine));
        categories.add(getResources().getString(R.string.vehicle));
        categories.add(getResources().getString(R.string.wedding_planner));
        categories.add(getResources().getString(R.string.event_planner));
        categories.add(getResources().getString(R.string.other));



        categoryEditAdapter = new BusinessCategoryEditAdapter(this,categories);


        allEditTxtEnableFalse();


        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {

                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String name = ((Map<String, String>) dataSnapshot.getValue()).get("shopName");
                            ArrayList<String> categoryList = ((Map<String, ArrayList<String>>) dataSnapshot.getValue()).get("shopCategories");
                            String description = ((Map<String, String>) dataSnapshot.getValue()).get("shopDescription");
                            String contact1 = ((Map<String, String>) dataSnapshot.getValue()).get("contact1");
                            String contact2 = ((Map<String, String>) dataSnapshot.getValue()).get("contact2");
                            String address = ((Map<String, String>) dataSnapshot.getValue()).get("address");
                            String district = ((Map<String, String>) dataSnapshot.getValue()).get("district");
                            Double latitude = ((Map<String, Double>) dataSnapshot.getValue()).get("latitude");
                            Double longitude = ((Map<String, Double>) dataSnapshot.getValue()).get("longitude");

                            shopNameTxt.setText(name);

                            if(description == null){
                                shopDescriptionTxt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.red_corner_background));
                                shopDescriptionTxt.setError("No Shop Description");
                            }else{
                                shopDescriptionTxt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.ash_corner_background));
                                shopDescriptionTxt.setText(description.trim());
                            }


                            categoryEditAdapter.checkedItem.clear();
                            for(String oldCategoryTxt: categoryList){
                                categoryEditAdapter.checkedItem.add(oldCategoryTxt);
                            }
                            shopCategoryList.setLayoutManager(new GridLayoutManager(BusinessShopGeneralDetail.this,2));
                            shopCategoryList.setAdapter(categoryEditAdapter);


                            if(contact1 == null){
                                shopContact1Txt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.red_corner_background));
                                shopContact1Txt.setError("No Contact");
                            }else{
                                shopContact1Txt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.ash_corner_background));
                                shopContact1Txt.setText(contact1.trim());
                            }

                            shopContact2Txt.setText(contact2);

                            if(address == null){
                                shopLocationNoTxt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.red_corner_background));
                                shopLocationNoTxt.setError("No Address");

                                shopLocationStreet1Txt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.red_corner_background));
                                shopLocationStreet1Txt.setError("No Address");
                            }else{

                                shopLocationNoTxt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.ash_corner_background));
                                shopLocationStreet1Txt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.ash_corner_background));

                                String allAdd[] = address.split(",");

                                shopLocationNoTxt.setText(allAdd[0].trim());
                                shopLocationStreet1Txt.setText(allAdd[1].trim());

                                if(allAdd[2].trim().equals("null")){
                                    shopLocationStreet2Txt.setText("");
                                }else {
                                    shopLocationStreet2Txt.setText(allAdd[2].trim());
                                }
                                shopLocationCitySpinner.setSelection(citiesAdapter.getPosition(allAdd[3].trim()));
                            }


                            shopLocationDistrictSpinner.setSelection(districtsAdapter.getPosition(district));

                            if(latitude != null | longitude != null) {
                                if (latitude == 1.1 && longitude == 1.1) {
                                    savedLocation = null;
                                } else {
                                    savedLocation = new LatLng(latitude, longitude);
                                }
                            }else{
                                savedLocation = null;
                            }
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(BusinessShopGeneralDetail.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATIONREQUESTCODE);
                            } else {
                                setSelectedLocation(savedLocation);
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


        nameEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopNameTxt.setEnabled(true);
                nameEditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        categoryEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                categoryEditAdapter.isEditable = true;
                categoryEditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        descriptionEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopDescriptionTxt.setEnabled(true);
                descriptionEditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        contact1EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopContact1Txt.setEnabled(true);
                contact1EditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        contact2EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopContact2Txt.setEnabled(true);
                contact2EditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        noEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopLocationNoTxt.setEnabled(true);
                noEditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        street1EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopLocationStreet1Txt.setEnabled(true);
                street1EditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        street2EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopLocationStreet2Txt.setEnabled(true);
                street2EditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        cityEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopLocationCitySpinner.setEnabled(true);
                cityEditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });

        districtEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEditTxtEnableFalse();
                shopLocationDistrictSpinner.setEnabled(true);
                districtEditBtn.setImageResource(R.drawable.edit_select_btn);
            }
        });


//        shopDescriptionTxt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                if(charSequence.toString().isEmpty()){
//                    shopDescriptionTxt.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_corner_background));
//                }else{
//                    shopDescriptionTxt.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ash_corner_background));
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
    }






    public void selectLocationMap(View view) {

        startActivityForResult(new Intent(getApplicationContext(),BusinessShopGeneralDetailsMap.class).putExtra("savedLocation",savedLocation),LOCATIONSAVEDCODE);
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == LOCATIONREQUESTCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    setSelectedLocation(savedLocation);
                }
            }
        }
    }

    private void setSelectedLocation(LatLng location){
        if(location != null ) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(location).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOCATIONSAVEDCODE){
            setSelectedLocation(savedLocation);
        }
    }

    private void allEditTxtEnableFalse(){
        categoryEditAdapter.isEditable = false;
        shopNameTxt.setEnabled(false);
        shopDescriptionTxt.setEnabled(false);
        shopContact1Txt.setEnabled(false);
        shopContact2Txt.setEnabled(false);
        shopLocationNoTxt.setEnabled(false);
        shopLocationStreet1Txt.setEnabled(false);
        shopLocationStreet2Txt.setEnabled(false);
        shopLocationCitySpinner.setEnabled(false);
        shopLocationDistrictSpinner.setEnabled(false);

        nameEditBtn.setImageResource(R.drawable.edit_btn);
        categoryEditBtn.setImageResource(R.drawable.edit_btn);
        descriptionEditBtn.setImageResource(R.drawable.edit_btn);
        contact1EditBtn.setImageResource(R.drawable.edit_btn);
        contact2EditBtn.setImageResource(R.drawable.edit_btn);
        noEditBtn.setImageResource(R.drawable.edit_btn);
        street1EditBtn.setImageResource(R.drawable.edit_btn);
        street2EditBtn.setImageResource(R.drawable.edit_btn);
        cityEditBtn.setImageResource(R.drawable.edit_btn);
        districtEditBtn.setImageResource(R.drawable.edit_btn);
    }


    String shopName,description,tp1,tp2,no,street1,street2,city,district;
    public void saveGeneralDetails(View view) {

        shopName = shopNameTxt.getText().toString().trim();
        description = shopDescriptionTxt.getText().toString().trim();
        tp1 = shopContact1Txt.getText().toString().trim();
        tp2 = shopContact2Txt.getText().toString().trim();
        no = shopLocationNoTxt.getText().toString().trim();
        street1 = shopLocationStreet1Txt.getText().toString().trim();
        street2 = shopLocationStreet2Txt.getText().toString().trim();
        city = shopLocationCitySpinner.getSelectedItem().toString().trim();
        district = shopLocationDistrictSpinner.getSelectedItem().toString().trim();

        if(!shopName.isEmpty()){

            if(!description.isEmpty()){

                if(categoryEditAdapter.checkedItem.size()!=0){

                if(!tp1.isEmpty()){

                    if(!no.isEmpty()){

                        if(!street1.isEmpty()){


                            progressBar.setVisibility(View.VISIBLE);

                            if(tp2.isEmpty()){
                                tp2 = null;
                            }

                            if(street2.isEmpty()){
                                street2 = null;
                            }

                            databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                        String address = no + "," + street1 + "," + street2 + "," + city;

                                        final Map<String, Object> updatedMap = new HashMap<String, Object>();

                                        updatedMap.put("shopName", shopName);
                                        updatedMap.put("shopCategories", categoryEditAdapter.checkedItem);
                                        updatedMap.put("shopDescription", description);
                                        updatedMap.put("contact1", tp1);
                                        updatedMap.put("contact2", tp2);
                                        updatedMap.put("address", address);
                                        updatedMap.put("district", district);
                                        updatedMap.put("confirmation", "1");

                                        if(savedLocation == null){
                                            updatedMap.put("latitude", 1.1);
                                            updatedMap.put("longitude", 1.1);
                                        }else{
                                            updatedMap.put("latitude", savedLocation.latitude);
                                            updatedMap.put("longitude", savedLocation.longitude);
                                        }

                                         databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if(task.isSuccessful()){
                                                     Log.d(DETAILSUPDATED,"Update details to db");
                                                     Toast.makeText(BusinessShopGeneralDetail.this, "Successfully saved", Toast.LENGTH_LONG).show();
                                                     BusinessShopProfile.comformatonAlert.dismiss();
                                                     progressBar.setVisibility(View.GONE);
                                                     allEditTxtEnableFalse();
                                                     resetEditTxt();
                                                 }else{
                                                     Log.w(DETAILSUPDATED,"Cannot update details to db: "+task.getException());
                                                     Toast.makeText(BusinessShopGeneralDetail.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                     progressBar.setVisibility(View.GONE);
                                                 }
                                             }
                                         });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });




                        }else{
                            shopLocationStreet1Txt.setError("Enter address street");
                            shopLocationStreet1Txt.requestFocus();
                        }

                    }else{
                        shopLocationNoTxt.setError("Enter address no");
                        shopLocationNoTxt.requestFocus();
                    }

                }else{
                    shopContact1Txt.setError("Enter conatact number");
                    shopContact1Txt.requestFocus();
                }

            }else{
                    categoryTxtView.setError("Select category");
                    categoryTxtView.requestFocus();
            }

        }else{
                shopDescriptionTxt.setError("Enter shop description");
                shopDescriptionTxt.requestFocus();
        }

        }else{
            shopNameTxt.setError("Enter shop name");
            shopNameTxt.requestFocus();
        }

    }

    private void resetEditTxt(){
        shopDescriptionTxt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.ash_corner_background));
        shopContact1Txt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.ash_corner_background));
        shopLocationNoTxt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.ash_corner_background));
        shopLocationStreet1Txt.setBackgroundDrawable(ContextCompat.getDrawable(BusinessShopGeneralDetail.this, R.drawable.ash_corner_background));
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
        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object>  hm = new HashMap<String, Object>();
                    hm.put("status", status);

                    databaseReference.child(snaps.getKey()).updateChildren(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
