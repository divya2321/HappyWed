package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.example.happywed.Adapters.BusinessCategoryTextAdapter;
import com.example.happywed.Adapters.BusinessProductCardAdapter;
import com.example.happywed.Adapters.BusinessShopProductPreviewAdapter;
import com.example.happywed.Adapters.ShopReviewAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessProductCardModel;
import com.example.happywed.Models.BusinessProductModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusinessShopPreview extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private Toolbar toolBar;
    private CircleImageView businessProfilePicture;
    private TextView businessNameTxt, businessDescriptionTxt,businessContact1Txt,businessContact2Txt,businessAddressTxt;
    private RecyclerView categoryRecyclerView,productRecyclerView;

    private GoogleMap mMap;
    private double latitude,longitude;
    private Location placeLocation;
    private static final int LOCATIONREQUESTCODE =  1;

    BusinessCategoryTextAdapter categoryAdapter;
    ArrayList<String> categoryList = new ArrayList<String>();

    BusinessShopProductPreviewAdapter productAdapter;
    ArrayList<BusinessProductModel> productList = new ArrayList<BusinessProductModel>();

    private String shopKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_shop_preview);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        businessProfilePicture = findViewById(R.id.businessProfilePicture);
        businessNameTxt = findViewById(R.id.businessName);
        businessDescriptionTxt = findViewById(R.id.businessDescription);
        businessContact1Txt = findViewById(R.id.businessContact1);
        businessContact2Txt = findViewById(R.id.businessContact2);
        businessAddressTxt = findViewById(R.id.businessAddress);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        productRecyclerView = findViewById(R.id.productRecyclerView);

        if (getSupportActionBar() != null){
            setSupportActionBar(toolBar);
        }
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        shopKey =  getIntent().getStringExtra("shopKey");

        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        placeLocation = new Location(LocationManager.GPS_PROVIDER);

        categoryList.clear();
        categoryAdapter = new BusinessCategoryTextAdapter(this, categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        productList.clear();
        productAdapter = new BusinessShopProductPreviewAdapter(this, productList);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productRecyclerView.setAdapter(productAdapter);


        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {



                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String profilePic = ((Map<String, String>) dataSnapshot.getValue()).get("profilePic");
                            String name = ((Map<String, String>) dataSnapshot.getValue()).get("shopName");
                            ArrayList<String> categories = ((Map<String, ArrayList<String>>) dataSnapshot.getValue()).get("shopCategories");
                            String description = ((Map<String, String>) dataSnapshot.getValue()).get("shopDescription");
                            String tp1 = ((Map<String, String>) dataSnapshot.getValue()).get("contact1");
                            String tp2 = ((Map<String, String>) dataSnapshot.getValue()).get("contact2");
                            String address = ((Map<String, String>) dataSnapshot.getValue()).get("address");
                            String district = ((Map<String, String>) dataSnapshot.getValue()).get("district");

                            if((((Map<String, String>) dataSnapshot.getValue()).get("latitude"))!= null) {
                                if((((Map<String, String>) dataSnapshot.getValue()).get("longitude"))!= null) {
                                    latitude = ((Map<String, Double>) dataSnapshot.getValue()).get("latitude");
                                    longitude = ((Map<String, Double>) dataSnapshot.getValue()).get("longitude");
                                }
                            }else{
                                latitude = 1.1;
                                longitude = 1.1;
                            }

                            if(profilePic!=null){

                            Picasso.get().load(Uri.parse(profilePic)).into(businessProfilePicture);
                            }else{
                                businessProfilePicture.setImageResource(R.drawable.defaultshoppic);
                            }
                            businessNameTxt.setText(name.trim());

                            for (String category : categories) {
                                categoryList.add(category);
                                categoryAdapter.notifyDataSetChanged();
                            }

                            businessDescriptionTxt.setText(description);
                            businessContact1Txt.setText(Html.fromHtml("<u>"+tp1+"</u>"));
                            if (tp2 != null) {
                                businessContact2Txt.setText(Html.fromHtml("<u>"+tp2+"</u>"));
                            }
                            String realAddress = "";
                            if(address != null) {
                                String allAdd[] = address.trim().split(",");
                                for (String a : allAdd) {
                                    if (!a.equals("null")) {
                                        realAddress += a + "\n";
                                    }
                                }
                            }
                            businessAddressTxt.setText(realAddress + district);

                            if (latitude != 1.1 && longitude != 1.1) {

                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(BusinessShopPreview.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATIONREQUESTCODE);
                            } else {
                                placeLocation.setLatitude(latitude);
                                placeLocation.setLongitude(longitude);
                                setSelectedLocation(placeLocation);
                            }

                        }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (final DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {

                                String name = ((Map<String, String>) childSnapshot1.getValue()).get("productName");
                                String price = ((Map<String, String>) childSnapshot1.getValue()).get("productPrice");
                                String description = ((Map<String, String>) childSnapshot1.getValue()).get("productDescription");
                                ArrayList<String> images = ((Map<String, ArrayList<String>>) childSnapshot1.getValue()).get("productImages");

                                BusinessProductModel productModel = new BusinessProductModel()
                                        .setProductName(name)
                                        .setProductPrice(price)
                                        .setProductDescription(description)
                                        .setProductImages(images)
                                        .setMainProductImage(images.get(0));

                                productList.add(productModel);

                                productAdapter.notifyDataSetChanged();

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
                    placeLocation.setLatitude(latitude);
                    placeLocation.setLongitude(longitude);
                    setSelectedLocation(placeLocation);
                }
            }
        }
    }



    private void setSelectedLocation(Location location){
        if(location != null ) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
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
        databaseReference.orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object> hm = new HashMap<String, Object>();
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
