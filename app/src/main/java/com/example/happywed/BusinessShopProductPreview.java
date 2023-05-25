package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.happywed.Adapters.ProductViewAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BusinessShopProductPreview extends AppCompatActivity {

    private Toolbar toolBar;
    private ViewPager viewPager;
    private LinearLayout dotLinear;
    private TextView dots[];
    private TextView productNameTxt,productPriceTxt,productDetailTxt;

    private ProductViewAdapter productViewAdapter;
    private ArrayList<String> imageList =new ArrayList<String>();

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;


    private BusinessProductModel productModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_shop_product_preview);

        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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

        dotLinear = (LinearLayout) findViewById(R.id.productDot);
        viewPager = (ViewPager) findViewById(R.id.productImageView);
        productNameTxt = (TextView) findViewById(R.id.productName);
        productPriceTxt = (TextView) findViewById(R.id.productPrice);
        productDetailTxt = (TextView) findViewById(R.id.productDetail);

        productViewAdapter = new ProductViewAdapter(imageList, this);
        viewPager.setAdapter(productViewAdapter);
        viewPager.setOnPageChangeListener(viewChange);
        addDots(0);

        productModel = (BusinessProductModel) getIntent().getSerializableExtra("productModel");

        imageList.clear();
        for(String img: productModel.getProductImages()){
            imageList.add(img);
            productViewAdapter.notifyDataSetChanged();
        }

        productNameTxt.setText(productModel.getProductName().trim());
        productPriceTxt.setText("LKR "+productModel.getProductPrice().trim());
        productDetailTxt.setText(productModel.getProductDescription().trim());

    }

    public void addDots(int position){
        dots = new TextView[imageList.size()];
        dotLinear.removeAllViews();

        for (int i=0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.ash));

            dotLinear.addView(dots[i]);
        }

        if (dots.length>0){
            dots[position].setTextColor(getResources().getColor(R.color.black));
        }

    }


    ViewPager.OnPageChangeListener viewChange = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDots(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };



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
