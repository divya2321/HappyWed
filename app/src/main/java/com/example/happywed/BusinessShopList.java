package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.example.happywed.Adapters.BusinessCategoryPopupAdapter;
import com.example.happywed.Adapters.BusinessShopListAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.Shop;
import com.example.happywed.Models.BusinessShopModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class BusinessShopList extends AppCompatActivity {


    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private Animation closeEmptyNote, openEmptyNote;
    private ImageView shopImage;
    private TextView oopsText;
    private RecyclerView businessShopList;

    private BusinessShopListAdapter businessShopListAdapter;
    private ArrayList<BusinessShopModel> businessShopModels = new ArrayList<BusinessShopModel>();

    private RecyclerViewSkeletonScreen skeletonScreen;

    private Dialog popDialog;
    private ProgressDialog loadingDialog;

    private Toolbar toolBar;
    private RecyclerView businessCategoryList;
    private Button popupSave;
    private ImageView popupCancel;
    private EditText businessName;

    private ArrayList<String> categories= new ArrayList<String>();
    private BusinessCategoryPopupAdapter categoryPopupAdapter;

    private String shopName;
    private ArrayList<String> categoryList;
    private String allCategories= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_shop_list);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        popDialog = new Dialog(this);

        loadingDialog = new ProgressDialog(BusinessShopList.this);
        loadingDialog.setCancelable(false);
        loadingDialog.setTitle("Uploading...");


        shopImage = (ImageView) findViewById(R.id.shopImage);
        oopsText = (TextView) findViewById(R.id.oops_shop);
        businessShopList = (RecyclerView) findViewById(R.id.businessShopList);
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

        closeEmptyNote = AnimationUtils.loadAnimation(this, R.anim.close_empty_note);
        openEmptyNote = AnimationUtils.loadAnimation(this, R.anim.open_empty_note);


        businessShopModels.clear();

        businessShopListAdapter = new BusinessShopListAdapter(this, businessShopModels);
        businessShopList.setLayoutManager(new LinearLayoutManager(this));
        businessShopList.setAdapter(businessShopListAdapter);

        shopImage.setVisibility(View.GONE);
        oopsText.setVisibility(View.GONE);

        skeletonScreen = Skeleton.bind(businessShopList)
                .adapter(businessShopListAdapter)
                .load(R.layout.layout_default_item_skeleton)
                .show();

        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {

                    databaseReference.child(childSnapshot.getKey()).child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {
                                shopImage.setVisibility(View.GONE);
                                oopsText.setVisibility(View.GONE);

                                for (DataSnapshot childSnapsho1 : dataSnapshot.getChildren()) {

                                    BusinessShopModel shopModel = new BusinessShopModel();

                                    String shop = ((Map<String, String>) childSnapsho1.getValue()).get("shopName");
                                    ArrayList<String> shopsListCategories = ((Map<String, ArrayList<String>>) childSnapsho1.getValue()).get("shopCategories");

                                    String profilePic = ((Map<String, String>) childSnapsho1.getValue()).get("profilePic");


                                    String allCategory = "";

                                    for (String shc : shopsListCategories) {
                                        allCategory += shc+" ";
                                    }

                                    shopModel.setBusinessKey(childSnapsho1.getKey());
                                    shopModel.setBusinessName(shop);
                                    shopModel.setBusinessCategory(allCategory);
                                    shopModel.setBusinessProfilePic(profilePic);

                                    businessShopModels.add(shopModel);
                                    businessShopListAdapter.notifyDataSetChanged();
                                    skeletonScreen.hide();
                                }

                            }else{
                                skeletonScreen.hide();
                                shopImage.startAnimation(openEmptyNote);
                                shopImage.setVisibility(View.VISIBLE);
                                oopsText.setVisibility(View.VISIBLE);
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




        popDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popDialog.setContentView(R.layout.business_shop_create_popup);

        businessCategoryList = (RecyclerView) popDialog.findViewById(R.id.businessCategoryList);
        popupCancel = (ImageView) popDialog.findViewById(R.id.cancel);
        popupSave = (Button) popDialog.findViewById(R.id.popupSave);
        businessName = (EditText) popDialog.findViewById(R.id.businessName) ;

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

        categoryPopupAdapter = new BusinessCategoryPopupAdapter(this,categories);
        businessCategoryList.setLayoutManager(new LinearLayoutManager(this));
        businessCategoryList.setAdapter(categoryPopupAdapter);

        popupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDialog.dismiss();
                businessName.setText(null);
                categoryPopupAdapter.checkedItem.clear();

                if (businessShopList.getAdapter().getItemCount() == 0) {

                    shopImage.startAnimation(openEmptyNote);
                    shopImage.setVisibility(View.VISIBLE);
                    oopsText.setVisibility(View.VISIBLE);

                }else {
                    shopImage.setVisibility(View.GONE);
                    oopsText.setVisibility(View.GONE);

                }

            }
        });


        popupSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shopName = businessName.getText().toString().trim();
                categoryList = new ArrayList<String>();

                categoryList.clear();
                allCategories = "";

                for (int i = 0; i < categoryPopupAdapter.checkedItem.size(); i++) {
                    categoryList.add(categoryPopupAdapter.checkedItem.get(i).trim());

                    if (i != categoryPopupAdapter.checkedItem.size() - 1) {
                        allCategories += categoryPopupAdapter.checkedItem.get(i).trim() + "/";
                    } else {
                        allCategories += categoryPopupAdapter.checkedItem.get(i).trim();
                    }
                }

                if((!shopName.isEmpty()) && categoryList.size() != 0){

                    loadingDialog.show();
                    skeletonScreen.show();

                    databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                                Shop shopModel = new Shop()
                                        .setShopName(shopName)
                                        .setShopCategories(categoryList)
                                        .setConfirmation("0")
                                        .setStatus("1");

                                final DatabaseReference dbRef =   databaseReference.child(childDataSnapshot.getKey()).child("Shops").push();

                                dbRef.setValue(shopModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        if (task.isSuccessful()) {

                                            BusinessShopModel bsModel =  new BusinessShopModel()
                                                    .setBusinessKey(dbRef.getKey())
                                                    .setBusinessProfilePic(null)
                                                    .setBusinessName(shopName)
                                                    .setBusinessCategory(allCategories);

                                            businessShopModels.add(bsModel);
                                            businessShopListAdapter.notifyDataSetChanged();
                                            popDialog.dismiss();
                                            loadingDialog.dismiss();
                                            skeletonScreen.hide();

                                            businessName.setText(null);
                                            categoryPopupAdapter.checkedItem.clear();
                                            categoryPopupAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(BusinessShopList.this, "Something went wrong please try again!", Toast.LENGTH_LONG).show();
                                            loadingDialog.dismiss();
                                            skeletonScreen.hide();
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
                    Toast.makeText(BusinessShopList.this, "Full fill all details", Toast.LENGTH_LONG).show();
                }



            }
        });

    }


    public void godoClickAdd(View view) {

        if (businessShopList.getAdapter().getItemCount() == 0) {
            shopImage.startAnimation(closeEmptyNote);
            oopsText.startAnimation(closeEmptyNote);

            shopImage.setVisibility(View.GONE);
            oopsText.setVisibility(View.GONE);
        }else {
            shopImage.setVisibility(View.GONE);
            oopsText.setVisibility(View.GONE);
        }
        popDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (businessShopList.getAdapter().getItemCount() == 0) {

            shopImage.startAnimation(openEmptyNote);
            shopImage.setVisibility(View.VISIBLE);
            oopsText.setVisibility(View.VISIBLE);

        }else {
            shopImage.setVisibility(View.GONE);
            oopsText.setVisibility(View.GONE);

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
        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
