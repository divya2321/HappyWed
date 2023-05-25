package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happywed.Adapters.BusinessCategoryTextAdapter;
import com.example.happywed.Adapters.BusinessProductCardAdapter;
import com.example.happywed.Adapters.ShopReviewAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.Review;
import com.example.happywed.Models.BusinessProductCardModel;
import com.example.happywed.Models.BusinessProductModel;
import com.example.happywed.Models.ShopReviewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusinessProfile extends AppCompatActivity implements OnMapReadyCallback {


    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private Toolbar toolBar;
    private RecyclerView categoryRecyclerView, productRecyclerView, reviewRecyclerView;
    private CircleImageView businessProfilePic,userProfilePic;
    private TextView businessNameTxt,businessDescriptionTxt,businessContact1Txt,businessContact2Txt,businessAddressTxt,overallRateTxt,overallRateCountTxt;
    private EditText reviewEditTxt;
    private RatingBar reviewRateBar,overallRatingBar;
    private Button reviewSubmitBtn;
    private ProgressBar oneProgressBar,twoProgressBar,threeProgressBar,fourProgressBar,fiveProgressBar;
    private ImageView businessFav;

    private BusinessCategoryTextAdapter categoryAdapter;
    private ArrayList<String> categoryList = new ArrayList<String>();

    private BusinessProductCardAdapter productAdapter;
    private ArrayList<BusinessProductModel> productList = new ArrayList<BusinessProductModel>();

    private ShopReviewAdapter reviewAdapter;
    private ArrayList<ShopReviewModel> reviewList = new ArrayList<ShopReviewModel>();

    private GoogleMap mMap;
    private double latitude,longitude;
    private Location placeLocation;
    private static final int LOCATIONREQUESTCODE =  1;

    private String ownerKey;
    private String shopKey;

    private boolean processFav = false;

    private String contact1,contact2;

    private Snackbar statusAlert;
    String businessId;

    Button chatAccessBtn, callAccessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_profile);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ownerKey =getIntent().getStringExtra("ownerKey");
        shopKey = getIntent().getStringExtra("shopKey");

        toolBar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolBar);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.wishList:
                        startActivity(new Intent(getApplicationContext(),WishList.class));
                        return true;
                }
                return false;
            }
        });


        databaseReference = HappyWedDB.getDBConnection();
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        categoryRecyclerView = (RecyclerView) findViewById(R.id.categoryView);
        productRecyclerView = (RecyclerView) findViewById(R.id.productView);
        reviewRecyclerView = (RecyclerView) findViewById(R.id.shopReview);
        businessProfilePic = findViewById(R.id.businessProfilePic);
        businessNameTxt = findViewById(R.id.businessName);
        businessFav = findViewById(R.id.businessFav);
        businessDescriptionTxt = findViewById(R.id.businessDescription);
        businessContact1Txt = findViewById(R.id.businessContact1);
        businessContact2Txt = findViewById(R.id.businessContact2);
        businessAddressTxt = findViewById(R.id.businessAddress);
        userProfilePic = findViewById(R.id.userProfilePic);
        reviewEditTxt = findViewById(R.id.reviewEditTxt);
        reviewRateBar = findViewById(R.id.reviewRateBar);
        reviewSubmitBtn = findViewById(R.id.reviewSubmitBtn);
        overallRateTxt = findViewById(R.id.overallRateTxt);
        overallRatingBar = findViewById(R.id.overallRatingBar);
        overallRateCountTxt = findViewById(R.id.overallRateCountTxt);
        oneProgressBar = findViewById(R.id.oneProgressBar);
        twoProgressBar = findViewById(R.id.twoProgressBar);
        threeProgressBar = findViewById(R.id.threeProgressBar);
        fourProgressBar = findViewById(R.id.fourProgressBar);
        fiveProgressBar = findViewById(R.id.fiveProgressBar);

        chatAccessBtn = findViewById(R.id.chatAccessbtn);
        callAccessButton = findViewById(R.id.callAccessbtn);

        statusAlert = Snackbar.make(businessNameTxt,"This shop is deactivated!",Snackbar.LENGTH_INDEFINITE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        placeLocation = new Location(LocationManager.GPS_PROVIDER);

        categoryList.clear();
        categoryAdapter = new BusinessCategoryTextAdapter(this, categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        productList.clear();
        productAdapter = new BusinessProductCardAdapter(this, productList);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productRecyclerView.setAdapter(productAdapter);

        reviewList.clear();
        reviewAdapter = new ShopReviewAdapter(this, reviewList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        Picasso.get().load(currentProfile.getPhotoUrl()).into(userProfilePic);

        databaseReference.child("businesses").child(ownerKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                businessId = ((Map<String, String>) dataSnapshot.getValue()).get("uid");

                if(currentProfile.getUid().equals(businessId)){
                    reviewSubmitBtn.setEnabled(false);
                    reviewSubmitBtn.setBackgroundColor(getResources().getColor(R.color.dark_ash));

                    chatAccessBtn.setEnabled(false);
                    chatAccessBtn.setBackground(getResources().getDrawable(R.drawable.disabled_rounded));


                    callAccessButton.setEnabled(false);
                    callAccessButton.setBackground(getResources().getDrawable(R.drawable.disabled_rounded));


                }else{
                    reviewSubmitBtn.setEnabled(true);
                    reviewSubmitBtn.setBackgroundColor(getResources().getColor(R.color.button_end));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).child("Favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(currentProfile.getUid())) {
                            businessFav.setImageResource(R.drawable.favourite);
                            businessFav.setTag("fav");
                        } else {
                            businessFav.setImageResource(R.drawable.unfavourite);
                            businessFav.setTag("unFav");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                String status = ((Map<String, String>) dataSnapshot.getValue()).get("status");
                String profilePic = ((Map<String, String>) dataSnapshot.getValue()).get("profilePic");
                String name = ((Map<String, String>) dataSnapshot.getValue()).get("shopName");
                ArrayList<String> categories = ((Map<String, ArrayList<String>>) dataSnapshot.getValue()).get("shopCategories");
                String description = ((Map<String, String>) dataSnapshot.getValue()).get("shopDescription");
                contact1 = ((Map<String, String>) dataSnapshot.getValue()).get("contact1");
                contact2= ((Map<String, String>) dataSnapshot.getValue()).get("contact2");
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

                if(status.equals("0")){
                    statusAlert.show();
                }else{
                    statusAlert.dismiss();
                }

                if(profilePic!=null){

                    Picasso.get().load(Uri.parse(profilePic)).into(businessProfilePic);
                }else{
                    businessProfilePic.setImageResource(R.drawable.defaultshoppic);
                }

                businessNameTxt.setText(name);

                for(String category: categories){
                    categoryList.add(category);
                    categoryAdapter.notifyDataSetChanged();
                }

                businessDescriptionTxt.setText(description);
                businessContact1Txt.setText(Html.fromHtml("<u>"+contact1+"</u>"));

                if(contact2 != null){

                businessContact2Txt.setText(Html.fromHtml("<u>"+contact2+"</u>"));
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
                        ActivityCompat.requestPermissions(BusinessProfile.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATIONREQUESTCODE);
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


        databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {

                    String name = ((Map<String, String>) childSnapshot1.getValue()).get("productName");
                    String price = ((Map<String, String>) childSnapshot1.getValue()).get("productPrice");
                    ArrayList<String> images = ((Map<String, ArrayList<String>>) childSnapshot1.getValue()).get("productImages");

                   final BusinessProductModel productModel = new BusinessProductModel()
                            .setOwnerKey(ownerKey)
                            .setBusinessKey(shopKey)
                            .setProductKey(childSnapshot1.getKey())
                            .setUid(currentProfile.getUid())
                            .setProductName(name)
                            .setProductPrice(price)
                            .setProductImages(images)
                            .setMainProductImage(images.get(0));


                    databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).child("Products").child(childSnapshot1.getKey()).child("Favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(currentProfile.getUid())) {

                                productModel.setFav(true);
                                productList.add(productModel);
                                productAdapter.notifyDataSetChanged();
                            } else {
                                productModel.setFav(false);
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

        readReview(new ReviewCallback() {
            @Override
            public void onCallback(final ShopReviewModel reviewModel) {

                databaseReference.child("users").orderByChild("uid").equalTo(reviewModel.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){

                            String name = ((Map<String, String>) childSnapshot.getValue()).get("userName");
                            String proPic = ((Map<String, String>) childSnapshot.getValue()).get("profilePicUrl");

                            reviewModel.setUserName(name);
                            reviewModel.setUserProfilePic(proPic);
                            reviewList.add(reviewModel);
                            reviewAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        businessFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                processFav = true;

                databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).child("Favorite").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(processFav) {
                            if (dataSnapshot.hasChild(currentProfile.getUid())) {
                                processFav = false;
                                databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).child("Favorite").child(currentProfile.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Snackbar.make(view,"Item Removed from WishList",Snackbar.LENGTH_LONG).show();
                                            businessFav.setImageResource(R.drawable.unfavourite);
                                            businessFav.setTag("unFav");
                                        }
                                    }
                                });
                            } else {
                                processFav = false;
                                databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).child("Favorite").child(currentProfile.getUid()).setValue("random value").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Snackbar.make(view,"Item Added to WishList",Snackbar.LENGTH_LONG).show();
                                            businessFav.setImageResource(R.drawable.favourite);
                                            businessFav.setTag("fav");
                                        }
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


        businessContact1Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+businessContact1Txt.getText().toString().trim()));
                startActivity(intent);
            }
        });

        businessContact2Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(businessContact2Txt.getText()!=null){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+businessContact2Txt.getText().toString().trim()));
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.wishlist_menu, menu);

        return true;
    }

    public void gotoChat(View view) {
        startActivity(new Intent(BusinessProfile.this, Chat.class).putExtra("ownerKey", ownerKey).putExtra("shopKey", shopKey));

    }


    interface ReviewCallback {
        void onCallback(ShopReviewModel reviewModel);
    }

    int allRateCount = 0;
    private void readReview(final ReviewCallback reviewCallback) {

        databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                allRateCount = (int) dataSnapshot.getChildrenCount();
                overallRateCountTxt.setText(String.valueOf(allRateCount));
                for(DataSnapshot childSnapshot1 : dataSnapshot.getChildren()){

                    String uid = ((Map<String, String>) childSnapshot1.getValue()).get("uid");
                    String review = ((Map<String, String>) childSnapshot1.getValue()).get("review");
                    String rate = ((Map<String, String>) childSnapshot1.getValue()).get("rate");
                    String reviewedDate = ((Map<String, String>) childSnapshot1.getValue()).get("reviewedDate");

                    setRatings(Float.parseFloat(rate));

                    ShopReviewModel model = new ShopReviewModel()
                            .setUserId(uid)
                            .setReview(review)
                            .setRate( Float.parseFloat(rate))
                            .setReviewedDate(reviewedDate);

                    reviewCallback.onCallback(model);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private float allRateSum = 0;
    private int fiveRate = 0;
    private int fourRate = 0;
    private int threeRate = 0;
    private int twoRate = 0;
    private int oneRate = 0;

    private void setRatings(float rate){

        allRateSum += rate;
        if(rate == 5.0){
            fiveRate += 1;
            fiveProgressBar.setProgress(fiveRate*100/allRateCount);
        }else if(rate == 4.0){
            fourRate += 1;
            fourProgressBar.setProgress(fourRate*100/allRateCount);
        }else if(rate == 3.0){
            threeRate += 1;
            threeProgressBar.setProgress(threeRate*100/allRateCount);
        }else if(rate == 2.0){
            twoRate += 1;
            twoProgressBar.setProgress(twoRate*100/allRateCount);
        }else if(rate == 1.0){
            oneRate += 1;
            oneProgressBar.setProgress(oneRate*100/allRateCount);
        }

        double overallCount = Math.round(allRateSum/allRateCount * 10) / 10.0;
        overallRateTxt.setText(String.valueOf(overallCount));
        overallRatingBar.setRating((float) overallCount);
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


    private float rate;
    private String review;
    public void submitReiew(View view) {

        rate = reviewRateBar.getRating();
        review = reviewEditTxt.getText().toString();

        if((rate!=0.0) && (!review.isEmpty()) ){

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aa");
            Review reviewModel = new Review()
                    .setUid(currentProfile.getUid())
                    .setRate(String.valueOf(rate))
                    .setReview(review.trim())
                    .setReviewedDate(formatter.format(new Date()));

            databaseReference.child("businesses").child(ownerKey).child("Shops").child(shopKey).child("Reviews").push().setValue(reviewModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        reviewEditTxt.setText("");
                        reviewRateBar.setRating(0.0f);
                        Toast.makeText(BusinessProfile.this, "Thank you for your review!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    public void goToCall(View view) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+contact1));
        startActivity(intent);
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
