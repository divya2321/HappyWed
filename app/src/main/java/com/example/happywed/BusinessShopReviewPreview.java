package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.example.happywed.Adapters.ShopReviewAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessProductModel;
import com.example.happywed.Models.ShopReviewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BusinessShopReviewPreview extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private Toolbar toolBar;
    private TextView overallRateTxt,overallRateCountTxt;
    private RatingBar overallRatingBar;
    private ProgressBar oneProgressBar,twoProgressBar,threeProgressBar,fourProgressBar,fiveProgressBar;
    private RecyclerView reviewRecyclerView;

    private ShopReviewAdapter reviewAdapter;
    private ArrayList<ShopReviewModel> reviewList = new ArrayList<ShopReviewModel>();

    private RecyclerViewSkeletonScreen skeletonScreen;

    private String shopKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_shop_review_preview);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        databaseReference = HappyWedDB.getDBConnection();
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        overallRateTxt = findViewById(R.id.overallRateTxt);
        overallRateCountTxt = findViewById(R.id.overallRateCountTxt);
        overallRatingBar = findViewById(R.id.overallRatingBar);
        oneProgressBar = findViewById(R.id.oneProgressBar);
        twoProgressBar = findViewById(R.id.twoProgressBar);
        threeProgressBar = findViewById(R.id.threeProgressBar);
        fourProgressBar = findViewById(R.id.fourProgressBar);
        fiveProgressBar = findViewById(R.id.fiveProgressBar);
        reviewRecyclerView = findViewById(R.id.shopReview);

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

        reviewList.clear();
        reviewAdapter = new ShopReviewAdapter(this, reviewList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        readReview(new BusinessShopReviewPreview.ReviewCallback() {
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
                            skeletonScreen.hide();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    interface ReviewCallback {
        void onCallback(ShopReviewModel reviewModel);
    }

    private int allRateCount = 0;
    private void readReview(final BusinessShopReviewPreview.ReviewCallback reviewCallback) {

        skeletonScreen = Skeleton.bind(reviewRecyclerView)
                .adapter(reviewAdapter)
                .load(R.layout.layout_default_item_skeleton)
                .show();


        databaseReference.child("businesses").orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){


                    databaseReference.child("businesses").child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            allRateCount = (int) dataSnapshot.getChildrenCount();
                            overallRateCountTxt.setText(String.valueOf(allRateCount));

                            if(dataSnapshot.getChildrenCount()==0){
                                skeletonScreen.hide();
                            }

                            for (final DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {

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
        databaseReference.child("businesses").orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object> hm = new HashMap<String, Object>();
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
