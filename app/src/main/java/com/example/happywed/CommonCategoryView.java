package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.example.happywed.Adapters.CommonCategoryAdapter;
import com.example.happywed.Adapters.CommonCategoryListAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessProductModel;
import com.example.happywed.Models.BusinessModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommonCategoryView extends AppCompatActivity {


    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private RecyclerView categoryRecycler ;
    private static Spinner viewAsSpinner;
    public static Button filterLocationBtn;
    private SearchView itemSearchView;
    private Toolbar toolBar;

    private static CommonCategoryAdapter productAdapter ;
    private ArrayList<BusinessProductModel> productModelList = new ArrayList<BusinessProductModel>();
    public static ArrayList<BusinessProductModel> productModelListAll = new ArrayList<BusinessProductModel>();
    public static ArrayList<BusinessProductModel> pmListAll = new ArrayList<BusinessProductModel>();

    private static CommonCategoryListAdapter businessAdapter ;
    private ArrayList<BusinessModel> businessModelList = new ArrayList<BusinessModel>();
    public static ArrayList<BusinessModel> businessModelListAll = new ArrayList<BusinessModel>();
    public static ArrayList<BusinessModel> bmListAll = new ArrayList<BusinessModel>();

    private RecyclerViewSkeletonScreen skeletonScreen;

    private String selectedCategory;

    private ArrayList<String> allProductImages = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_category_view);

        selectedCategory = getIntent().getStringExtra("category");

        toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);
        toolBar.setTitle(selectedCategory);
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


        categoryRecycler = (RecyclerView) findViewById(R.id.categoryRecycler);
        viewAsSpinner = (Spinner) findViewById(R.id.filterViewAs);
        filterLocationBtn = (Button) findViewById(R.id.filterLocation);
        itemSearchView = (SearchView) findViewById(R.id.itemSearchView);


        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        productAdapter = new CommonCategoryAdapter(this, productModelList);

        businessAdapter = new CommonCategoryListAdapter(this, businessModelList);






        viewAsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String viewOption = adapterView.getItemAtPosition(i).toString();
                if (viewOption.equalsIgnoreCase("View as Image")){
                    itemSearchView.setQueryHint(getResources().getString(R.string.search_for_product));
                    gotoImageList();

                    itemSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            productAdapter.getFilter().filter(s);
                            return false;
                        }
                    });
                }else if (viewOption.equalsIgnoreCase("View as Business")){
                    itemSearchView.setQueryHint(getResources().getString(R.string.search_for_shop));
                    gotoBusinessList();

                    itemSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            businessAdapter.getFilter().filter(s);
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(viewAsSpinner.getSelectedItem().equals("View as Image")) {


            itemSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
//                    productAdapter.getFilter().filter(s);
                    return false;
                }
            });

        }else{


        }

    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.wishlist_menu, menu);

        return true;
    }


    public void gotoImageList(){

        productModelList.clear();
        productModelListAll.clear();
        pmListAll.clear();
        categoryRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        categoryRecycler.setAdapter(productAdapter);
        databaseReference.orderByChild("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot childDataSnapshot1 : dataSnapshot.getChildren()){

                    databaseReference.child(childDataSnapshot1.getKey()).child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot childDataSnapshot2 : dataSnapshot.getChildren()) {

                                final String shopKey = childDataSnapshot2.getKey();
                                String confirmation = ((Map<String, String>) childDataSnapshot2.getValue()).get("confirmation");
                                String status = ((Map<String, String>) childDataSnapshot2.getValue()).get("status");
                                String address = ((Map<String, String>) childDataSnapshot2.getValue()).get("address");
                                final String district = ((Map<String, String>) childDataSnapshot2.getValue()).get("district");



                                if((confirmation.equals("1")) && (status.equals("1"))){

                                    String allAdd[] = address.trim().split(",");
                                    final String city = allAdd[allAdd.length - 1];

                                databaseReference.child(childDataSnapshot1.getKey()).child("Shops").child(shopKey).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot childDataSnapshot3 : dataSnapshot.getChildren()) {

                                            ArrayList<String> productCategory = ((Map<String, ArrayList<String>>) childDataSnapshot3.getValue()).get("productCategories");

                                            for (String category : productCategory) {
                                                if (selectedCategory.equalsIgnoreCase(category)) {

                                                    ArrayList<String> productImages = ((Map<String, ArrayList<String>>) childDataSnapshot3.getValue()).get("productImages");

                                                    String productKey = childDataSnapshot3.getKey();
                                                    final String productName = ((Map<String, String>) childDataSnapshot3.getValue()).get("productName");


//                                                    for(String img: productImages){
                                                    final BusinessProductModel productModel = new BusinessProductModel()
                                                            .setOwnerKey(childDataSnapshot1.getKey())
                                                            .setBusinessKey(shopKey)
                                                            .setProductKey(productKey)
                                                            .setUid(currentProfile.getUid())
                                                            .setMainProductImage(productImages.get(0))
                                                            .setProductName(productName)
                                                            .setBusinessCity(city)
                                                            .setBusinessDistrict(district);


                                                    databaseReference.child(childDataSnapshot1.getKey()).child("Shops").child(shopKey).child("Products").child(productKey).child("Favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            if (dataSnapshot.hasChild(currentProfile.getUid())) {

                                                                productModel.setFav(true);
                                                                productModelList.add(productModel);
                                                                productModelListAll.add(productModel);
                                                                pmListAll.add(productModel);
                                                                productAdapter.notifyDataSetChanged();
                                                            } else {
                                                                productModel.setFav(false);
                                                                productModelList.add(productModel);
                                                                productModelListAll.add(productModel);
                                                                pmListAll.add(productModel);
                                                                productAdapter.notifyDataSetChanged();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                }
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void gotoBusinessList() {

        businessModelList.clear();
        businessModelListAll.clear();
        bmListAll.clear();
        readBusiness(new BusinessCallback(){

            @Override
            public void onCallback(final BusinessModel model) {
                databaseReference.child(model.getOwnerKey()).child("Shops").child(model.getBusinessKey()).child("Reviews").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        allRateCount = (int) dataSnapshot.getChildrenCount();
                        if(allRateCount == 0){
                            model.setRate(0.0f);
                            businessModelList.add(model);
                            businessModelListAll.add(model);
                            bmListAll.add(model);
                            businessAdapter.notifyDataSetChanged();
                            skeletonScreen.hide();
                        }else {
                            double overallCount = 0.0;
                            for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                                String rate = ((Map<String, String>) childSnapshot1.getValue()).get("rate");
                                allRateSum += Float.parseFloat(rate);
                                overallCount = Math.round(allRateSum / allRateCount * 10) / 10.0;
                            }
                            model.setRate((float) overallCount);
                            businessModelList.add(model);
                            businessModelListAll.add(model);
                            bmListAll.add(model);
                            businessAdapter.notifyDataSetChanged();
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


    private float allRateSum = 0;
    int allRateCount = 0;

    interface BusinessCallback {
        void onCallback(BusinessModel model);
    }


    long snapshotCount = 0;
    long snapshotState = 0;


    private void readBusiness(final CommonCategoryView.BusinessCallback reviewCallback) {


        categoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        categoryRecycler.setAdapter(businessAdapter);
        skeletonScreen = Skeleton.bind(categoryRecycler)
                .adapter(businessAdapter)
                .load(R.layout.layout_default_item_skeleton)
                .show();
        databaseReference.orderByChild("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot childDataSnapshot1 : dataSnapshot.getChildren()){

                    databaseReference.child(childDataSnapshot1.getKey()).child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            snapshotCount = dataSnapshot.getChildrenCount();

                            if(snapshotCount==0){
                                skeletonScreen.hide();
                            }

                            for(DataSnapshot childDataSnapshot2 : dataSnapshot.getChildren()) {
                                snapshotState++;

                                String confirmation = ((Map<String, String>) childDataSnapshot2.getValue()).get("confirmation");
                                String status = ((Map<String, String>) childDataSnapshot2.getValue()).get("status");

                                if((confirmation.equals("1")) && (status.equals("1"))){

                                ArrayList<String> shopCategories = ((Map<String, ArrayList<String>>) childDataSnapshot2.getValue()).get("shopCategories");

                                for (String category : shopCategories) {
                                    if (selectedCategory.equalsIgnoreCase(category)) {

                                        String shopKey = childDataSnapshot2.getKey();
                                        String shopName = ((Map<String, String>) childDataSnapshot2.getValue()).get("shopName");
                                        String profilePic = ((Map<String, String>) childDataSnapshot2.getValue()).get("profilePic");
                                        String address = ((Map<String, String>) childDataSnapshot2.getValue()).get("address");
                                        String district = ((Map<String, String>) childDataSnapshot2.getValue()).get("district");

                                        String allAdd[] = address.trim().split(",");
                                        String city = allAdd[allAdd.length - 1];

                                        final BusinessModel businessModel = new BusinessModel()
                                                .setOwnerKey(childDataSnapshot1.getKey())
                                                .setBusinessKey(shopKey)
                                                .setBusinessProPic(profilePic)
                                                .setBusinessName(shopName)
                                                .setUid(currentProfile.getUid())
                                                .setBusinessCity(city)
                                                .setBusinessDistrict(district);

                                        databaseReference.child(childDataSnapshot1.getKey()).child("Shops").child(shopKey).child("Favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild(currentProfile.getUid())) {

                                                    businessModel.setFav(true);
                                                    reviewCallback.onCallback(businessModel);
                                                } else {
                                                    businessModel.setFav(false);
                                                    reviewCallback.onCallback(businessModel);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }

                            }

                                if(snapshotState==snapshotCount-1){

                                    if(businessModelList.isEmpty()){
                                        skeletonScreen.hide();
                                    }
                                }
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

    public void gotoLocation(View view) {

        startActivity(new Intent(this, LocationExpand.class));

//        categoryRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        categoryRecycler.setAdapter(commonCategoryAdapter);


    }

    private String getRandomProductImg() {
//        for(){
//
//        }
        return allProductImages.get(new Random().nextInt(allProductImages.size()));
    }

    public static void flterLocation(String location) {
        filterLocationBtn.setText(location);

        if(viewAsSpinner.getSelectedItem().toString().equals("View as Image")) {
            Log.d("abc", "View as Image"+" "+viewAsSpinner.getSelectedItem().toString());
            Log.d("abc", location);
            switch (location) {
                case "All of Sri Lanka":
                    productAdapter.modelList.clear();

                    for (BusinessProductModel modelItem : pmListAll) {
                        productAdapter.modelList.add(modelItem);
                    }
                    productAdapter.selectedLocation = "All of Sri Lanka";
                    productAdapter.notifyDataSetChanged();
                    break;

                case "Gampaha":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Gampaha") || city.equalsIgnoreCase("Gampaha")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Gampaha";
                    break;

                case "Biyagama":
                    Log.d("abc", location);
                    Log.d("abc", productAdapter.modelList.size() + "");
                    productAdapter.modelList.clear();
                    Log.d("abc", productAdapter.modelList.size() + "");
                    for (BusinessProductModel modelItem : productModelListAll) {
                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();
                        Log.d("abc", city + " " + distict);

                        if (distict.equalsIgnoreCase("Biyagama") || city.equalsIgnoreCase("Biyagama")) {
                            Log.d("abc", modelItem.getProductName());
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Biyagama";
                    break;

                case "Delgoda":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Delgoda") || city.equalsIgnoreCase("Delgoda")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Delgoda";
                    break;

                case "Weliveriya":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Weliveriya") || city.equalsIgnoreCase("Weliveriya")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Weliveriya";
                    break;


                case "Colombo":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Colombo") || city.equalsIgnoreCase("Colombo")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Colombo";
                    break;

                case "Piliyandala":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Piliyandala") || city.equalsIgnoreCase("Piliyandala")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Piliyandala";
                    break;

                case "Maharagama":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Maharagama") || city.equalsIgnoreCase("Maharagama")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Maharagama";
                    break;

                case "Boralesgamuwa":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Boralesgamuwa") || city.equalsIgnoreCase("Boralesgamuwa")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Boralesgamuwa";
                    break;

                case "Nugegoda":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Nugegoda") || city.equalsIgnoreCase("Nugegoda")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Nugegoda";
                    break;

                case "Kaduwela":
                    productAdapter.modelList.clear();
                    for (BusinessProductModel modelItem : productModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Kaduwela") || city.equalsIgnoreCase("Kaduwela")) {
                            productAdapter.modelList.add(modelItem);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                    productAdapter.selectedLocation = "Kaduwela";
                    break;

            }
        }else{
            Log.d("abc", "View as business"+" "+viewAsSpinner.getSelectedItem().toString());

            switch (location) {

                case "All of Sri Lanka":
                    businessAdapter.modelList.clear();

                    for (BusinessModel modelItem : bmListAll) {
                        businessAdapter.modelList.add(modelItem);
                    }
                    businessAdapter.selectedLocation = "All of Sri Lanka";
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.notifyDataSetChanged();
                    break;

                case "Gampaha":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Gampaha") || city.equalsIgnoreCase("Gampaha")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Gampaha";
                    break;

                case "Biyagama":
                    businessAdapter.modelList.clear();

                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Biyagama") || city.equalsIgnoreCase("Biyagama")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Biyagama";
                    break;

                case "Delgoda":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Delgoda") || city.equalsIgnoreCase("Delgoda")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Delgoda";
                    break;

                case "Weliveriya":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Weliveriya") || city.equalsIgnoreCase("Weliveriya")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Weliveriya";
                    break;


                case "Colombo":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Colombo") || city.equalsIgnoreCase("Colombo")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Colombo";
                    break;

                case "Piliyandala":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Piliyandala") || city.equalsIgnoreCase("Piliyandala")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Piliyandala";
                    break;

                case "Maharagama":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Maharagama") || city.equalsIgnoreCase("Maharagama")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Maharagama";
                    break;

                case "Boralesgamuwa":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Boralesgamuwa") || city.equalsIgnoreCase("Boralesgamuwa")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Boralesgamuwa";
                    break;

                case "Nugegoda":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Nugegoda") || city.equalsIgnoreCase("Nugegoda")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Nugegoda";
                    break;

                case "Kaduwela":
                    businessAdapter.modelList.clear();
                    for (BusinessModel modelItem : businessModelListAll) {

                        String distict = modelItem.getBusinessDistrict();
                        String city = modelItem.getBusinessCity();

                        if (distict.equalsIgnoreCase("Kaduwela") || city.equalsIgnoreCase("Kaduwela")) {
                            businessAdapter.modelList.add(modelItem);
                        }
                        businessAdapter.notifyDataSetChanged();
                    }
//                businessModelListAll.clear();
//                businessModelListAll.addAll(businessAdapter.modelList);
                    businessAdapter.selectedLocation = "Kaduwela";
                    break;

            }

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
