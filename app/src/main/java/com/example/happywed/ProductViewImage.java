package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.happywed.Adapters.ProductViewAdapter;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessProductModel;
import com.example.happywed.Models.CommonCategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductViewImage extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private Toolbar toolBar;
    private ViewPager viewPager;
    private LinearLayout dotLinear;
    private TextView dots[];
    private TextView productNameTxt,productPriceTxt,productDetailTxt,goToShopTxt;
    private ImageView productFav;

    private ProductViewAdapter productViewAdapter;
    private ArrayList<String> imageList =new ArrayList<String>();

    private BusinessProductModel productModel;

    private boolean processFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_view_image);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null){
            setSupportActionBar(toolBar);
        }
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

        dotLinear = (LinearLayout) findViewById(R.id.productDot);
        viewPager = (ViewPager) findViewById(R.id.productImageView);
        productNameTxt = (TextView) findViewById(R.id.productName);
        productPriceTxt = (TextView) findViewById(R.id.productPrice);
        productDetailTxt = (TextView) findViewById(R.id.productDetail);
        goToShopTxt = (TextView) findViewById(R.id.goToShop);
        productFav = (ImageView) findViewById(R.id.productFav);

        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        productModel = (BusinessProductModel) getIntent().getSerializableExtra("productModel");

        imageList.clear();
        productViewAdapter = new ProductViewAdapter(imageList, this);
        viewPager.setAdapter(productViewAdapter);
        viewPager.setOnPageChangeListener(viewChange);
        addDots(0);

        databaseReference.child(productModel.getOwnerKey()).child("Shops").child(productModel.getBusinessKey()).child("Products").child(productModel.getProductKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                databaseReference.child(productModel.getOwnerKey()).child("Shops").child(productModel.getBusinessKey()).child("Products").child(productModel.getProductKey()).child("Favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(currentProfile.getUid())) {
                            productFav.setImageResource(R.drawable.favourite);
                            productFav.setTag("fav");
                        } else {
                            productFav.setImageResource(R.drawable.unfavourite);
                            productFav.setTag("unFav");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ArrayList<String> productImgList = ((Map<String, ArrayList<String>>) dataSnapshot.getValue()).get("productImages");
                String productName = ((Map<String, String>) dataSnapshot.getValue()).get("productName");
                String productDescription = ((Map<String, String>) dataSnapshot.getValue()).get("productDescription");
                String productPrice = ((Map<String, String>) dataSnapshot.getValue()).get("productPrice");

//                imageList.add(productModel.getProductImage());
                for(String img: productImgList){
//                    if(!img.equals(productModel.getProductImage())){
                        imageList.add(img);
                        productViewAdapter.notifyDataSetChanged();
//                    }
                    productViewAdapter.notifyDataSetChanged();
                }

                productNameTxt.setText(productName);
                productDetailTxt.setText(productDescription);
                productPriceTxt.setText("LKR "+productPrice);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        goToShopTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BusinessProfile.class).putExtra("ownerKey",productModel.getOwnerKey().trim()).putExtra("shopKey",productModel.getBusinessKey().trim()));
            }
        });


        productFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                processFav = true;

                databaseReference.child(productModel.getOwnerKey()).child("Shops").child(productModel.getBusinessKey()).child("Products").child(productModel.getProductKey()).child("Favorite").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(processFav) {
                            if (dataSnapshot.hasChild(currentProfile.getUid())) {
                                processFav = false;
                                databaseReference.child(productModel.getOwnerKey()).child("Shops").child(productModel.getBusinessKey()).child("Products").child(productModel.getProductKey()).child("Favorite").child(currentProfile.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Snackbar.make(view,"Item Removed from WishList",Snackbar.LENGTH_LONG).show();
                                            productFav.setImageResource(R.drawable.unfavourite);
                                            productFav.setTag("unFav");
                                        }
                                    }
                                });
                            } else {
                                processFav = false;
                                databaseReference.child(productModel.getOwnerKey()).child("Shops").child(productModel.getBusinessKey()).child("Products").child(productModel.getProductKey()).child("Favorite").child(currentProfile.getUid()).setValue("random value").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Snackbar.make(view,"Item Added to WishList",Snackbar.LENGTH_LONG).show();
                                            productFav.setImageResource(R.drawable.favourite);
                                            productFav.setTag("fav");
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
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.wishlist_menu, menu);

        return true;
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
        HappyWedDB.getDBConnection().child("users").orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    hm.put("status", status);

                    HappyWedDB.getDBConnection().child("users").child(snaps.getKey()).updateChildren(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
